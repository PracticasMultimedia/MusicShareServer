/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorsimple;

import GUI.Servidor_Interfaz;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 *
 * @author macosx
 */
public class ServirDirectorios extends Thread {

    static final String OK = "Successful";
    static final String NOK = "Not Understood";
    FileSystem fs;
    final int PUERTO = 5000;
    ServerSocket socket;
    Socket sck;
    DataOutputStream out;
    String nombre = InetAddress.getLocalHost().getHostName();
    MandarAuido ma = null;
    String path;
    Servidor_Interfaz gui;
    UDPBroadcast udp;

    public ServirDirectorios() throws IOException {
    }

    public ServirDirectorios(String _path, Servidor_Interfaz _gui) throws IOException {
        path = _path;
        gui = _gui;
        udp = null;
    }

    private void out(String s) {
        System.out.println(s);
    }

    public void setPath(String _path) {
        path = _path;
        fs = new FileSystem(path);
    }

    @Override
    public void run() {
        try {

            ma = new MandarAuido();
            ma.start();
            /**
             * Creamos las variables que vamos a necesitar.
             */
            if (path == null) {
                fs = new FileSystem(File.listRoots()[0].getAbsolutePath());
            } else {
                fs = new FileSystem(path);
            }


            /**
             * Creamos la conexión.
             */
            socket = new ServerSocket(PUERTO);
            sck = new Socket();
            gui.out("Se han creado los sockets para la comunicación");

            /**
             * Esperamos la conexión.
             */
            out("Esperando conexion...");
            sck = socket.accept();

            out("Conexión establecida con el cliente [" + sck.getRemoteSocketAddress().toString() + "]");
            out = new DataOutputStream(sck.getOutputStream());

            /**
             * Creamos el buffer de entrada.
             */
            BufferedReader in = new BufferedReader(new InputStreamReader(sck.getInputStream()));


            /**
             * Presentaciones... Establecemos los parámetros iniciales.
             */
            gui.out("Conexion establecida con " + in.readLine());
            out.write((nombre + "\n").getBytes(Charset.forName("UTF-8")));

            for (String s : fs.dirMusicRecursive()) {
                out.write((s + "\n").getBytes(Charset.forName("UTF-8")));
            }
            out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));


            /**
             * Navegación por archivos...
             */
            String msg = "";
            while (!msg.equals("exit")) {
                msg = in.readLine();

                String[] commands = msg.split(" ");


                out(commands[0]);

                switch (commands[0]) {

                    case "cd":
                        if (commands.length >= 2) {
                            String file = msg.split("\"")[1];
                            gui.out("[CD] Moviendonos a " + file);
                            fs.cd(file);
                            out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));
                            break;
                        }
                        out.write(("Error" + "\n").getBytes(Charset.forName("UTF-8")));
                        break;

                    case "dir":
                        gui.out("[DIR] Listando directorios");
                        for (String file : fs.dir()) {
                            out.write((file + "\n").getBytes(Charset.forName("UTF-8")));

                        }
                        out.write((".." + "\n").getBytes(Charset.forName("UTF-8")));
                        break;

                    case "play":
                        if (commands.length >= 2) {

                            String file = msg.split("\"")[1];
                            String ruta = fs.getAbsolutePath(file);
                            gui.out("[PLAY] Reproduciendo " + file);
                            ma.play(ruta);
                            out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));
                        } else {
                            out.write(("Error" + "\n").getBytes(Charset.forName("UTF-8")));
                        }
                        break;

                    case "play_fm":
                        if (commands.length >= 2) {

                            String file = msg.split("\"")[1];
                            gui.out("[PLAY] Reproduciendo " + file);
                            ma.play(file);
                            out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));
                        } else {
                            out.write(("Error" + "\n").getBytes(Charset.forName("UTF-8")));
                        }
                        break;
                    case "play_fr":

                        if (commands.length >= 2) {
                            String file = ma.getSongName(Integer.parseInt(msg.split("\"")[1]));
                            if (file == null) {
                                out.write(("Error" + "\n").getBytes(Charset.forName("UTF-8")));
                            } else {
                                gui.out("[PLAY] Reproduciendo " + file);
                                System.out.println("Parseint: " + Integer.parseInt(msg.split("\"")[1]));
                                ma.selectSong(Integer.parseInt(msg.split("\"")[1]));

                                out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));
                            }
                        } else {
                            out.write(("Error" + "\n").getBytes(Charset.forName("UTF-8")));
                        }

                        break;

                    case "stop":
                        if (ma != null) {
                            ma.stop_();
                            ma = null;
                        }
                        out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));
                        break;

                    case "add":
                        if (commands.length >= 2) {
                            String song = msg.split("\"")[1];
                            String ruta = fs.getAbsolutePath(song);
                            ma.addSong(ruta);
                            out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));

                        } else {
                            out.write(("Error" + "\n").getBytes(Charset.forName("UTF-8")));
                        }
                        break;

                    case "add_fm":

                        String song = msg.split("\"")[1];
                        ma.addSong(song);
                        out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));
                        break;


                    case "delete":

                        if (commands.length >= 2) {

                            String file = ma.getSongName(Integer.parseInt(msg.split("\"")[1]));
                            out(msg.split("\"")[1]);
                            out(file);

                            if (file == null) {
                                out.write(("Error" + "\n").getBytes(Charset.forName("UTF-8")));
                            } else {
                                gui.out("[Delete] Borrando " + file);

                                ma.deleteSong(Integer.parseInt(msg.split("\"")[1]));

                                out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));
                            }
                        } else {
                            out.write(("Error" + "\n").getBytes(Charset.forName("UTF-8")));
                        }

                        break;

                    case "repeat":

                        String rep = msg.split("\"")[1];
                        if (rep.equals("true")) {
                            ma.setLoop(true);
                            gui.out("[LOOP] TRUE");
                        } else {
                            ma.setLoop(false);
                            gui.out("[LOOP] FALSE");
                        }
                        out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));
                        break;

                    case "shuffle":

                        String ran = msg.split("\"")[1];
                        if (ran.equals("true")) {
                            ma.setShuffle(true);
                            gui.out("[SHUFFLE] TRUE");
                        } else {
                            ma.setShuffle(false);
                            gui.out("[SHUFFLE] FALSE");
                        }
                        out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));

                        break;

                    case "exit":
                        if (ma != null) {
                            ma.kill();
                            gui.out("[EXIT]");
                        }
                        break;

                    case "next":
                        gui.out("[NEXT] Reproduciendo la siguiente canción.");
                        ma.next();
                        out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));
                        break;

                    case "prev":
                        gui.out("[PREV] Reproduciendo la canción anterior.");
                        ma.previous();
                        out.write((OK + "\n").getBytes(Charset.forName("UTF-8")));
                        break;
                        
                    default:
                        out.write((NOK + "\n").getBytes(Charset.forName("UTF-8")));
                        break;
                }

            }

            sck.close();
            socket.close();
            if (ma != null) {
                ma.kill();
            }


        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ma.kill();
        }

    }

    public void initUDPBroadcast() {
        if (udp == null) {
            udp = new UDPBroadcast();
            udp.start();
            gui.out("[UDP] Iniciada la escucha de Conexiones Directas.");
        }
    }

    public void stopUDPBroadcast() {
        if (udp != null) {
            udp.stopListening();
            System.out.println("StopUDP" + udp);
            udp = null;
            gui.out("[UDP] Escucha de Conexiones Directas detenida.");
        }
    }
}
