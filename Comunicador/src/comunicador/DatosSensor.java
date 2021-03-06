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
 
package comunicador;

import com.rti.dds.dynamicdata.DynamicData;
import es.prometheus.dds.Escritor;
import java.util.Date;
import org.w3c.dom.Element;

/**
 * Esctructura de datos recibido de los sensores.
 */
class DatosSensor {
    /** ID del escritor del dato. */
    private String id;

    /** Nombre de la sala donde está el sensor. */
    private String sala;

    /** Posición del sensor Bluetooth. */
    private Par<Double, Double> sensorPos;

    /** ID del niño. */
    private String ninoId;

    /** Intensidad de la señal (RSSI). */
    private double intensidad;

    /** Fecha de creación del dato. */
    private long creacion;

    /**
     * Instancia vacía.
     */
    private DatosSensor() {
    }

    /**
     * Constructor para la clase DatosSensor.
     *
     * @param id ID del sensor emisor
     * @param sala Nombre de la sala
     * @param posX Posición X del sensor
     * @param posY Posición Y del sensor
     * @param ninoId ID del niño
     * @param intensidad Intensidad de señal
     */
    public DatosSensor(String id, String sala, double posX, double posY,
            String ninoId, double intensidad) {
        this.id         = id;
        this.sala       = sala;
        this.sensorPos  = new Par<>(posX, posY);
        this.ninoId     = ninoId;
        this.intensidad = intensidad;
        this.creacion   = new Date().getTime();
    }

    /**
     * Constructor para la clase DatosSensor.
     *
     * @param id ID del sensor emisor
     * @param sala Nombre de la sala
     * @param sensorPos Posición del sensor
     * @param IDnino ID del niño
     * @param intensidad Intensidad de la señal
     */
    public DatosSensor(String id, String sala, Par<Double, Double> sensorPos,
            String ninoId, double intensidad) {
        this.id         = id;
        this.sala       = sala;
        this.sensorPos  = sensorPos;
        this.ninoId     = ninoId;
        this.intensidad = intensidad;
        this.creacion   = new Date().getTime();
    }

    /**
     * Constructor para la clase DatosSensor.
     *
     * @param mensaje String con los datos de la clase.
     */
    public DatosSensor(String mensaje) {
        String[] fields = mensaje.split(" ");
        this.id   = fields[0];
        this.sala = fields[1];
        double posX = Double.parseDouble(fields[2]);
        double posY = Double.parseDouble(fields[3]);
        this.sensorPos  = new Par<>(posX, posY);
        this.ninoId     = fields[4];
        this.intensidad = Double.parseDouble(fields[5]);
        this.creacion   = Long.parseLong(fields[6]);
    }

    /**
     * Constructor para la clase DatosSensor.
     *
     * @param mensaje String con los datos de la clase.
     */
    public DatosSensor(DatosSensor base, String ninoId, double intensidad) {
        this.id         = base.getID();
        this.sala       = base.getSala();
        this.sensorPos  = base.getPosicionSensor();
        this.ninoId     = ninoId;
        this.intensidad = intensidad;
        this.creacion   = new Date().getTime();
    }

    /**
     * Crea una nueva instancia a partir de los datos recibidos en Connext DDS.
     *
     * @param sample Datos para parsear.
     * @return Instancia de esta clase.
     */
    public static DatosSensor FromDds(final DynamicData sample) {
        DatosSensor datos = new DatosSensor();
        datos.id    = sample.get_string("id",   DynamicData.MEMBER_ID_UNSPECIFIED);
        datos.sala  = sample.get_string("sala", DynamicData.MEMBER_ID_UNSPECIFIED);
        double posX = sample.get_double("posX", DynamicData.MEMBER_ID_UNSPECIFIED);
        double posY = sample.get_double("posY", DynamicData.MEMBER_ID_UNSPECIFIED);
        datos.sensorPos  = new Par<>(posX, posY);
        datos.ninoId     = sample.get_string("ninoId", DynamicData.MEMBER_ID_UNSPECIFIED);
        datos.intensidad = sample.get_double("rssi",      DynamicData.MEMBER_ID_UNSPECIFIED);
        datos.creacion   = sample.get_long("fecha",    DynamicData.MEMBER_ID_UNSPECIFIED);
        return datos;
    }

    /**
     * Crea una nueva instancia a partir de los datos que contiene una entrada
     * de XML.
     *
     * @param el Entrada de XML con los datos.
     * @return Instancia de esta clase.
     */
    public static DatosSensor FromXml(final Element el) {
        DatosSensor datos = new DatosSensor();
        datos.id   = GetXmlEntryValue(el, "id");
        datos.sala = GetXmlEntryValue(el, "sala");
        double posX = Double.parseDouble(GetXmlEntryValue(el, "posX"));
        double posY = Double.parseDouble(GetXmlEntryValue(el, "posY"));
        datos.sensorPos = new Par<>(posX, posY);
        return datos;
    }

    /**
     * Shortcut to get the text in a XML entry.
     *
     * @param el XML entry element.
     * @param name Tag name.
     * @return Value of the entry.
     */
    private static String GetXmlEntryValue(final Element el, final String name) {
        return el.getElementsByTagName(name).item(0).getTextContent();
    }

    /**
     * Devuelve la ID del sensor emisor.
     *
     * @return	ID del sensor emisor.
     */
    public String getID() {
        return id;
    }

    /**
     * Devuelve el nombre de la sala en la que está.
     *
     * @return Nombre de la sala.
     */
    public String getSala() {
        return sala;
    }

    /**
     * Devuelve la posición del sensor.
     *
     * @return	Posición del sensor.
     */
    public Par<Double, Double> getPosicionSensor() {
        return sensorPos;
    }

    /**
     * Devuelve intensidad de señal.
     *
     * @return	Intensidad de señal.
     */
    public double getIntensidad() {
        return intensidad;
    }

    /**
     * Devuelve la ID del niño.
     *
     * @return	ID del niño.
     */
    public String getIDNino() {
        return ninoId;
    }

    /**
     * Devuelve el momento en el que fue creado el dato.
     *
     * @return	Hora del dato.
     */
    public long getCreacion() {
        return creacion;
    }

    /**
     * Método toString sobrecargado.
     *
     * @return Cadena de caracteres con el valor de los atributos.
     */
    @Override
    public String toString() {
        return id + " " + sala + " " + sensorPos.getPrimero() + " " +
                sensorPos.getSegundo()  + " " + ninoId + " " + intensidad + " "
                + creacion;
    }

    /**
     * Escribe los datos en una instancia reutilizable.
     *
     * @param datos Instancia en la que escribir los datos.
     */
    public void escribeDds(DynamicData datos) {
        datos.clear_all_members();

        datos.set_string("id",     DynamicData.MEMBER_ID_UNSPECIFIED, this.id);
        datos.set_string("sala",   DynamicData.MEMBER_ID_UNSPECIFIED, this.sala);
        datos.set_double("posX",   DynamicData.MEMBER_ID_UNSPECIFIED, this.sensorPos.getPrimero());
        datos.set_double("posY",   DynamicData.MEMBER_ID_UNSPECIFIED, this.sensorPos.getSegundo());
        datos.set_string("ninoId", DynamicData.MEMBER_ID_UNSPECIFIED, this.ninoId);
        datos.set_double("rssi",   DynamicData.MEMBER_ID_UNSPECIFIED, this.intensidad);
        datos.set_long  ("fecha",  DynamicData.MEMBER_ID_UNSPECIFIED, this.creacion);
    }

    /**
     * Escribe los datos de esta estructura en una instancia de un sólo uso.
     *
     * @param escritor Escritor del que generar la instancia y enviar.
     */
    public void escribeDds(Escritor escritor) {
        DynamicData datos = escritor.creaDatos();
        this.escribeDds(datos);
        escritor.escribeDatos(datos);
        escritor.eliminaDatos(datos);
    }
}
