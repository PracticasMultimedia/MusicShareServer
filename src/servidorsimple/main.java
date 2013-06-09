/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorsimple;

import GUI.Servidor_Interfaz;
import java.io.IOException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Clase que lanza el servidor de musica
 * @author Jesus Adrian
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //Comprueba si esta instalado el Look and Feel Nimbus, y si esta lo aplica
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
        
        //Lanzamos el servidor
        Servidor_Interfaz gui = new Servidor_Interfaz();
        gui.setVisible(true);


    }
//    }
}
