package util;

public class Point2D implements Comparable<Point2D>{
    public double x,y;

    public Point2D(double x, double y){
        this.x=x;
        this.y=y;
    }

    @Override
    public int compareTo(Point2D o) {
        if (this.y != o.y) {
            return java.lang.Double.compare(o.y, this.y);
        } else {
            return java.lang.Double.compare(this.x, o.x);
        }
    }
}
