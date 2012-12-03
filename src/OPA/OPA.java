/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OPA;

import OPA.GUI.MainFrame;
import OPA.Object.ObjectHandler;

/**
 *
 * @author Dirk
 */
public class OPA {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ObjectHandler objHandler = new ObjectHandler();
                MainFrame gui = new MainFrame(objHandler);
                gui.setVisible(true);
                gui.setLocationRelativeTo(null);
            }
        });
    }
}
