package com.motorph.main;

import com.motorph.ui.login.Login;

public class Main {

    public static void main(String[] args) {
        
         // Use invokeLater to ensure thread safety for Swing components
        java.awt.EventQueue.invokeLater(() -> {
            Login loginFrame = new Login(null);
            loginFrame.setLocationRelativeTo(null); // Centers the window on screen
            loginFrame.setVisible(true);            
        });
    }
}
