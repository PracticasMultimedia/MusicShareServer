package servidorsimple;

import com.xuggle.xuggler.*;
import ddf.minim.analysis.FFT;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import semaforo.SemaforoBinario;

/**
 * Clase hilo que mantiene el servidor de datos y de control de la música
 *
 * @author Jorge V
 */
public class MandarAuido extends Thread {

    semaforo.SemaforoBinario sem = new SemaforoBinario(0);
    final int PUERTO_CONTROL = 5001;
    ServerSocket serv_control;
    Socket socket_control;
    DataOutputStream out_control;
    final int PUERTO_DATOS = 5002;
    ServerSocket serv_datos;
    Socket socket_datos;
    DataOutputStream out_datos;
    IContainer container;
    IStreamCoder audioCoder;
    String mensajeRecibido;
    ArrayList<String> songList;
    boolean continuar;
    int indice;
    boolean loop;
    boolean shuffle;
    boolean salir;
    boolean stopped;
    boolean stop;

    /**
     * Constructor por defecto
     */
    public MandarAuido() {

        songList = new ArrayList<>();
        indice = 0;
        loop = false;
        shuffle = false;
        salir = false;
        continuar = true;

    }

    /**
     * Método que elimina la lista de canciones y añade la que se le indica
     *
     * @param song Cancion a reproducir cuando se elimine la lista
     */
    public void play(String song) {


        deleteListSong();
        indice = 0;

        songList.add(song);

        continuar = false;

        boolean aux = stopped;

        if (aux) {
            sem.SIGNAL();
        }


    }

    /**
     * Método que para la reproduccion y elimina la lista de canciones
     */
    public void deleteListSong() {

        indice = 0;
        songList.clear();
        continuar = false;

    }

    /**
     * Elimina una canción de la lista
     *
     * @param song ruta de la canción a eliminar
     */
    public void deleteSong(int song) {

        songList.remove(song);

    }

    /**
     * Indica si se quiere poner la reproduccion en modo continuo
     *
     * @param b boolean que indica el estado que se tiene que poner
     */
    public void setLoop(boolean b) {

        loop = b;

    }

    /**
     * Indica si se quiere poner la reproduccion en modo aleatorio
     *
     * @param b boolean que indica el estado que se tiene que poner
     */
    public void setShuffle(boolean b) {

        shuffle = b;

    }

    /**
     * Añade la cancion a la lista
     *
     * @param file ruta de la cancion
     */
    public void addSong(String file) {

        songList.add(file);
        continuar = true;

        boolean aux = stopped;

        if (aux) {
            sem.SIGNAL();
        }

    }

    /**
     * Reproduce la siguiente cancion
     */
    public void next() {
        
        if (shuffle == true) {
            
            continuar = false;
            
        } else {
            
            indice--;//ahora apunta a la actual

            if (indice == songList.size() - 1) {

                if (loop == true) {
                    indice = 0;
                    continuar = false;//detenemos la canción actual
                } else {
                    indice++;//al estar el indice fuera de rango parara la reproduccion al terminar la cancion
                }

            } else {

                indice++;//ahora apunta a la siguiente
                continuar = false;//detenemos la canción actual
            }
        }
    }

    /**
     * Reproduce la canción anterior
     */
    public void previous() {

        if (shuffle == true) {
            continuar = false;
        } else {
            indice--;//ahora apunta a la actual

            if (indice == 0) {

                if (loop == true) {
                    indice = songList.size() - 1;
                    continuar = false;//detenemos la canción actual
                } else {
                    indice++;//continuara la siguiente canción
                }

            } else {

                indice--;//ahora apunta a la anterior
                continuar = false;//detenemos la canción actual
            }
        }
    }

    /**
     * Devuelve el nombre de una cancion de la lista, a partir de un índice
     *
     * @param i indice de la cancion
     * @return nombre de la cancion
     */
    public String getSongName(int i) {

        if (i < 0 || i >= songList.size()) {
            return null;
        }

        return songList.get(i);

    }

    /**
     * Método que es llamado por el método de reproduccion para que le devuelva
     * la siguiente canción para reproducir
     *
     * @return ruta de la siguiente cancion o nulo si no hay mas que reproducie
     */
    private String getNextSong() {

        int ret;

        if (songList.isEmpty()) {

            return null;

        } else if (loop == false && shuffle == false) {

            if (indice >= songList.size()) {

                return null;

            } else {

                ret = indice;
                indice++;
                return songList.get(ret);
            }

        } else if (shuffle == true) {

            int i = (int) Math.random() % songList.size();
            indice = i;
            return songList.get(indice);

        } else {

            if (indice >= songList.size()) {

                indice = indice % songList.size();

            }
            ret = indice;
            indice++;
            return songList.get(ret);

        }


    }

    /**
     * Manda la orden la hilo reproductor para que se cierre de forma correcta
     */
    public void kill() {
        continuar = false;
        salir = true;

        boolean aux = stopped;


        if (aux) {
            sem.SIGNAL();
        }

    }

    /**
     * Manda la orden al reproductor de que deje de reproducir
     */
    public void stop_() {

        continuar = false;
        stop = true;
    }

    /**
     * Obliga a reproducir una canción de la lista
     *
     * @param i Indice de la cancion
     */
    void selectSong(int i) {

        indice = i;
        continuar = false;

        boolean aux = stopped;

        if (aux) {
            sem.SIGNAL();
        }

    }

    /**
     * Comportamiento del hilo reproductor. Clase heredada de la clase Thread
     */
    @Override
    public void run() {

        BufferedReader entrada;



        try {

            serv_control = new ServerSocket(PUERTO_CONTROL);
            serv_datos = new ServerSocket(PUERTO_DATOS);
            /*
             * crea socket servidor que escuchara en puerto 5000
             */

            socket_control = new Socket();
            socket_datos = new Socket();

            System.out.println("Esperando una conexión:");

            socket_control = serv_control.accept();
            socket_datos = serv_datos.accept();

//Inicia el socket, ahora esta esperando una conexión por parte del cliente

            System.out.println("Un cliente se ha conectado.");



//Canales de entrada y salida de datos

            entrada = new BufferedReader(new InputStreamReader(socket_control.getInputStream()));

            out_control = new DataOutputStream(socket_control.getOutputStream());
            out_datos = new DataOutputStream(socket_datos.getOutputStream());

            System.out.println("Confirmando conexion al cliente....");

            while (salir == false) {

                if (stop) {
                    stop = false;
                    stopped = true;

                    sem.WAIT();
                }


                String newSong = getNextSong();

                if (newSong != null) {

                    System.out.println("Reproduciendo: " + newSong);

                    // Create a Xuggler container object
                    container = IContainer.make();

                    // Open up the container
                    if (container.open(newSong, IContainer.Type.READ, null) < 0) {
                        throw new IllegalArgumentException("could not open file: " + songList);
                    }

                    // query how many streams the call to open found
                    int numStreams = container.getNumStreams();

                    // and iterate through the streams to find the first audio stream
                    int audioStreamId = -1;
                    audioCoder = null;
                    for (int i = 0; i < numStreams; i++) {
                        // Find the stream object
                        IStream stream = container.getStream(i);
                        // Get the pre-configured decoder that can decode this stream;
                        IStreamCoder coder = stream.getStreamCoder();

                        if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
                            audioStreamId = i;
                            audioCoder = coder;
                            break;
                        }
                    }
                    if (audioStreamId == -1) {
                        throw new RuntimeException("could not find audio stream in container: " + songList);
                    }

                    /*
                     * Now we have found the audio stream in this file. Let's open up
                     * our decoder so it can do work.
                     */
                    if (audioCoder.open() < 0) {
                        throw new RuntimeException("could not open audio decoder for container: " + songList);
                    }

                    /*
                     * And once we have that, we ask the Java Sound System to get itself
                     * ready.
                     */

                    out_control.write(((-1) + "\n").getBytes(Charset.forName("UTF-8")));
                    out_control.write(((newSong) + "\n").getBytes(Charset.forName("UTF-8")));
                    out_control.write(((audioCoder.getSampleRate()) + "\n").getBytes(Charset.forName("UTF-8")));
                    out_control.write((((int) IAudioSamples.findSampleBitDepth(audioCoder.getSampleFormat())) + "\n").getBytes(Charset.forName("UTF-8")));
                    out_control.write(((audioCoder.getChannels()) + "\n").getBytes(Charset.forName("UTF-8")));

                    /*
                     * Now, we start walking through the container looking at each
                     * packet.
                     */
                    IPacket packet = IPacket.make();

                    continuar = true;

                    while (container.readNextPacket(packet) >= 0 && continuar) {
                        /*
                         * Now we have a packet, let's see if it belongs to our audio
                         * stream
                         */
                        if (packet.getStreamIndex() == audioStreamId) {
                            /*
                             * We allocate a set of samples with the same number of
                             * channels as the coder tells us is in this buffer.
                             *
                             * We also pass in a buffer size (1024 in our example),
                             * although Xuggler will probably allocate more space than
                             * just the 1024 (it's not important why).
                             */
                            IAudioSamples samples = IAudioSamples.make(1024, audioCoder.getChannels());

                            /*
                             * A packet can actually contain multiple sets of samples
                             * (or frames of samples in audio-decoding speak). So, we
                             * may need to call decode audio multiple times at different
                             * offsets in the packet's data. We capture that here.
                             */
                            int offset = 0;

                            /*
                             * Keep going until we've processed all data
                             */
                            while (offset < packet.getSize() && continuar) {
                                int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
                                if (bytesDecoded < 0) {
                                    throw new RuntimeException("got error decoding audio in: " + songList);
                                }
                                offset += bytesDecoded;
                                /*
                                 * Some decoder will consume data in a packet, but will
                                 * not be able to construct a full set of samples yet.
                                 * Therefore you should always check if you got a
                                 * complete set of samples from the decoder
                                 */



                                if (samples.isComplete()) {

                                    int n = (int) samples.getNumSamples();

                                    float[] samp = new float[1024];
                                    float[] bandas = new float[7];

                                    n = Math.min(n, 1024);

                                    for (int i = 0; i < n; i++) {

                                        samp[i] = samples.getSample(i, 0, audioCoder.getSampleFormat());

                                    }


                                    FFT fft = new FFT(samp.length, (float) audioCoder.getSampleRate());
                                    fft.forward(samp);

                                    int corte = fft.specSize() / 14;

                                    for (int i = 0; i < 7; i++) {
                                        for (int j = 0; j < corte; j++) {
                                            bandas[i] = Math.abs(fft.getBand(i * corte + j));
                                        }
                                    }

                                    String bandasString = "";

                                    for (int i = 0; i < bandas.length; i++) {

                                        bandasString += bandas[i] + ";";

                                    }


                                    out_control.write((samples.getSize() + "\n").getBytes(Charset.forName("UTF-8")));
                                    out_control.write((bandasString + "\n").getBytes(Charset.forName("UTF-8")));
                                    out_datos.write(samples.getData().getByteArray(0, samples.getSize()));
                                }
                            }

                        }


                    }



                    if (audioCoder != null) {
                        audioCoder.close();
                        audioCoder = null;
                    }
                    if (container != null) {
                        container.close();
                        container = null;
                    }
                    /*
                     * Technically since we're exiting anyway, these will be cleaned up
                     * by the garbage collector... but because we're nice people and
                     * want to be invited places for Christmas, we're going to show how
                     * to clean up.
                     */
                } else {


                    stopped = true;

                    sem.WAIT();

                }

            }
            out_control.write("0\n".getBytes(Charset.forName("UTF-8")));


            serv_control.close();
            serv_datos.close();//Aqui se cierra la conexión con el cliente



        } catch (IOException | /*InterruptedException |*/ RuntimeException e) {
            if (serv_control != null) {
                try {
                    serv_control.close();
                    if (serv_datos != null) {
                        serv_datos.close();
                    }
                } catch (IOException ex) {
                }
            }

            System.out.println("Error: " + e.getMessage());

        }

    }
}
