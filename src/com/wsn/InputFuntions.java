/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wsn;

import javax.swing.JOptionPane;

/**
 *
 * @author Mr CUONG
 */
public class InputFuntions {

    public static int inputInteger(String message) {
        int ans = -1;
        while (true) {
            try {
                String line = JOptionPane.showInputDialog(null, message);
                if (null != line) {
                    ans = Integer.parseInt(line);
                } else {}
            } catch (NumberFormatException t) {
                JOptionPane.showMessageDialog(null, "It is not a number");
                continue;
            }
            break;
        }
        return ans;
    }

    public static double inputDouble(String message) {
        double x = -1;
        while (true) {
            try {
                String line = JOptionPane.showInputDialog(null, message);
                if (null != line) {
                    x = Double.parseDouble(line);
                } else {}
            } catch (NumberFormatException t) {
                JOptionPane.showMessageDialog(null, "It is not a number");
                continue;
            }
            break;
        }
        return x;
    }

    public static Point2D inputPoint2D() {
        double x = inputDouble("Enter coordiate X:");
        double y = inputDouble("Enter coordiate Y:");
        return new Point2D(x, y);
    }
}
