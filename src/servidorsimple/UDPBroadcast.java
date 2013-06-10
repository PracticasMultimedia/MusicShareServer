/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorsimple;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Clase que lanza un servidor udp que escucha conexiones en el puerto 9876 y
 * devuelve la ip del servidor de música
 *
 * @author Jesus Adrian
 */
public class UDPBroadcast extends Thread {

    private boolean listen;

    /**
     * Constructor que lanza el servidor
     */
    @Override
    public void run() {
        listen = true;
        listen();
    }

    /**
     * Método principal de la clase que lanza el servidor, hasta que se le
     * indica con la funcion stopListening
     */
    private void listen() {
        try {
            DatagramSocket sckServidor = new DatagramSocket(9876);
            byte[] datosRecibidos = new byte[1024];
            byte[] datosEnvio = new byte[1024];
            
            while (listen) {
                DatagramPacket pckRecibido = new DatagramPacket(datosRecibidos, datosRecibidos.length);
                sckServidor.receive(pckRecibido);
                String mensaje = new String(pckRecibido.getData());
                InetAddress IPAddress = pckRecibido.getAddress();
                int port = pckRecibido.getPort();
                
                if (mensaje.trim().equals("MENSAJE PARA CONEXION DIRECTA")) {
                    datosEnvio = "CONEXION DIRECTA DISPONIBLE".getBytes();
                    DatagramPacket sendPacket =
                            new DatagramPacket(datosEnvio, datosEnvio.length, IPAddress, port);
                    sckServidor.send(sendPacket);
                }
                
            }
            
        } catch (Exception ex) {
        }
    }
    
    /**
     * Método para parar la escucha del servidor udp
     */
    public void stopListening() {
        listen = false;
    }
}
