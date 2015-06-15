/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wsn;

import static com.wsn.ConstValues.*;

/**
 *
 * @author Mr CUONG
 */
public class ConvertToGraphic {

    public static int convertCo(double x) {
        return (int) (x * SCALE);
    }

    public static int[] convertPo(Point2D a) {
        int[] temp = new int[2];
        temp[0] = convertCo(a.x) + LEFT_MARGIN;
        temp[1] = convertCo(a.y) - TOP_MARGIN;
        return temp;
    }

    public static Point2D convertToPo(int x, int y) {
        return new Point2D((double) (x - LEFT_MARGIN) / SCALE, (double) (y + TOP_MARGIN) / SCALE);
    }
}
