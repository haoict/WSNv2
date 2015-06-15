package com.wsn;

import static com.wsn.ConstValues.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class KeyListenerForMap extends KeyAdapter {

    private boolean leftKey, rightKey, upKey, downKey, stopKey, showPaths;

    public KeyListenerForMap() {
        leftKey = false;
        rightKey = false;
        upKey = false;
        downKey = false;
        stopKey = false;
        showPaths = false;
    }

    public boolean isShowPaths() {
        return showPaths;
    }

    public boolean isStopKey() {
        return stopKey;
    }

    public boolean isDownKey() {
        return downKey;
    }

    public boolean isLeftKey() {
        return leftKey;
    }

    public boolean isRightKey() {
        return rightKey;
    }

    public boolean isUpKey() {
        return upKey;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // System.out.println(e.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftKey = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightKey = true;
                break;
            case KeyEvent.VK_UP:
                upKey = true;
                break;
            case KeyEvent.VK_DOWN:
                downKey = true;
                break;
            case KeyEvent.VK_F3:
                stopKey = !stopKey;
                break;
            case KeyEvent.VK_F4:
                showPaths = !showPaths;
                break;
        }
        //   System.out.println("** " + e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftKey = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightKey = false;
                break;
            case KeyEvent.VK_UP:
                upKey = false;
                break;
            case KeyEvent.VK_DOWN:
                downKey = false;
                break;
            case KeyEvent.VK_A:
                SCAN_TIME += 5;
                break;
            case KeyEvent.VK_S:
                SCAN_TIME += 5;
                break;
        }
    }

    public void adjust() {
        if (upKey) {
            TOP_MARGIN -= DELTA;
        }
        if (downKey) {
            TOP_MARGIN += DELTA;
        }
        if (leftKey) {
            LEFT_MARGIN += DELTA;
        }
        if (rightKey) {
            LEFT_MARGIN -= DELTA;
        }
        //      System.out.println("** " + TOP_MARGIN + " * "+LEFT_MARGIN);
    }

}
