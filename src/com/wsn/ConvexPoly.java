package com.wsn;

import static com.wsn.ConvertToGraphic.convertPo;
import static com.wsn.Geometry.isPoint2DRightOfLine;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class ConvexPoly {

    private final ArrayList<Point2D> points = new ArrayList<>();

    public ConvexPoly() {
       // points = new ArrayList<>();
    }

    public ConvexPoly(String t) {
        String[] sItems;
        sItems = t.split(" ");
        for (int i = 0; i < sItems.length / 2; i++) {
            Point2D temp = new Point2D(sItems[2*i] + " " + sItems[2*i+1]);
            points.add(temp);
        }
    }

    public void addPoint(Point2D point) {
        points.add(point);
    }
    public int size(){
        return points.size();
    }
    public Point2D getPoint(int index){
        return points.get(index);
    }
    public void draw(Graphics g){
        int[] xpoint = new int[size()];
        int[] ypoint = new int[size()];
        for (int i=0;i<size();i++){
            int[] temp = convertPo(points.get(i));
            xpoint[i] = temp[0];
            ypoint[i] = temp[1];            
        }
        g.setColor(Color.GREEN);
        g.fillPolygon(xpoint,ypoint, size());
        g.setColor(Color.BLACK);
    }
    
    public boolean isInside(Point2D point){
        for (int i = 0; i < size();i++){
            Point2D x = new Point2D(points.get(i));
            Point2D y = new Point2D(points.get((i+1) % size()));
            if (!isPoint2DRightOfLine(new Line(x,y),point)){
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < points.size();i++){
            temp.append(points.get(i).toString());
            temp.append(" ");                  
        }
        return temp.toString();
    }
}
