package com.wsn;

import static com.wsn.ConstValues.*;
import static com.wsn.ConvertToGraphic.convertPo;
import static com.wsn.SensorPanel.getTimeCounter;
import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SensorRT extends DefaultSensor implements Queryable {

    private final Map routeTable;
    private boolean isUpdate;
    private int choosen;
    private boolean waiting;
    private int sWaitingTime;
    private final int MAX_HOP = 20;

    public SensorRT(Point2D add, int id) {
        super(add, id);
        routeTable = new HashMap<>();
        triggerTime = getTimeCounter() + UPDATE_TIME;
        isUpdate = false;
        waiting = false;
    }

    @Override
    public int querrySensor(int t) {
        if (t == ID) {
            return 0;
        }
        if (routeTable.containsKey(t)) {
            return ((RecordSensor) routeTable.get(t)).numOfSteps;
        } else {
            return CAN_NOT_REACH;
        }
    }

    private void updateRT() {
        Map temp;
        Map<Integer, Integer> nIDs = new HashMap<>();
        // Change nIDs into Map<ID, get> set of sensor that are querriable
        isUpdate = false;
        for (int i = 0; i < nSensors.size(); i++) {
            routeTable.put(nSensors.get(i).getID(), new RecordSensor(nSensors.get(i).getID(), 1));
            if (nSensors.get(i) instanceof Queryable) {
                nIDs.put(nSensors.get(i).getID(), i);
            }
        }
        Iterator<Map.Entry<Integer, RecordSensor>> iter = routeTable.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, RecordSensor> entry = iter.next();            
            if (!nIDs.containsKey(entry.getValue().nextID)) {                
                isUpdate = true;
                iter.remove();
                continue;
            }
            int t = nIDs.get(entry.getValue().nextID);
            int steps = ((Queryable) nSensors.get(t)).querrySensor(entry.getKey());
            if (steps + 1 > entry.getValue().numOfSteps) {                
                isUpdate = true;
                iter.remove();
            }
        }
        for (Sensor sensor : nSensors) {
            if (sensor instanceof Queryable) {
                temp = ((Queryable) sensor).querrySensors();
            } else {
                continue;
            }
            Iterator entries = temp.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                Integer key = (Integer) entry.getKey();
                RecordSensor value = (RecordSensor) entry.getValue();
                if (value.numOfSteps == MAX_HOP) {
                    continue;
                }
                if (key == ID) {
                    continue;
                }
                RecordSensor direct = (RecordSensor) routeTable.get(key);
                if (direct != null) {
                    if (direct.numOfSteps <= value.numOfSteps + 1) {
                        continue;
                    }
                }
                isUpdate = true;
                routeTable.put(key, new RecordSensor(sensor.getID(), value.numOfSteps + 1));
            }

        }
    }

    @Override
    public void run() {
        if (!waiting) {
            if (!requireScan) {
                requireScan = true;
                triggerTime = getTimeCounter() + SCAN_TIME;
                return;
            }
        }
        requireScan = false;
        if (pack == null) {
            triggerTime = getTimeCounter() + UPDATE_TIME;
            updateRT();
            isSentPath = false;
            return;
        }
        if (waiting) {
            if (nSensors.get(choosen).isAvailable()) {
                transPack(nSensors.get(choosen));                                
                waiting = false;
                return;
            }
            if (getTimeCounter() - sWaitingTime < TIME_EXEED) {
                triggerTime = getTimeCounter() + WAIT_TIME;
                return;
            }
        }
        waiting = false;
        choosen = -1;
        if (routeTable.containsKey(pack.IDdes)) {
            int nextId = ((RecordSensor) routeTable.get(pack.IDdes)).nextID;                        
            MapFrame.updateTextArea("[Routing table] " + ID + "->" + nextId + "\n");
            for (int i = 0; i < nSensors.size(); i++) {
                if (nSensors.get(i).getID() == nextId) {
                    choosen = i;                                                                
                    if (!nSensors.get(i).isAvailable()) {
                        waiting = true;
                        sWaitingTime = getTimeCounter();
                        triggerTime = getTimeCounter() + WAIT_TIME;
                        return;
                    }
                }
            }
        }
        
        
        if (choosen == -1) {
            choosen = chooseSensor(nSensors, pack.hashCode(), pack.getDesLocation());

            if (choosen != -1) {
                MapFrame.updateTextArea("[Greedy] " + ID + "-> " + nSensors.get(choosen).getID() + "\n");
            }
            System.out.println();
        }
        
        if (choosen != -1) {
            isSentPath = true;
            nextSensor = nSensors.get(choosen);
        }
        
        
        if (choosen == -1) {            
            Statistics.add(pack, MAX_TIME);
            this.activeTime = getTimeCounter() + DELETE_TIME;
            this.triggerTime = this.activeTime;
            deletePack();            
            return;
        }
        transPack(nSensors.get(choosen));
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        if (requireScan) {
            g.setColor(Color.RED);
            if (isUpdate) {
                g.setColor(Color.cyan);
            }
        }
        if (pack != null) {
            g.setColor(Color.YELLOW);
        }
        if (waiting) {
            g.setColor(Color.DARK_GRAY);
        }
        
        
        int [] temp = convertPo(coOrdinate);
        if (pack != null) {
            if ( (pack.getIDdes() == this.getID()) || (pack.getIDsou() == this.getID()) ) {
                int size = SIZE * 2;
                g.fillOval(temp[0] - size / 2, temp[1] - size / 2, size, size);
            }
            else {
                g.setColor(Color.YELLOW);
                g.fillOval(temp[0] - SIZE / 2, temp[1] - SIZE / 2, SIZE, SIZE);
            }
        }
        else {
            g.fillOval(temp[0] - SIZE / 2, temp[1] - SIZE / 2, SIZE, SIZE);
        }
         
        g.setColor(Color.BLACK);
        g.drawString("S" + String.valueOf(ID), temp[0], temp[1]); 
        /*
        if (isSentPath) {   
            if (nextSensor.havePack()) {
                g.setColor(Color.RED);
                int[] temp2 = convertPo(nextSensor.getLocation());
                g.drawLine(temp[0], temp[1], temp2[0], temp2[1]);      
            }
        }   */     
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
    public void transPack(Sensor s) {
        s.setPack(this.takePack());
        activeTime = getTimeCounter() + TRANFER_TIME;
        triggerTime = activeTime; // Tiep tuc update   
    }

    @Override
    public void setPack(Package pack) {
        this.pack = pack;
        this.pack.addPath(ID);
        this.hashOfMessage.add(pack.hashCode());
        this.triggerTime = getTimeCounter() + TRANFER_TIME;
        this.activeTime = MAX_TIME;

        if (pack.IDdes == ID) {            
            MapFrame.updateTextArea("\n" + pack.getMessage() + " - Tranfer successful!\n");
            this.activeTime = getTimeCounter() + TRANFER_TIME;
            this.triggerTime = this.activeTime;
            Statistics.add(pack, getTimeCounter() + TRANFER_TIME);
            this.getPack().deletePath();
            deletePack();
            
            this.isDes = true;
            // HaoNV clear path paint        
//            this.isSentPath = false;
//            ArrayList<Integer> path = pack.getPath();
//            for (Integer ite : path) {
//                int intv = ite.intValue();
//                nSensors.get(ite.intValue()).setSentPath(false);
//            }
        }
    }

    @Override
    public Map querrySensors() {
        return routeTable;
    }

}