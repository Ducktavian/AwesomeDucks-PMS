/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.motorph.main;

import com.motorph.ui.Login;
import javax.swing.SwingUtilities;

/**
 *
 * @author Lenovo
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}