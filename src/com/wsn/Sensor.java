package com.wsn;

import java.awt.Graphics;
import java.util.List;
import java.util.Map;

public interface Sensor {

    public static final int MAX_DISTANCE = 1000;

    public boolean isAvailable();

    public int getID();
    
    public Point2D getLocation();

    public void setPack(Package takePack);

    public Package getPack();

    public void transPack(Sensor get);
    
    public boolean isContainMessage(int hashNum);
    
    public void draw(Graphics g);

    public int getTriggerTime();

    public void run();

    public boolean isRequireScan();

    public void setNeighbourSensors(List<Sensor> ns);

    public void turnOffScan();

    public boolean havePack();
    
    public void setSentPath(boolean b);
    
    public int getReceivedMessage();
}
