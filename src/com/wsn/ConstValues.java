/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wsn;

/**
 *
 * @author Mr CUONG
 */
enum SensorType {
    DEFAULT_SENSOR,
    SMART_SENSOR
}

public class ConstValues {            
    public static final int SIZE = 10;
    public static int SCALE = 100;
    public static final double ZOOM_IN = 1.1;
    public static final double ZOOM_OUT = 0.9;
    public static int TOP_MARGIN = 0;
    public static int LEFT_MARGIN = 0;
    public static final int DELTA = 10;
    public static final double EPS = 0.000001;
    public static final int SIZE_OF_MAP = 4;           
    public static final int CAN_NOT_REACH = 1000;
    public static final int TIME_DELAY = 20;
    public static int addPoly;
    public static int SCAN_TIME = 5;
    public static final int TRANFER_TIME = 15;
    public static final int MESSAGE_INTERVAL_TIME = 50;
    public static final int UPDATE_TIME = 1;
    public static final int WAIT_TIME = 5;
    public static final int TIME_EXEED = 20;
    public static final int DELETE_TIME = 1;
    public static final int MAX_TIME = 100000000;
}
