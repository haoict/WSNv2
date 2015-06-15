package com.wsn;

public class Geometry {

    public static final double EPSILON = 0.000001;

    /**
     * Calculate the cross product of two points.
     *
     * @param a startP point
     * @param b endP point
     * @return the value of the cross product
     */
    public static double crossProduct(Point2D a, Point2D b) {
        return a.x * b.y - b.x * a.y;
    }

    /**
     * Checks if a Point2D is on a line
     *
     * @param a line (interpreted as line, although given as line segment)
     * @param b point
     * @return <code>true</code> if point is on line, otherwise
     * <code>false</code>
     */
    public static boolean isPoint2DOnLine(Line a, Point2D b) {
        // Move the image, so that a.startP is on (0|0)
        Line aTmp = new Line(new Point2D(0, 0), new Point2D(
                a.endP.x - a.startP.x, a.endP.y - a.startP.y));
        Point2D bTmp = new Point2D(b.x - a.startP.x, b.y - a.startP.y);
        double r = crossProduct(aTmp.endP, bTmp);
        return Math.abs(r) < EPSILON;
    }

    /**
     * Checks if a point is right of a line. If the point is on the line, it is
     * not right of the line.
     *
     * @param a line segment interpreted as a line
     * @param b the point
     * @return <code>true</code> if the point is right of the line,
     * <code>false</code> otherwise
     */
    public static boolean isPoint2DRightOfLine(Line a, Point2D b) {
        // Move the image, so that a.startP is on (0|0)
        Line aTmp = new Line(new Point2D(0, 0), new Point2D(
                a.endP.x - a.startP.x, a.endP.y - a.startP.y));
        Point2D bTmp = new Point2D(b.x - a.startP.x, b.y - a.startP.y);
        return crossProduct(aTmp.endP, bTmp) < 0;
    }

    
    public static boolean is2LineSegmentCross(Line a, Line b) {
        return  (isPoint2DRightOfLine(a, b.startP) ^ isPoint2DRightOfLine(a, b.endP))&&
                (isPoint2DRightOfLine(b, a.startP) ^ isPoint2DRightOfLine(b, a.endP)) ;
    }   
}