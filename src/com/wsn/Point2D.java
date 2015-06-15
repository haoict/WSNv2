/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author HEDSPI
 */
package com.wsn;
import static com.wsn.ConstValues.*;
import static java.lang.Math.*;
public class Point2D {
    public double x, y;
    
    public Point2D(String t) {
        String[] sItems;
        sItems = t.split(" ");
        this.x = Double.parseDouble(sItems[0]);
        this.y = Double.parseDouble(sItems[1]);
    }
    
    double distance(Point2D t){
        return sqrt(pow(x-t.x,2)+pow(y-t.y,2.0));
    }
    boolean inDistance(Point2D t){
        return distance(t) - EPS < 1.00;
    }
    public Point2D(Point2D a){
        x = a.x;
        y = a.y;
    }
    public Point2D(double u, double v){
        x = u;
        y = v;
    }
    public Point2D(){
        x = y = 0;
    }
    @Override
    public String toString(){
        return x + " " + y;
        //return String.format("%.2lf", x) + " " + String.format("%.2lf", y);
    }
    public static void main(String[] args){
        Point2D a = new Point2D(1,2);
        Point2D b = new Point2D(2,3);
        double ans = a.distance(b);
        System.out.print(ans);
    }
}

