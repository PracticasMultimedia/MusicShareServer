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
 *
 * @author macosx
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
        Servidor_Interfaz gui = new Servidor_Interfaz();
        gui.setVisible(true);


    }
//    }
}
