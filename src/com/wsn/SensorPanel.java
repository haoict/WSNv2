package com.wsn;

import static com.wsn.ConstValues.*;
import static com.wsn.ConvertToGraphic.*;
import static com.wsn.Geometry.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SensorPanel extends JPanel implements ActionListener {

    private List<Sensor> sensors = new ArrayList<>();
    private Point2D mouseCoords;
    private final List<Line> lines = new ArrayList<>();
    private final List<ConvexPoly> polies = new ArrayList<>();
    KeyListenerForMap keyListener;
    Timer timer;
    private static int timeCounter;
    private Line tempLine;
    private Point2D lastPoint;
    private TextArea textArea, textArea2;
    private PrintWriter out;
    private boolean autoCreateMessage;
    private boolean isAddSensor;
    private JPopupMenu popupMenu = new JPopupMenu();
    private MapFrame parentFrame;

    public boolean overLoad(Line line) {
        for (Sensor sensor : sensors) {
            if (isPoint2DOnLine(line, sensor.getLocation())) {
                return true;
            }
        }
        return false;
    }

    public boolean isInsideWithSensors(ConvexPoly t) {
        for (Sensor sensor : sensors) {
            if (t.isInside(sensor.getLocation())) {
                return true;
            }
        }
        return false;
    }

    public static int getTimeCounter() {
        return timeCounter;
    }

    public void addWall(Line l) {
        lines.add(l);
    }

    public void addPoly(ConvexPoly p) {
        polies.add(p);
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    int NumOfSensors() {
        return sensors.size();
    }

    void clear() {
        sensors.clear();
        lines.clear();
        polies.clear();
    }

    public SensorPanel() {

    }

    void reqFocus() {

    }

    public SensorPanel(final MapFrame parentFrame) {
        super();
        this.parentFrame = parentFrame;
        
        timeCounter = 0;
        setPreferredSize(new Dimension(400, 300));
        keyListener = new KeyListenerForMap();

        // Init key navigation
        JButton navigation = new JButton("Key Navigation");
        navigation.addKeyListener(keyListener);
        this.addKeyListener(keyListener);
        add(navigation);
        navigation.requestFocusInWindow();
        textArea2 = new TextArea(1, 12);

        // Mouse action
        addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                if (notches < 0) {
                    SCALE = SCALE <= 600 ? (int) (SCALE * ZOOM_IN) : 660;
                    //MapFrame.updateTextArea("Scale:  " + SCALE);
                    parentFrame.getjLabelScale().setText("Scale: " + SCALE);
                } else {
                    SCALE = (int) (SCALE * ZOOM_OUT);
                    if (SCALE < 10) {
                        SCALE = 10;
                    }
                    //MapFrame.updateTextArea("Scale:  " + SCALE);
                    parentFrame.getjLabelScale().setText("Scale: " + SCALE);
                }
            }

        });
        addMouseListener(new MouseAdapter() {

            private boolean OnSendMessage(Sensor sensor) {
                if (!sensor.isAvailable()) {
                    JOptionPane.showMessageDialog(null, "Sensor" + sensor.getID() + " is not available!");
                    return false;
                }

                JTextField messageText = new JTextField();
                JTextField destination = new JTextField();
                JPanel sendMessagePanel = new JPanel();
                sendMessagePanel.add(new JLabel("Message: "));
                sendMessagePanel.add(messageText);
                //sendMessagePanel.add(Box.createVerticalStrut(1)); // a spacer
                sendMessagePanel.add(new JLabel("Destination:"));
                sendMessagePanel.add(destination);
                sendMessagePanel.setLayout(new BoxLayout(sendMessagePanel, BoxLayout.Y_AXIS));

                int result = JOptionPane.showConfirmDialog(null, sendMessagePanel,
                        "Please Enter Message and Destination Values", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    Integer des;
                    try {
                        des = new Integer(destination.getText());
                    } catch (NumberFormatException t) {
                        JOptionPane.showMessageDialog(null, "Destination must be a number");
                        return false;
                    }

                    if (des.intValue() >= sensors.size()) {
                        JOptionPane.showMessageDialog(null, "Destination is not exists");
                        return false;
                    } else {
                        MapFrame.updateTextArea("Send message: " + sensor.getID() + " -> " + des.toString() + "\n");                        
                        sensor.setPack(new Package(messageText.getText(), sensor.getID(), des.intValue(), sensor.getLocation(), sensors.get(des.intValue()).getLocation()));
                    }
                }
                return true;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Point2D point = convertToPo(e.getX(), e.getY());

                if (parentFrame.isAddingSensor()) {
                    if (parentFrame.isIsDefaultSensor()) {
                        addSensor(SensorType.DEFAULT_SENSOR, point);
                    } else {
                        addSensor(SensorType.SMART_SENSOR, point);
                    }
                    return;
                }

                if (addPoly >= 0) {
                    if (addPoly == 0) {
                        polies.add(new ConvexPoly());
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        textArea2.setText("Finished!");
                        if (addPoly > 2) {
                            lines.add(new Line(polies.get(polies.size() - 1).getPoint(addPoly - 1),
                                    polies.get(polies.size() - 1).getPoint(0)));
                            addPoly = -1;
                            return;
                        }
                    }
                    if (addPoly > 1) {
                        Line ab = new Line(polies.get(polies.size() - 1).getPoint(addPoly - 2),
                                polies.get(polies.size() - 1).getPoint(addPoly - 1));
                        Line xy = new Line(polies.get(polies.size() - 1).getPoint(0),
                                polies.get(polies.size() - 1).getPoint(1));
                        Line bc = new Line(polies.get(polies.size() - 1).getPoint(addPoly - 1), point);
                        if (!isPoint2DRightOfLine(ab, point)) {
                            textArea2.setText("Invalid Point!");
                            return;
                        }
                        if (!isPoint2DRightOfLine(xy, point)) {
                            textArea2.setText("Invalid Point!!");
                            return;
                        }
                        if (!isPoint2DRightOfLine(bc, polies.get(polies.size() - 1).getPoint(0))) {
                            textArea2.setText("Invalid Point!!!");
                            return;
                        }
                        ConvexPoly t = new ConvexPoly();
                        t.addPoint(polies.get(polies.size() - 1).getPoint(0));
                        t.addPoint(polies.get(polies.size() - 1).getPoint(addPoly - 1));
                        t.addPoint(point);
                        if (isInsideWithSensors(t)) {
                            JOptionPane.showMessageDialog(null, "It is not valid point because it will be overload to sensor");
                            return;
                        }
                    }
                    textArea2.setText("Valid Point!");
                    polies.get(polies.size() - 1).addPoint(point);
                    addPoly++;
                    if (addPoly > 1) {
                        lines.add(new Line(polies.get(polies.size() - 1).getPoint(addPoly - 2),
                                polies.get(polies.size() - 1).getPoint(addPoly - 1)));
                    }

                    return;
                }

                for (final Sensor sensor : sensors) {
                    if (0.50 * SIZE / SCALE > point.distance(sensor.getLocation())) {

                        if (SwingUtilities.isRightMouseButton(e)) {
                            //Sensor msensor = sensor;
                            // Init pop-up menu for right click
                            popupMenu.removeAll();
                            JMenuItem popupItem = new JMenuItem("Send Message");
                            RightClick rc = new RightClick(sensor);
                            popupItem.addActionListener(rc);
                            popupMenu.add(popupItem);
                            popupItem = new JMenuItem("Property");
                            popupItem.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    SensorInfo si = new SensorInfo(sensor);
                                    si.setVisible(true);
                                }
                            });
                            popupMenu.add(popupItem);
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                            if (!rc.isOK()) {
                                break;
                            }
                            return;
                        }

                        if (!OnSendMessage(sensor)) {
                            break;
                        }

                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (parentFrame.isAddingWall()) {
                    lastPoint = convertToPo(e.getX(), e.getY());
                } else {

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (parentFrame.isAddingWall()) {
                    tempLine = null;
                    Point2D nextPoint;
                    nextPoint = convertToPo(e.getX(), e.getY());
                    lines.add(new Line(lastPoint, nextPoint));
                } else {
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (parentFrame.isAddingWall()) {
                    Line a = new Line(lastPoint, convertToPo(e.getX(), e.getY()));
                    if (!overLoad(a)) {
                        tempLine = new Line(lastPoint, convertToPo(e.getX(), e.getY()));

                    } else {
                        System.out.println("Overload to sensors");
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                Double x = (double) (e.getX() - LEFT_MARGIN) / SCALE;
                Double y = (double) (e.getY() + TOP_MARGIN) / SCALE;
                DecimalFormat df = new DecimalFormat("#.##");
                parentFrame.getjLabelX().setText("X: " + df.format(x.doubleValue()));
                parentFrame.getjLabelY().setText("Y: " + df.format(y.doubleValue()));
            }
        });

        revalidate();
        autoCreateMessage = false;
        timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(), 100, 100);
    }

    public void setAutoCreateMessage(boolean autoCreateMessage) {
        this.autoCreateMessage = autoCreateMessage;
    }

    public boolean isAutoCreateMessage() {
        return autoCreateMessage;
    }

    @Override
    public void paintComponent(Graphics g) {
        /* Call the original implementation of this method */
        super.paintComponent(g);
        //g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        g.setColor(Color.MAGENTA);
        g.fillRect(0, getHeight() - SIZE / 2, SCALE, SIZE / 2);

        g.setColor(Color.GREEN);
        for (Sensor sensor : sensors) {
            if (sensor.havePack()) {
                if (sensor.isRequireScan()) {
                    int[] point = convertPo(sensor.getLocation());
                    double percentage = 1.0 * (sensor.getTriggerTime() - timeCounter) / SCAN_TIME;
                    int process = (int) (360 * percentage);
                    g.setColor(Color.GREEN);
                    g.fillArc(point[0] - SCALE, point[1] - SCALE, 2 * SCALE, 2 * SCALE, 90, process);
                }
                Package a = sensor.getPack();
                if (keyListener.isShowPaths()) {
                    for (int i = 0; i < a.path.size() - 2; i++) {
                        Point2D s = new Point2D(sensors.get(a.path.get(i)).getLocation());
                        Point2D f = new Point2D(sensors.get(a.path.get(i + 1)).getLocation());
                        int[] temp0 = convertPo(s);
                        int[] temp1 = convertPo(f);
                        g.setColor(Color.BLUE);
                        g.drawLine(temp0[0], temp0[1], temp1[0], temp1[1]);
                    }

                }
                if (a.path.size() < 2) continue;
                g.setColor(Color.red);
                Point2D s = new Point2D(sensors.get(a.path.get(a.path.size() - 2)).getLocation());
                Point2D f = new Point2D(sensors.get(a.path.get(a.path.size() - 1)).getLocation());
                int[] temp0 = convertPo(s);
                int[] temp1 = convertPo(f);
                g.drawLine(temp0[0], temp0[1], temp1[0], temp1[1]);
            }
        }

        for (ConvexPoly poly : polies) {
            poly.draw(g);
        }
        for (Sensor sensor : sensors) {
            sensor.draw(g);
        }
        for (Line line : lines) {
            line.draw(g);
        }
        if (tempLine != null) {
            tempLine.draw(g);
        }
    }

    void addSensor(SensorType type, Point2D add) {
        for (int i = 0; i < polies.size(); i++) {
            if (polies.get(i).isInside(add)) {
                textArea2.setText("S invalid!");
                return;
            }
        }
//        if (DefaultMode)
//            sensors.add(new DefaultSensor(add, sensors.size()));
//        else
        if (type == SensorType.DEFAULT_SENSOR) {
            sensors.add(new DefaultSensor(add, sensors.size()));
        } else if (type == SensorType.SMART_SENSOR) {
            sensors.add(new SensorRT(add, sensors.size()));
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 405, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    void writeToFile(String fileName) throws IOException {
        try (FileWriter f0 = new FileWriter(fileName)) {
            f0.write(sensors.size() + "\n");
            for (Sensor sensor : sensors) {
                if (sensor instanceof SensorRT) {
                    String output = sensor.getLocation().toString() + " 1\n";
                    f0.write(output);
                } else {
                    String output = sensor.getLocation().toString() + " 0\n";
                    f0.write(output);
                }
            }
            f0.write(lines.size() + "\n");
            for (Line line : lines) {
                String output = line.toString() + "\n";
                f0.write(output);
            }
            f0.write(polies.size() + "\n");
            for (ConvexPoly poly : polies) {
                String output = poly.toString() + "\n";
                f0.write(output);
            }
            f0.flush();
        }
    }

    void loadFromFile(String fileName) throws IOException {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String line;
            try {
                line = br.readLine();
                int n = Integer.parseInt(line);
                for (int i = 0; i < n; i++) {
                    line = br.readLine();
                    String[] sItems;
                    sItems = line.split(" ");
                    double x = Double.parseDouble(sItems[0]);
                    double y = Double.parseDouble(sItems[1]);
                    int t = Integer.parseInt(sItems[2]);
                    if (t == 1) {
                        addSensor(SensorType.SMART_SENSOR, new Point2D(x, y));
                    } else {
                        addSensor(SensorType.DEFAULT_SENSOR, new Point2D(x, y));
                    }
                }
                line = br.readLine();
                n = Integer.parseInt(line);
                for (int i = 0; i < n; i++) {
                    line = br.readLine();
                    addWall(new Line(line));
                }
                line = br.readLine();
                n = Integer.parseInt(line);
                for (int i = 0; i < n; i++) {
                    line = br.readLine();
                    addPoly(new ConvexPoly(line));
                }
            } catch (IOException | NumberFormatException | ParseException e) {
                Logger.getLogger(MapFrame.class.getName()).log(Level.SEVERE, null, e);
            }
            br.close();
        } catch (FileNotFoundException e) {
            Logger.getLogger(MapFrame.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException e) {
            Logger.getLogger(MapFrame.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    class ScheduleTask extends TimerTask {

        List getListNS(int ID) {
            List<Sensor> tsensors = new ArrayList<>();
            for (Sensor sensor : sensors) {
                if (sensor.getID() != ID) {
                    if (sensors.get(ID).getLocation().inDistance(sensor.getLocation())) {
                        Line tline = new Line(sensors.get(ID).getLocation(), sensor.getLocation());
                        if (!tline.areIntersect(lines)) {
                            tsensors.add(sensor);
                        }

                    }
                }
            }
            return tsensors;
        }

        @Override
        public void run() {
            keyListener.adjust();
            if (keyListener.isStopKey()) {
                return;
            }
            timeCounter++;
            for (Sensor sensor : sensors) {
                if (sensor.getTriggerTime() == timeCounter) {
                    if (sensor.isRequireScan()) {
                        sensor.setNeighbourSensors(getListNS(sensor.getID()));
                        // sensor.turnOffScan();
                    }
                    sensor.run();
                }
            }
            if (autoCreateMessage) {
                if (sensors.size() > 2) {
                    if (timeCounter % MESSAGE_INTERVAL_TIME == 0) {
                        Random rand = new Random();
                        int sou = rand.nextInt(sensors.size());
                        if (!sensors.get(sou).isAvailable()) {
                            return;
                        }
                        int des = rand.nextInt(sensors.size());
                        if (sou == des) {
                            return;
                        }
                        String message = "Default Message: " + sou + "->" + des;                        
                        MapFrame.updateTextArea("\nDefault Message: " + sou + "->" + des + "\n");
                        sensors.get(sou).setPack(new Package(message, sou, des, sensors.get(sou).getLocation(), sensors.get(des).getLocation()));
                    }
                }
            }
        }
    }

    class RightClick implements ActionListener {

        Sensor sensor;
        private boolean isOK;

        public boolean isOK() {
            return isOK;
        }

        public RightClick(Sensor sensor) {
            this.sensor = sensor;
            isOK = false;
        }

        public void actionPerformed(ActionEvent e) {
            if (!sensor.isAvailable()) {
                JOptionPane.showMessageDialog(null, "Sensor" + sensor.getID() + " is not available!");
                isOK = false;
            }

            JTextField messageText = new JTextField();
            JTextField destination = new JTextField();
            JPanel sendMessagePanel = new JPanel();
            sendMessagePanel.add(new JLabel("Message: "));
            sendMessagePanel.add(messageText);
            //sendMessagePanel.add(Box.createVerticalStrut(1)); // a spacer
            sendMessagePanel.add(new JLabel("Destination:"));
            sendMessagePanel.add(destination);
            sendMessagePanel.setLayout(new BoxLayout(sendMessagePanel, BoxLayout.Y_AXIS));

            int result = JOptionPane.showConfirmDialog(null, sendMessagePanel,
                    "Please Enter Message and Destination Values", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Integer des;
                try {
                    des = new Integer(destination.getText());
                } catch (NumberFormatException t) {
                    JOptionPane.showMessageDialog(null, "Destination must be a number");
                    isOK = false;
                    return;
                }

                if (des.intValue() >= sensors.size()) {
                    JOptionPane.showMessageDialog(null, "Destination is not exists");
                    isOK = false;
                    return;
                } else {
                    sensor.setPack(new Package(messageText.getText(), sensor.getID(), des.intValue(), sensor.getLocation(), sensors.get(des.intValue()).getLocation()));
                }
            }
            isOK = true;
            return;
        }

    }
}
