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
public class Record {

    int IDsou, IDdes;
    int timeS, timeF;
    double dis;

    public Record(int IDsou, int IDdes, int timeS, int timeF, double dis) {
        this.IDsou = IDsou;
        this.IDdes = IDdes;
        this.timeS = timeS;
        this.timeF = timeF;
        this.dis = dis;
    }
}
