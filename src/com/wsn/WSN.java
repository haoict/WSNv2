package com.wsn;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class WSN {

    
    public static void main(String[] args) throws InterruptedException {
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");   
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");              
        } catch (Exception e) {
            System.err.println("Look and feel not set.");
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
        //System.out.println("End test map!");
    }
    
}
