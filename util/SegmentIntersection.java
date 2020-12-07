package util;

import java.awt.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class SegmentIntersection {
    public final int INNERPOINT = 1;
    public final int ENDPOINT = 2;

    private final int N;
    private Segment[] S;
    private ArrayList<Intersection> intersections;
    private RedBlackBST<Double, Segment> status;
    private RedBlackBST<Point2D, Segment> events;

    private class Intersection {
        private ArrayList<Integer> segments;
        private ArrayList<Integer> intersectionTypes;
        private final Point2D intersectionPoint;

        public Intersection(Point2D point) {
            segments = new ArrayList<>();
            intersectionTypes = new ArrayList<>();
            intersectionPoint = point;
        }

        public void addSegment(int segment, int intersectionType) {
            segments.add(segment);
            intersectionTypes.add(intersectionType);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Intersection at (%.2f, %.2f)\n",intersectionPoint.x, intersectionPoint.y));
            for (int i = 0; i < segments.size(); i++) {
                sb.append(String.format("Segment %d: %s\n",
                        segments.get(i), intersectionTypes.get(i) == INNERPOINT ? "Innerpoint" : "Endpoint"));
            }
            return sb.toString();
        }

    }

    public SegmentIntersection(In in) {
        if (in == null) throw new IllegalArgumentException("argument is null");

        try {
            N = in.readInt();
            S = new Segment[N];
            status = new RedBlackBST<>();
            events = new RedBlackBST<>();
            intersections = new ArrayList<>();

            for (int i = 0; i < N; i++) {
                double x1 = in.readDouble();
                double y1 = in.readDouble();
                double x2 = in.readDouble();
                double y2 = in.readDouble();
                Segment s = new Segment(i,new Point2D(x1, y1), new Point2D(x2, y2));
                S[i] = s;
            }
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in SegmentIntersection constructor", e);
        }
    }

    public void getIntersections(){
        for (Segment s: S){
            events.put(s.upper(),s);
            events.put(s.lower(),s);
        }
        while (!events.isEmpty()){
            Segment s=events.get(events.min());
            Point2D p = events.min();
            events.deleteMin();
            handleEventPoint(s, p);
        }
    }

    private void handleEventPoint(Segment s, Point2D p){
        if(s.isSinglePoint()){
            //Aqui se evalua el handle de las intyersecciones
            //hacer eliminaciones para conseguir los vecinos
            Double leftKey=status.floor(s.upper().x);
            Double rigthKey=status.ceiling(s.upper().x);
            Segment leftNode=status.get(leftKey);
            Segment rigthNode=status.get(rigthKey);
            //eliminar los que intercambiariamos y conseguir vecinos
            status.delete(leftKey);
            status.delete(rigthKey);
            Segment leftNeig = getLeftNeighbor(leftNode);
            Segment rigthNeig = getRightNeighbor(rigthNode);
            status.put(leftKey,rigthNode);
            status.put(rigthKey,leftNode);
            Intersection i=new Intersection(s.upper());
            if(leftNode.upper().equals(s.upper())||leftNode.lower().equals(s.upper()))
                i.addSegment(leftNode.id(),ENDPOINT);
            else
                i.addSegment(leftNode.id(),INNERPOINT);

            if(rigthNode.upper().equals(s.upper())||rigthNode.lower().equals(s.upper()))
                i.addSegment(rigthNode.id(),ENDPOINT);
            else
                i.addSegment(rigthNode.id(),INNERPOINT);
            intersections.add(i);

            //checar loos nuevos vecinos e intersecciones (punto 2 y 3 del slide)
            if(leftNeig!=null&&doIntersect(leftNeig,rigthNode)){
                double ms=(rigthNode.upper().y-rigthNode.lower().y)/(rigthNode.upper().x-rigthNode.lower().x);
                double ml=(leftNeig.upper().y-leftNeig.lower().y)/(leftNeig.upper().x-leftNeig.lower().x);
                double x=(ms*rigthNode.upper().x-ml*leftNeig.upper().x+leftNeig.upper().y-rigthNode.upper().y)/
                        (ms-ml);
                double y=(ms*ml*(leftNeig.upper().x-rigthNode.upper().x)+ml*rigthNode.upper().y-ms*leftNeig.upper().y)/
                        (ml-ms);
                Segment inter= new Segment(-1,new Point2D(x,y),new Point2D(x,y));
                events.put(inter.upper(),inter);
            }
            if(rigthNeig!=null&&doIntersect(rigthNeig,leftNode)){
                double ms=(leftNode.upper().y-leftNode.lower().y)/(leftNode.upper().x-leftNode.lower().x);
                double mr=(rigthNeig.upper().y-rigthNeig.lower().y)/(rigthNeig.upper().x-rigthNeig.lower().x);
                double x=(ms*leftNode.upper().x-mr*rigthNeig.upper().x+rigthNeig.upper().y-leftNode.upper().y)/
                        (ms-mr);
                double y=(ms*mr*(rigthNeig.upper().x-leftNode.upper().x)+mr*leftNode.upper().y-ms*rigthNeig.upper().y)/
                        (mr-ms);
                Segment inter=new Segment(-1,new Point2D(x,y),new Point2D(x,y));
                events.put(inter.upper(),inter);
            }

        } else if(s.upper().equals(p)){
            //Segment leftNeig = status.get(status.floor(s.upper().x));
            Segment leftNeig = getLeftNeighbor(s);
            //Segment rigthNeig = status.get(status.ceiling(s.upper().x));
            Segment rigthNeig = getRightNeighbor(s);
            status.put(s.upper().x, s);
            if(leftNeig!=null && doIntersect(leftNeig, s)){
                double ms=(s.upper().y-s.lower().y)/(s.upper().x-s.lower().x);
                double ml=(leftNeig.upper().y-leftNeig.lower().y)/(leftNeig.upper().x-leftNeig.lower().x);
                double x=(ms*s.upper().x-ml*leftNeig.upper().x+leftNeig.upper().y-s.upper().y)/
                        (ms-ml);
                double y=(ms*ml*(leftNeig.upper().x-s.upper().x)+ml*s.upper().y-ms*leftNeig.upper().y)/
                        (ml-ms);
                Segment inter=new Segment(-1,new Point2D(x,y),new Point2D(x,y));
                events.put(inter.upper(),inter);
                /*
                Intersection in=new Intersection(inter.upper());
                in.addSegment(1,1);
                intersections.add(in);
                */
            }
            if(rigthNeig!=null && doIntersect(rigthNeig, s)){
                double ms=(s.upper().y-s.lower().y)/(s.upper().x-s.lower().x);
                double mr=(rigthNeig.upper().y-rigthNeig.lower().y)/(rigthNeig.upper().x-rigthNeig.lower().x);
                double x=(ms*s.upper().x-mr*rigthNeig.upper().x+rigthNeig.upper().y-s.upper().y)/
                        (ms-mr);
                double y=(ms*mr*(rigthNeig.upper().x-s.upper().x)+mr*s.upper().y-ms*rigthNeig.upper().y)/
                        (mr-ms);
                Segment inter=new Segment(-1,new Point2D(x,y),new Point2D(x,y));
                events.put(inter.upper(),inter);
                 /*
                Intersection in=new Intersection(inter.upper());
                in.addSegment(1,1);
                intersections.add(in);
                */
            }
        }else if(s.lower().equals(p)){
            //Segment leftNeig = status.get(status.floor(s.lower().x));
            //Segment rigthNeig = status.get(status.ceiling(s.lower().x));
            Segment leftNeig = getLeftNeighbor(s);
            Segment rigthNeig = getRightNeighbor(s);
            status.delete(s.upper().x);
            status.delete(s.lower().x);
            if(leftNeig!=null && doIntersect(leftNeig, s)){
                double ms=(s.upper().y-s.lower().y)/(s.upper().x-s.lower().x);
                double ml=(leftNeig.upper().y-leftNeig.lower().y)/(leftNeig.upper().x-leftNeig.lower().x);
                double x=(ms*s.upper().x-ml*leftNeig.upper().x+leftNeig.upper().y-s.upper().y)/
                        (ms-ml);
                double y=(ms*ml*(leftNeig.upper().x-s.upper().x)+ml*s.upper().y-ms*leftNeig.upper().y)/
                        (ml-ms);
                Segment inter=new Segment(-1,new Point2D(x,y),new Point2D(x,y));
                events.put(inter.upper(),inter);
                /*
                Intersection in=new Intersection(inter.upper());
                in.addSegment(1,1);
                intersections.add(in);
                */
            }
            if(rigthNeig!=null && doIntersect(rigthNeig, s)){
                double ms=(s.upper().y-s.lower().y)/(s.upper().x-s.lower().x);
                double mr=(rigthNeig.upper().y-rigthNeig.lower().y)/(rigthNeig.upper().x-rigthNeig.lower().x);
                double x=(ms*s.upper().x-mr*rigthNeig.upper().x+rigthNeig.upper().y-s.upper().y)/
                        (ms-mr);
                double y=(ms*mr*(rigthNeig.upper().x-s.upper().x)+mr*s.upper().y-ms*rigthNeig.upper().y)/
                        (mr-ms);
                Segment inter=new Segment(-1,new Point2D(x,y),new Point2D(x,y));
                events.put(inter.upper(),inter);
                 /*
                Intersection in=new Intersection(inter.upper());
                in.addSegment(1,1);
                intersections.add(in);
                */
            }
        }
    }

    private Segment getRightNeighbor (Segment s) {
        Segment rightNeighbor = null;
        try {
            if (!status.isEmpty()) {
                Double rightKey = status.ceiling(s.upper().x);
                if (rightKey != null) rightNeighbor = status.get(rightKey);
            }
        }catch (Exception ex){

        }
        return rightNeighbor;
    }

    private Segment getLeftNeighbor(Segment s) {
        Segment leftNeighbor = null;
        try {
            if (s.upper().x != status.max()) {
                Double leftKey = status.floor(s.upper().x);
                if (leftKey != null) leftNeighbor = status.get(leftKey);
            }
        }catch (Exception ex){

        }
        return leftNeighbor;
    }

    private boolean doIntersect(Segment a, Segment b){
        double x1=a.lower().x;
        double y1=a.lower().y;
        double x2=a.upper().x;
        double y2=a.upper().y;
        double det1=(x1*y2-x2*y1)-(x1*b.lower().y-y1*b.lower().x)+(x2*b.lower().y-y2*b.lower().x);
        double det2=(x1*y2-x2*y1)-(x1*b.upper().y-y1*b.upper().x)+(x2*b.upper().y-y2*b.upper().x);
        return (det1<0&&det2>0)||(det1>0&&det2<0);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Segment s: S) {
            sb.append(String.format("Segment %d: %s\n", i++, s.toString()));
        }
        for (Intersection intersection: intersections) {
            sb.append(intersection.toString());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        In in = new In("./SegmentsTest.txt");
        SegmentIntersection si = new SegmentIntersection(in);
        si.getIntersections();
        StdOut.println(si);
    }


}

