package com.wsn;

import static com.wsn.SensorPanel.getTimeCounter;
import java.util.ArrayList;
import java.util.Objects;

public class Package {

    final String message;
    ArrayList<Integer> path = new ArrayList<>();
    int IDdes;
    int state;
    Point2D souLocation;
    Point2D desLocation;
    int timeS;

    public Point2D getSouLocation() {
        return souLocation;
    }

  
    public Point2D getDesLocation() {
        return desLocation;
    }
    public int getTimeS() {
        return timeS;
    }

    // state = 0 still transfering, state = 1 success, state = -1 unsucess
    public int getIDsou() {
        return path.get(0);
    }

    public void addPath(int id) {
        path.add(id);
    }
    
    public ArrayList<Integer> getPath() {
        return path;
    }
    
    public void deletePath() {
        path.removeAll(path);
    }

    public String getMessage() {
        return message;
    }

    public int getIDcur() {
        return path.get(path.size() - 1);
    }

    public int getIDdes() {
        return IDdes;
    }

    public void setIDdes(int IDdes) {
        this.IDdes = IDdes;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Package(String message, int IDsou, int IDdes, Point2D souLocation, Point2D desLocation) {
        this.message = message;        
        this.IDdes = IDdes;
        this.state = 0;
        this.souLocation = souLocation;
        this.desLocation = desLocation;
        this.timeS = getTimeCounter();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Objects.hashCode(this.message);
        hash = 73 * hash + this.IDdes;
        hash = 73 * hash + this.timeS;
        return hash;
    }

    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Package other = (Package) obj;
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        return this.IDdes == other.IDdes;
    }

}
