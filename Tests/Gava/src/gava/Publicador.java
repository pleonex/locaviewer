/*
 * Copyright (C) 2014 Prometheus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package gava;

import org.gstreamer.Buffer;
import org.gstreamer.ClockTime;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.elements.AppSink;

public class Publicador {

    /**
     * Inicia la aplicación.
     * 
     * @param args Ninguno.
     */
    public static void main(String[] args) {
        // Inicializamos GStreamer
        args = new String[] {
        "v4lsrc device=/dev/video0",       // Origen de vídeo
        "video/x-raw, width=640, height=480, framerate=15/1",
        "appsink"   // Destino de vídeo
        };
        args = Gst.init("Gava", args);

        // Inicia la obtención de vídeo
        Pipeline p = Pipeline.launch(args);
        AppSink appsink = (AppSink)p.getSinks().get(0);
        p.play();

        // Cambiar el estado puede tomar hasta 5 segundos. Comprueba errores.
        State retState = p.getState(ClockTime.fromSeconds(5).toSeconds());
        if (retState == State.NULL) {
            System.err.println("failed to play the file");
            System.exit(-1);
        }
        
        // Configura el APPSINK
        appsink.setQOSEnabled(true);
        
        // Mientras no se acabe, coje cada frame y lo envía.
        while (!appsink.isEOS()) {
            Buffer buffer = appsink.pullBuffer();

            if (buffer == null)
                 continue;

            // TODO: Enviarlos por DDS
            byte[] toSend = buffer.getByteBuffer().array();
            String caps   = buffer.getCaps().toString();
            
            // TODO: Los caps son todos iguales y se podría solo enviar uno que
            // se obtiene desde
            //String caps = appsink.getCaps().toString();
            // Hay que mirar como enviar un dato sólo una vez al inicio.
        }
    }
}
