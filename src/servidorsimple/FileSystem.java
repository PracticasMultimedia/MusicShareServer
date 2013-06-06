package servidorsimple;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import javax.print.DocFlavor;

/**
 *
 * @author JesÃºs
 */
public class FileSystem {

    public static final String CD_PARENT = "..";
    /**
     * Variable que guardará el directorio actual en el que estamos.
     */
    File path;
    File deepestPath;

    public FileSystem(String initPath) {
        path = new File(initPath);
        deepestPath = new File(initPath);
    }

    public FileSystem(File initPath) {
        path = initPath;
        deepestPath = initPath;
    }

    public String getAbsolutePath() {
        return path.getAbsolutePath();
    }

    public String getAbsolutePath(String child) {
        File temp = new File(path.getAbsolutePath(), child);
        return temp.getAbsolutePath();
    }

    public String getParent() {
        return path.getParent();
    }

    public String getPath() {
        return path.getPath();
    }

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
    
    public ArrayList<String> dirMusicRecursive(){
        ArrayList<String> f = new ArrayList<>();
        for(String s : dirFilesRecursive()){
            if(s.endsWith(".mp3")){
                f.add(s);
            }
        }
        return f;
    }
}
