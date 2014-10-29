/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author iblancasa
 */
public class NuevoPadre extends javax.swing.JFrame {

    /**
     * Creates new form NuevoPadre
     */
    public NuevoPadre() {
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        idpadre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        password2 = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        idnino = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        key = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(400, 356));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setBackground(new java.awt.Color(160, 160, 160));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/centroinfantil/gui/padresicon.png"))); // NOI18N
        jLabel1.setText("Nuevo padre");
        jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setBackground(new java.awt.Color(254, 254, 254));
        jPanel2.setLayout(new java.awt.GridLayout(12, 2));

        jLabel2.setText("ID Padre");
        jPanel2.add(jLabel2);
        jPanel2.add(idpadre);

        jLabel3.setText("Password");
        jPanel2.add(jLabel3);
        jPanel2.add(password);

        jLabel4.setText("Repita password");
        jPanel2.add(jLabel4);
        jPanel2.add(password2);

        jLabel5.setText("ID niño");
        jPanel2.add(jLabel5);
        jPanel2.add(idnino);

        jLabel6.setText("Key niño");
        jPanel2.add(jLabel6);
        jPanel2.add(key);

        jButton1.setText("Registrar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);

        jButton2.setText("Reset");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        reset();
    }//GEN-LAST:event_jButton2ActionPerformed

    
    private void reset(){
        idpadre.setText("");
        password.setText("");
        password2.setText("");
        idnino.setText("");
        key.setText("");
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        String pw = new String(password.getPassword());
        String pw2 = new String(password2.getPassword());
        
        if(idpadre.getText().equals("")){
            JOptionPane.showMessageDialog(null, "El ID del padre no puede estar vacío");
        }
        else if(password.getPassword().equals("")){
            JOptionPane.showMessageDialog(null, "La contraseña no puede estar vacía");
        }
        else if(password2.getPassword().equals("")){
            JOptionPane.showMessageDialog(null, "Escriba de nuevo su contraseña");
        }
        else if(!pw.equals(pw2)){
            JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden");
        }
        else if(idnino.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "La ID del niño no puede estar vacía");
        }
        else if(key.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "La key del niño no puede estar vacía");
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
                
                writer.writeUTF("registrar "+idpadre.getText()+" "+pw+" "+idnino.getText()+" "+key.getText());
                
                
                respuesta = reader.readUTF();
                        
                JOptionPane.showMessageDialog(null,respuesta);
                
                if(!respuesta.equals("No se pudo registrar")){
                    reset();
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField idnino;
    private javax.swing.JTextField idpadre;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField key;
    private javax.swing.JPasswordField password;
    private javax.swing.JPasswordField password2;
    // End of variables declaration//GEN-END:variables
}