package servidorsimple;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import javax.print.DocFlavor;

/**
 * Clase que mantiene la lógica de la clase a nivel de explorador de ficheros
 *
 * @author Jesus Adrian
 */
public class FileSystem {

    public static final String CD_PARENT = "..";
    /**
     * Variable que guardará el directorio actual en el que estamos.
     */
    File path;
    File deepestPath;

    /**
     * Constructor que asigna la ruta como ruta por defecto, y como ruta mas
     * profunda accesible
     *
     * @param initPath Ruta inicial
     */
    public FileSystem(String initPath) {
        path = new File(initPath);
        deepestPath = new File(initPath);
    }

    /**
     * Constructor que asigna la ruta como ruta por defecto, y como ruta mas
     * profunda accesible
     *
     * @param initPath Ruta inicial
     */
    public FileSystem(File initPath) {
        path = initPath;
        deepestPath = initPath;
    }

    /**
     * Método que devuelve la ruta absoluta de la ruta actual
     *
     * @return ruta actual en cadena de carateres
     */
    public String getAbsolutePath() {
        return path.getAbsolutePath();
    }

    /**
     * Método que devuelve la ruta absoluta de la ruta actual
     *
     * @return ruta actual en cadena de caracteres
     */
    public String getAbsolutePath(String child) {
        File temp = new File(path.getAbsolutePath(), child);
        return temp.getAbsolutePath();
    }

    /**
     * Método que devuelve la ruta padre de la ruta actual, un nivel mas alto
     *
     * @return ruta padre de la actual en cadena de carateres
     */
    public String getParent() {
        return path.getParent();
    }

    /**
     * Método que devuelve la ruta actual
     *
     * @return ruta actual en cadena de carateres
     */
    public String getPath() {
        return path.getPath();
    }

    /**
     * Método que devuelve la ruta en formato URI de la ruta actual
     *
     * @return ruta actual en modo URI
     */
    public URI toUri() {
        return path.toURI();
    }

    /**
     * Cambiar el directorio actual a alguno de los directorios hijos, o al
     * directorio padre.
     *
     * @param route Directorio al que nos debemos mover. ".." si deseamos
     * movernos al padre.
     * @return True si se ha podido cambiar el directorio satisfactoriamente (si
     * el directorio existe, y si es legible). False en otro caso.
     */
    public boolean cd(String route) {
        File temp = null;
        if (route.equals(CD_PARENT)) {
            if (path.getAbsolutePath().equals(deepestPath.getAbsolutePath())) {
                return false;
            }
            temp = path.getParentFile();
        } else {
            temp = new File(path.getAbsolutePath(), route);
        }
        if (temp != null) {
            if (temp.exists()) {
                path = temp;
                return true;
            }
        }

        return false;
    }

    /**
     * Lista el contenido del directorio en que se encuentra.
     *
     * @return Lista de ficheros y directorios presentes en la carpeta en la que
     * se encuentra. Null si el directorio no se puede leer.
     */
    public ArrayList<String> dir() {
        ArrayList<String> childs = new ArrayList<String>(0);
        try {
            if (!path.canRead()) {
                return null;
            }


            for (File child : path.listFiles()) {
                if (child.isDirectory()) {
                    childs.add(child.getName() + "/");
                } else {
                    childs.add(child.getName());
                }
            }
        } catch (java.lang.NullPointerException ex) {
            System.out.println("NullpointerException... ???");
        }
        return childs;
    }

    /**
     * Método que devuelve todas los ficheros, de forma recursiva, a partir de
     * una ruta
     *
     * @return Lista de todos los ficheros
     */
    public ArrayList<String> dirFilesRecursive() {
        File p = deepestPath;
        ArrayList<String> f = new ArrayList<>();
        ArrayList<File> dirs = new ArrayList<>();
        dirs.add(p);
        while (dirs.size() > 0) {
            File file = dirs.get(0);
            dirs.remove(file);
            for (File child : file.listFiles()) {
                if (child.isDirectory()) {
                    dirs.add(child);
                } else {
                    f.add(child.getAbsolutePath());
                }
            }
        }
        return f;
    }

    /**
     * Método que devuelve todas los ficheros mp3, de forma recursiva, a partir
     * de una ruta
     *
     * @return Lista de todos los ficheros mp3
     */
    public ArrayList<String> dirMusicRecursive() {
        ArrayList<String> f = new ArrayList<>();
        for (String s : dirFilesRecursive()) {
            if (s.endsWith(".mp3")) {
                f.add(s);
            }
        }
        return f;
    }
}
