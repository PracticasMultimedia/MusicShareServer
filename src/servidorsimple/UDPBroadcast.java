/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorsimple;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Jesus
 */
//Servidor
public class UDPBroadcast extends Thread {

    private boolean listen;

    @Override
    public void run() {
        listen = true;
        listen();
    }

    private void listen() {
        try {
            DatagramSocket sckServidor = new DatagramSocket(9876);
            byte[] datosRecibidos = new byte[1024];
            byte[] datosEnvio = new byte[1024];
            while (listen) {
                DatagramPacket pckRecibido = new DatagramPacket(datosRecibidos, datosRecibidos.length);
                System.out.println("esperando paquetes <-");
                sckServidor.receive(pckRecibido);
                String mensaje = new String(pckRecibido.getData());
                System.out.println("RECEIVED: " + mensaje);
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

    public void stopListening() {
        listen = false;
    }
}
