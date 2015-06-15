/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wsn;

import static com.wsn.Geometry.is2LineSegmentCross;
import static com.wsn.ConvertToGraphic.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

public class Line {
    Point2D startP, endP;    
    public Line(Point2D startP, Point2D endP) {
        this.startP = startP;
        this.endP = endP;
    }
    
    public Line(String input) throws ParseException {
        String[] sItems;
        sItems = input.split(" ");
        startP = new Point2D(Double.parseDouble(sItems[0]), Double.parseDouble(sItems[1]));
        endP = new Point2D(Double.parseDouble(sItems[2]), Double.parseDouble(sItems[3]));
    }
        
    boolean isIntersect(Line line){
        return is2LineSegmentCross(new Line(startP, endP), line);
    }
    boolean areIntersect(List<Line> lines){
        for (Line line:lines){
            if (this.isIntersect(line))
                    return true;
        }
        return false;
    }
    void draw(Graphics g){
        int[] temp1 = convertPo(this.startP);
        int[] temp2 = convertPo(this.endP);
        g.setColor(Color.darkGray);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(2));
        g.drawLine( temp1[0], temp1[1], temp2[0], temp2[1]);      
        g2d.setStroke(new BasicStroke(1));
    }
    
    @Override
    public String toString() {
        return startP.toString() + " " + endP.toString();
    }
}
