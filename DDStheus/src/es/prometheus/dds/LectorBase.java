/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Prometheus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
 
package es.prometheus.dds;

import com.rti.dds.dynamicdata.DynamicData;
import com.rti.dds.dynamicdata.DynamicDataReader;
import com.rti.dds.dynamicdata.DynamicDataSeq;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusCondition;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.ContentFilteredTopic;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.UUID;

/**
 * Clase abstracta para recibir datos dinámicos de un tópico y filtrarlos según un key.
 */
public abstract class LectorBase {
    private final TopicoControl control;
    private final ContentFilteredTopic topico;
    private final DataCallback callback;
    private final Thread dataThread;

    private DynamicDataReader reader;

    /**
     * Crea una base de lector.
     *
     * @param control Control de tópico actual.
     * @param expresion Expresión para la condición al discernir los datos.
     * @param params Parámetros de la expresión del filtro.
     */
    protected LectorBase(final TopicoControl control, final String expresion,
            final String[] params) {
        this.control = control;

        // Crea el tópico con filtro de datos.
        this.topico = this.control.createCFT(UUID.randomUUID().toString(), expresion, params);

        // Crea el lector sobre ese filtro, de esta forma sólo se reciben
        // los datos necesarios y se reduce ancho de banda.
        DataReaderQos qos = new DataReaderQos();
        this.control.getParticipante().get_default_datareader_qos(qos);
        this.reader = this.creaLector(qos);

        // Craeamos una nueva hebra para recibir los datos de forma síncrona.
        this.callback = new DataCallback(this.reader, 0x5000);
        this.dataThread = new Thread(this.callback);
    }

    /**
     * Libera los recursos del lector.
     */
    public void dispose() {
        // Paramos de recibir datos
        this.callback.terminar();
        try { this.dataThread.join(5000); }
        catch (InterruptedException e) { System.err.println("TimeOver!"); }

        // Eliminamos los datos
        this.reader.delete_contained_entities();
        this.control.eliminaLector(this.reader);
    }

    /**
     * Añade un listener extra que se llamará después de parsear los datos recibidos.
     * Para desactivarlo establecer a null.
     *
     * @param listener Listener externo.
     */
    public void setExtraListener(final ActionListener listener) {
        this.callback.setExtraListener(listener);
    }

    /**
     * Cambia los parámetros de la expresión de filtro del lector.
     *
     * @param params Nuevos parámetros.
     */
    public final void cambioParametros(final String[] params) {
        // Cambia los parámetros del tópico
        this.topico.set_expression_parameters(new StringSeq(Arrays.asList(params)));
    }

    /**
     * Obtiene el control de tópico actual.
     *
     * @return Control de tópico.
     */
    public TopicoControl getTopicoControl() {
        return this.control;
    }

    /**
     * Obtiene la hebra que está obteniendo datos del lector.
     * Útil para dejar otra hebra bloqueada junto a esta.
     *
     * @return Hebra que recibe datos de forma síncrona.
     */
    public Thread getCallbackThread() {
        return this.dataThread;
    }

    /**
     * Comienza a recibir datos
     */
    public void iniciar() {
        if (!this.dataThread.isAlive())
            this.dataThread.start();
    }

    /**
     * Para de recibir datos de DDS.
     */
    public void suspender() {
        this.callback.suspender();
    }

    /**
     * Continua con la recepción de datos de DDS.
     */
    public void reanudar() {
        this.callback.reanudar();
    }

    /**
     * Método que se llama para sacar los datos recibidos de la muestra.
     *
     * @param sample Muestra recibida con datos.
     */
    protected abstract void getDatos(DynamicData sample);

    /**
     * Crea un lector a partir del tópico con filtro y con QOS.
     *
     * @param qos QOS a usar.
     * @return Nuevo lector.
     */
    private DynamicDataReader creaLector(final DataReaderQos qos) {
        // Crea el lector
        return (DynamicDataReader)this.control.getParticipante().create_datareader(
                this.topico,
                qos,
                null,
                StatusKind.DATA_AVAILABLE_STATUS);
    }

    /**
     * Clase para implementar la recepción de datos de DDS con condiciones de
     * forma síncrona.
     * Esta solución es necesaria porque usando listener, el listener de un
     * DataReader no puede modificar otro DataReader (como sus condiciones).
     *
     * Más información aquí:
     * http://community.rti.com/kb/how-can-i-prevent-deadlocks-while-invoking-rti-apis-listener
     * http://community.rti.com/kb/what-does-exclusive-area-error-message-mean
     */
    private class DataCallback implements Runnable {
        private static final int MAX_TIME_SEC  = 3;
        private static final int MAX_TIME_NANO = 0;

        private DynamicDataReader reader;
        private final WaitSet waitset;
        private final Duration_t duracion;
        private final int mask;

        private ActionListener extraListener;
        private boolean procesar;
        private boolean terminar;

        /**
         * Crea una nueva instancia para un lector con condición dada.
         *
         * @param reader Lector del que recibir datos.
         * @param condicion Condición a aplicar sobre los datos.
         */
        public DataCallback(final DynamicDataReader reader, final int mask) {
            this.procesar = true;
            this.terminar = false;

            this.reader    = reader;
            this.mask      = mask;
            this.duracion  = new Duration_t(MAX_TIME_SEC, MAX_TIME_NANO);
            this.waitset   = new WaitSet();

            // Le añado el StatusCondition de condición
            StatusCondition condicion = reader.get_statuscondition();
            condicion.set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
            this.waitset.attach_condition(condicion);
        }

        @Override
        public void run() {
            while (!this.terminar) {
                // Si estamos paramos, omitimos los datos recibidos.
                while (!this.procesar) {
                    synchronized (this) {
                        try { this.wait(); }
                        catch (InterruptedException ex) { }
                    }
                }

                // Esperamos a obtener la siguiente muestra que cumpla la condición
                ConditionSeq activadas = new ConditionSeq();
                try { this.waitset.wait(activadas, duracion); }
                catch (RETCODE_TIMEOUT e) { continue; }

                // Compruebo que se haya disparado por la condición que queremos
		if (activadas.size() == 0 || (reader.get_status_changes() & StatusKind.DATA_AVAILABLE_STATUS) == 0)
                    continue;

                // Procesamos los datos recibidos.
                this.processData();
            }
        }

        /**
         * Procesa los datos recibidos de DDS.
         */
        private void processData() {
            // Obtiene todos los sample de DDS
            DynamicDataSeq dataSeq = new DynamicDataSeq();
            SampleInfoSeq infoSeq = new SampleInfoSeq();

            try {
                // Obtiene datos aplicandole el filtro
                this.reader.take(
                    dataSeq,
                    infoSeq,
                    ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                    SampleStateKind.ANY_SAMPLE_STATE,
                    ViewStateKind.ANY_VIEW_STATE,
                    InstanceStateKind.ANY_INSTANCE_STATE
                );

                // Procesamos todos los datos recibidos
                for (int i = 0; i < dataSeq.size(); i++) {
                    SampleInfo info = (SampleInfo)infoSeq.get(i);

                    // En caso de que sea meta-data del tópico
                    if (!info.valid_data)
                        continue;

                    // Deserializa los datos
                    DynamicData sample = (DynamicData)dataSeq.get(i);
                    getDatos(sample);

                    // Llama al listener externo
                    if (this.extraListener != null)
                        this.extraListener.actionPerformed(null);
                }
            } catch (RETCODE_NO_DATA e) {
                // No hace nada, al filtrar datos pues se da la cosa de que no haya
            } finally {
                // Es para liberar recursos del sistema.
                this.reader.return_loan(dataSeq, infoSeq);
            }
        }

        /**
         * Deja de procesar los datos que recibe.
         */
        public synchronized void suspender() {
            this.procesar = false;
            this.notifyAll();
        }

        /**
         * Comienza a procesar los datos recibidos de nuevo.
         */
        public synchronized void reanudar() {
            this.procesar = true;
            this.notifyAll();
        }

        /**
         * Termina la ejecución en la próxima iteración.
         */
        public synchronized void terminar() {
            this.terminar = true;
            this.procesar = true;
            this.notifyAll();
        }

        /**
         * Establece un listener extra para cuando se reciben los datos.
         *
         * @param listener Listener extra.
         */
        public void setExtraListener(final ActionListener listener) {
            this.extraListener = listener;
        }

        /**
         * Cambia el lector para obtener datos.
         *
         * @param reader Nuevo lector.
         */
        public void cambiaReader(final DynamicDataReader reader) {
            // Elimina la condición anterior
            this.waitset.detach_condition(this.reader.get_statuscondition());

            // Añade la nueva condición y cambia de reader
            this.reader = reader;
            this.waitset.attach_condition(reader.get_statuscondition());
        }
    }
}
