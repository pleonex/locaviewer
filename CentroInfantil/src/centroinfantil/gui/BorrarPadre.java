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

package centroinfantil.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JOptionPane;

/**
 *
 */
public class BorrarPadre extends javax.swing.JFrame {

    /**
     * Creates new form BorrarPadre
     */
    public BorrarPadre() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        idpadre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        idnino = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(300, 302));

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/centroinfantil/gui/padresicon.png"))); // NOI18N
        jLabel1.setText("Borrar padre");
        jPanel2.add(jLabel1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.GridLayout(10, 0));

        jLabel2.setText("ID padre");
        jPanel1.add(jLabel2);
        jPanel1.add(idpadre);

        jLabel3.setText("ID del niño");
        jPanel1.add(jLabel3);
        jPanel1.add(idnino);

        jButton1.setText("Borrar padre");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private static Socket creaSocketSeguro(final String host, final int puerto) {
        SSLSocket socket = null;

        try {
            // Le indicamos de qué anillo obtener las claves públicas fiables
            // de autoridades de certificación:
            System.setProperty(
                    "javax.net.ssl.trustStore",
                    "./src/cert/cacerts.jks"
            );


            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            socket = (SSLSocket)factory.createSocket(host, puerto);

            socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
        } catch (IOException ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        return socket;
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(idpadre.getText().isEmpty() && idnino.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Rellene todos los campos");
        }
        else{
            Socket socket = creaSocketSeguro("localhost", 6556);
            InputStream inStream = null;
            try {
                DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                inStream = socket.getInputStream();
                DataInputStream reader = new DataInputStream(inStream);

                writer.writeUTF("autentificar admin administrador prometheus");
                String respuesta = reader.readUTF();

                if(respuesta.equals("Hubo algún problema")){
                    System.out.println("");
                }

                writer.writeUTF("borrar "+idpadre.getText()+" "+idnino.getText());

                respuesta = reader.readUTF();

                JOptionPane.showMessageDialog(null,respuesta);

                if(!respuesta.equals("No se pudo registrar")){
                    idpadre.setText("");
                    idnino.setText("");
                }

            } catch (IOException ex) {
                Logger.getLogger(NuevoPadre.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(NuevoPadre.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField idnino;
    private javax.swing.JTextField idpadre;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
