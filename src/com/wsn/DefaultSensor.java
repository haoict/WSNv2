package com.wsn;

import static com.wsn.ConstValues.*;
import static com.wsn.ConvertToGraphic.*;
import static com.wsn.SensorPanel.*;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class DefaultSensor implements Sensor {

    protected final Point2D coOrdinate;
    protected final int ID; // The ID & coOrdinate could not change after constructor
    protected boolean requireScan;
    protected Package pack;
    protected final List<Integer> hashOfMessage = new ArrayList<>();
    protected int activeTime;
    protected int triggerTime;
    protected List<Sensor> nSensors = new ArrayList<>();
    protected boolean isSentPath;
    protected Sensor nextSensor; 
    protected boolean isDes;
    protected int delayDrawDes;
    
    public List<Sensor> getNeighbourSensors() {
        return nSensors;
    }

    @Override
    public void setNeighbourSensors(List<Sensor> neighbourSensors) {
        this.nSensors = neighbourSensors;
    }

    @Override
    public int getTriggerTime() {
        return triggerTime;
    }

    @Override
    public int getID() {
        return ID;
    }

    public int chooseSensor(List<Sensor> sensors, int hashNum, Point2D desPoint) {
        double min = MAX_DISTANCE;
        int best = -1;
        for (int i = 0; i < sensors.size(); i++) {
            if (sensors.get(i).isAvailable()) {
                if (!sensors.get(i).isContainMessage(hashNum)) {
                    if (min > sensors.get(i).getLocation().distance(desPoint)) {
                        min = sensors.get(i).getLocation().distance(desPoint);
                        best = i;
                    }
                }
            }
        }
        return best;
    }

    @Override
    public boolean isContainMessage(int hashNum) {
        return this.hashOfMessage.contains(hashNum);
    }

    @Override
    public Point2D getLocation() {
        return coOrdinate;
    }

    DefaultSensor(Point2D add, int id) {
        coOrdinate = add;
        ID = id;
        requireScan = false;
        activeTime = -1;
        isDes = false;
        isSentPath = false;
        delayDrawDes = 0;
    }

    @Override
    public void setPack(Package pack) {
        this.pack = pack;
        this.pack.addPath(ID);
        this.hashOfMessage.add(pack.hashCode());
        this.triggerTime = getTimeCounter() + TRANFER_TIME;
        this.activeTime = MAX_TIME;
        if (pack.IDdes == ID) {            
            MapFrame.updateTextArea("\n" + pack.message + " - Tranfer successful!\n");
            this.triggerTime = -1;
            this.activeTime = getTimeCounter() + TRANFER_TIME;
            Statistics.add(pack, getTimeCounter() + TRANFER_TIME);
            isSentPath = false;
            deletePack();
            this.isDes = true;
        }
    }

    public Package takePack() {
        Package p = this.getPack();
        deletePack();
        return p;
    }

    @Override
    public void transPack(Sensor s) {
        s.setPack(this.takePack());
        activeTime = getTimeCounter() + TRANFER_TIME;
    }

    public void deletePack() {
        this.pack = null;
    }

    @Override
    public Package getPack() {
        return pack;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        if (requireScan) {
            g.setColor(Color.RED);
        }
        if (pack != null) {
            g.setColor(Color.YELLOW);
        }
        int[] temp = convertPo(coOrdinate);
        g.fillOval(temp[0] - SIZE / 2, temp[1] - SIZE / 2, SIZE, SIZE);
        g.setColor(Color.BLACK);
        g.drawString("G" + String.valueOf(ID), temp[0], temp[1]);
        /*
        if (isSentPath) {   
            if (nextSensor.havePack()) {
                g.setColor(Color.RED);
                int[] temp2 = convertPo(nextSensor.getLocation());
                g.drawLine(temp[0], temp[1], temp2[0], temp2[1]);      
            }
        } */       
        if (isDes) {
            g.setColor(Color.ORANGE);
            int size = SIZE * 2;
            g.fillOval(temp[0] - size / 2, temp[1] - size / 2, size, size);
            delayDrawDes++;
        }
        
        if (delayDrawDes > 80) {
            isDes = false;
        }
    }

    @Override
    public boolean isAvailable() {
        return getTimeCounter() >= activeTime;
    }

    @Override
    public void run() {
        if (!requireScan) {
            requireScan = true;
            triggerTime = getTimeCounter() + SCAN_TIME;
            return;
        }
        requireScan = false;
        int choosen;
        choosen = chooseSensor(nSensors, pack.hashCode(), pack.getDesLocation());
        if (choosen == -1) {            
            MapFrame.updateTextArea("Message: -" + pack.getMessage() + " -Could not reach\n");
            Statistics.add(pack, MAX_TIME);
            deletePack();
            isSentPath = false;
            return;
        }
        isSentPath = true;       
        MapFrame.updateTextArea("[Greedy] " + ID + " -> " + nSensors.get(choosen).getID() + "\n");
        nextSensor = nSensors.get(choosen);
        transPack(nextSensor);     
    }

    @Override
    public boolean isRequireScan() {
        return requireScan;
    }

    @Override
    public void turnOffScan() {
        requireScan = false;
    }

    @Override
    public boolean havePack() {
        return pack != null;
    }

    @Override
    public void setSentPath(boolean b) {
        isSentPath = b;
    }

    @Override
    public int getReceivedMessage() {
        return this.hashOfMessage.size();
    }

}
