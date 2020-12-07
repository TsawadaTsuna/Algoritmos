package util;

import java.awt.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Polygon {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V;
    private Bag<PolygonEdge> edges;

    /**
     * Initializes an empty polygon with {@code V} vertices.
     *
     * @param  V the number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public Polygon(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
        this.V = V;
        edges = new Bag<>();
    }

    /**
     * Initializes a polygon from an input stream.
     * The format is the number of vertices <em>V</em>,
     * followed by <em>E</em> pairs of points,
     * with each entry separated by whitespace.
     *
     * @param  in the input stream
     * @throws IllegalArgumentException if {@code in} is {@code null}
     * @throws IllegalArgumentException if the endpoints of any edge are not in prescribed range
     * @throws IllegalArgumentException if the number of vertices is negative
     */
    public Polygon(In in) {
        if (in == null) throw new IllegalArgumentException("argument is null");

        try {
            V = in.readInt();
            edges = new Bag<>();

            if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
            int vx = in.readInt();
            int vy = in.readInt();
            Point v = new Point(vx, vy);
            Point tmp = v;
            for (int i = 1; i < V; i++) {
                int wx = in.readInt();
                int wy = in.readInt();
                Point w = new Point(wx, wy);
                PolygonEdge e = new PolygonEdge(tmp, w);
                addEdge(e);
                tmp = w;
            }
            addEdge(new PolygonEdge(tmp, v));
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in EdgeWeightedGraph constructor", e);
        }

    }

    /**
     * Initializes a new polygon that is a deep copy of {@code G}.
     *
     * @param  G the polygon to copy
     */
    public Polygon(Polygon G) {
        this(G.V());
        for (int v = 0; v < G.V(); v++) {
            // reverse so that adjacency list is in same order as original
            Stack<PolygonEdge> reverse = new Stack<>();
            for (PolygonEdge e : G.edges) {
                reverse.push(e);
            }
            for (PolygonEdge e : reverse) {
                edges.add(e);
            }
        }
    }


    /**
     * Returns the number of vertices in this polygon.
     *
     * @return the number of vertices in this polygon
     */
    public int V() {
        return V;
    }

    /**
     * Adds the polygon edge {@code e} to this polygon.
     *
     * @param  e the edge
     */
    public void addEdge(PolygonEdge e) {
        edges.add(e);
    }

    /**
     * Returns all edges in this polygon.
     * To iterate over the edges in this polygon, use foreach notation:
     *
     * @return all edges in this polygon, as an iterable
     */
    public Iterable<PolygonEdge> edges() {
        return edges;
    }

    public Iterable<Point> vertices() {
        Bag<Point> list = new Bag<>();
        for (PolygonEdge e: edges) {
            Point v = e.either();
            list.add(v);
        }
        return list;
    }

    public boolean isInside(Point q){
        int intersecciones=0;
        for(PolygonEdge pe:edges){
            Point a= pe.either();
            Point b=pe.other(pe.either());
            if(a.y>b.y){
                Point tmp=a;
                a=b;
                b=tmp;
            }
            if(a.y<=q.y&&b.y>q.y){
                int[][] MDet=new int[3][3];
                MDet[0][0] = a.x;
                MDet[0][1]=a.y;
                MDet[0][2]=1;
                MDet[1][0] = b.x;
                MDet[1][1]=b.y;
                MDet[1][2]=1;
                MDet[2][0] = q.x;
                MDet[2][1]=q.y;
                MDet[2][2]=1;
                int det=(a.x*b.y-b.x*a.y)-(a.x*q.y-a.y*q.x)+(b.x*q.y-b.y*q.x);
                if(det<0) intersecciones++;
            }
        }
        return intersecciones%2==1;
    }

    public Polygon convexHull(Polygon p){
        PolygonEdge[] puntos = new PolygonEdge[p.edges.size()];
        int cont=0;
        for(PolygonEdge pe:p.edges){
            puntos[cont++]=pe;
        }
        //sorting
        XMergeSort(puntos,0,puntos.length-1);

        ArrayList<Point> lUpper=new ArrayList<>();
        lUpper.add(puntos[0].either());
        lUpper.add(puntos[1].either());
        for(int i=2;i<puntos.length;i++){
            lUpper.add(puntos[i].either());
            int det=0;
            if(lUpper.size()>2) {
                int x1 = lUpper.get(lUpper.size() - 3).x;
                int y1 = lUpper.get(lUpper.size() - 3).y;
                int x2 = lUpper.get(lUpper.size() - 2).x;
                int y2 = lUpper.get(lUpper.size() - 2).y;
                int x3 = lUpper.get(lUpper.size() - 1).x;
                int y3 = lUpper.get(lUpper.size() - 1).y;
                det = (x1 * y2 - x2 * y1) - (x1 * y3 - x3 * y1) + (x2 * y3 - x3 * y2);
            }
            while (lUpper.size()>2&&det<0){
                Point r=lUpper.get(lUpper.size()-2);
                lUpper.remove(r);
                if(lUpper.size()>2) {
                    int x1 = lUpper.get(lUpper.size() - 3).x;
                    int y1 = lUpper.get(lUpper.size() - 3).y;
                    int x2 = lUpper.get(lUpper.size() - 2).x;
                    int y2 = lUpper.get(lUpper.size() - 2).y;
                    int x3 = lUpper.get(lUpper.size() - 1).x;
                    int y3 = lUpper.get(lUpper.size() - 1).y;
                    det = (x1 * y2 - x2 * y1) - (x1 * y3 - x3 * y1) + (x2 * y3 - x3 * y2);
                }

            }
        }
        ArrayList<Point> lBottom=new ArrayList<>();
        lBottom.add(puntos[0].either());
        lBottom.add(puntos[1].either());
        for(int i=2;i<puntos.length;i++){
            lBottom.add(puntos[i].either());
            int det=0;
            if(lBottom.size()>2) {
                int x1 = lBottom.get(lBottom.size() - 3).x;
                int y1 = lBottom.get(lBottom.size() - 3).y;
                int x2 = lBottom.get(lBottom.size() - 2).x;
                int y2 = lBottom.get(lBottom.size() - 2).y;
                int x3 = lBottom.get(lBottom.size() - 1).x;
                int y3 = lBottom.get(lBottom.size() - 1).y;
                det = (x1 * y2 - x2 * y1) - (x1 * y3 - x3 * y1) + (x2 * y3 - x3 * y2);
            }
            //}
            while (lBottom.size()>2&&det>0){

                Point r=lBottom.get(lBottom.size()-2);
                lBottom.remove(r);
                if(lBottom.size()>2) {
                    int x1 = lBottom.get(lBottom.size() - 3).x;
                    int y1 = lBottom.get(lBottom.size() - 3).y;
                    int x2 = lBottom.get(lBottom.size() - 2).x;
                    int y2 = lBottom.get(lBottom.size() - 2).y;
                    int x3 = lBottom.get(lBottom.size() - 1).x;
                    int y3 = lBottom.get(lBottom.size() - 1).y;
                    det = (x1 * y2 - x2 * y1) - (x1 * y3 - x3 * y1) + (x2 * y3 - x3 * y2);
                }
            }
        }
        Polygon ret=new Polygon(lUpper.size()+lBottom.size());
        for (int i=0;i<lUpper.size()-1;i++){
            PolygonEdge pe=new PolygonEdge(lUpper.get(i),lUpper.get(i+1));
            ret.edges.add(pe);
        }
        PolygonEdge pen = new PolygonEdge(lUpper.get(lUpper.size()-1),lBottom.get(lBottom.size()-1));
        ret.edges.add(pen);
        for(int j=lBottom.size()-1;j>=1;j--){
            PolygonEdge pe=new PolygonEdge(lBottom.get(j-1),lBottom.get(j));
            ret.edges.add(pe);
        }


        return ret;
    }

    public void XMergeSort(PolygonEdge[] p,int first,int last){
        if(last<=first) return;
        else {
            int mid=first+(last-first)/2;
            XMergeSort(p,first,mid);
            XMergeSort(p,mid+1,last);
            XMerge(p,first,mid,last);
        }
    }

    public void XMerge(PolygonEdge[] p,int first,int mid,int last){
        int i=first;
        int j=mid+1;
        PolygonEdge[] aux=new PolygonEdge[p.length];
        for(int k=first;k<=last;k++){
            aux[k]=p[k];
        }
        for(int k=first;k<=last;k++){
            if(i>mid) p[k]=aux[j++];
            else if(j>last) p[k]=aux[i++];
            else if(aux[j].either().x<aux[i].either().x) p[k]=aux[j++];
            else if(aux[j].either().x==aux[i].either().x){
                if(aux[j].either().y<aux[i].either().y) p[k]=aux[j++];
                else p[k]=aux[i++];
            }else p[k]=aux[i++];

        }
    }

    /**
     * Returns a string representation of the polygon.
     *
     * @return the number of vertices <em>V</em>,
     *         followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + NEWLINE);
        for (PolygonEdge e : edges) {
            s.append(e + "  ");
            s.append(NEWLINE);
        }
        return s.toString();
    }

    /**
     * Unit tests the {@code EdgeWeightedGraph} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        In in1 = new In("./Polygon1.txt");
        In in2 = new In("./Polygon2.txt");
        In in3 = new In("./Polygon3.txt");
        In in4 = new In("./Polygon4.txt");
        In in5 = new In("./Polygon5.txt");
        Polygon G1 = new Polygon(in1);
        Polygon c1=G1.convexHull(G1);
        Polygon G2 = new Polygon(in2);
        Polygon c2=G2.convexHull(G2);
        Polygon G3 = new Polygon(in3);
        Polygon c3=G3.convexHull(G3);
        Polygon G4 = new Polygon(in4);
        Polygon c4=G4.convexHull(G4);
        Polygon G5 = new Polygon(in5);
        Polygon c5=G5.convexHull(G5);
        /*
        StdOut.println(G1);
        for (Point p: G1.vertices()) {
            StdOut.println(String.format("(%d, %d)", p.x, p.y));
        }
        */

        double[] x1=new double[G1.edges.size()];
        double[] y1=new double[G1.edges.size()];
        int cont=0;
        for(PolygonEdge pe:G1.edges){
            x1[cont]=(double) pe.either().x/100;
            y1[cont++]=(double)pe.either().y/100;
        }
        double[] x2=new double[G2.edges.size()];
        double[] y2=new double[G2.edges.size()];
        cont=0;
        for(PolygonEdge pe:G2.edges){
            x2[cont]=(double) pe.either().x/100;
            y2[cont++]=(double)pe.either().y/100;
        }
        double[] x3=new double[G3.edges.size()];
        double[] y3=new double[G3.edges.size()];
        cont=0;
        for(PolygonEdge pe:G3.edges){
            x3[cont]=(double) pe.either().x/100;
            y3[cont++]=(double)pe.either().y/100;
        }
        double[] x4=new double[G4.edges.size()];
        double[] y4=new double[G4.edges.size()];
        cont=0;
        for(PolygonEdge pe:G4.edges){
            x4[cont]=(double) pe.either().x/100;
            y4[cont++]=(double)pe.either().y/100;
        }
        double[] x5=new double[G5.edges.size()];
        double[] y5=new double[G5.edges.size()];
        cont=0;
        for(PolygonEdge pe:G5.edges){
            x5[cont]=(double) pe.either().x/100;
            y5[cont++]=(double)pe.either().y/100;
        }
        StdDraw.setPenRadius(0.001);
        //StdDraw.setPenColor(StdDraw.BLUE);
        //StdDraw.point(0.5, 0.5);
        //StdDraw.setPenColor(StdDraw.MAGENTA);
        //StdDraw.line(0.2, 0.2, 0.8, 0.2);

        //StdDraw.setPenColor(StdDraw.BLACK);
        //StdDraw.line(0,0,1,0);
        StdDraw.polygon(x1,y1);
        StdDraw.polygon(x2,y2);
        StdDraw.polygon(x3,y3);
        StdDraw.polygon(x4,y4);
        StdDraw.polygon(x5,y5);
        //Point q=new Point(3,2);
        double[] cx1=new double[c1.edges.size()];
        double[] cy1=new double[c1.edges.size()];
        cont=0;
        for(PolygonEdge pe:c1.edges){
            cx1[cont]=(double) pe.either().x/100;
            cy1[cont++]=(double)pe.either().y/100;
        }
        double[] cx2=new double[c2.edges.size()];
        double[] cy2=new double[c2.edges.size()];
        cont=0;
        for(PolygonEdge pe:c2.edges){
            cx2[cont]=(double) pe.either().x/100;
            cy2[cont++]=(double)pe.either().y/100;
        }
        double[] cx3=new double[c3.edges.size()];
        double[] cy3=new double[c3.edges.size()];
        cont=0;
        for(PolygonEdge pe:c3.edges){
            cx3[cont]=(double) pe.either().x/100;
            cy3[cont++]=(double)pe.either().y/100;
        }
        double[] cx4=new double[c4.edges.size()];
        double[] cy4=new double[c4.edges.size()];
        cont=0;
        for(PolygonEdge pe:c4.edges){
            cx4[cont]=(double) pe.either().x/100;
            cy4[cont++]=(double)pe.either().y/100;
        }
        double[] cx5=new double[c5.edges.size()];
        double[] cy5=new double[c5.edges.size()];
        cont=0;
        for(PolygonEdge pe:c5.edges){
            cx5[cont]=(double) pe.either().x/100;
            cy5[cont++]=(double)pe.either().y/100;
        }
        StdDraw.setPenColor(StdDraw.BOOK_BLUE);
        StdDraw.setPenRadius(0.002);
        StdDraw.polygon(cx1,cy1);
        StdDraw.polygon(cx2,cy2);
        StdDraw.polygon(cx3,cy3);
        StdDraw.polygon(cx4,cy4);
        StdDraw.polygon(cx5,cy5);

        boolean imp=true;
        while (true) {
            if (StdDraw.isMousePressed()) {
                Point q = new Point((int) (StdDraw.mouseX() * 100), (int) (StdDraw.mouseY() * 100));
                if (G1.isInside(q)) {
                    if(imp) {
                        System.out.println("Adentro del Poligono: G1");
                        imp=false;
                    }
                    //System.out.println(G1);
                }
                if (G2.isInside(q)) {
                    if(imp) {
                        System.out.println("Adentro del Poligono: G2");
                        imp=false;
                    }
                    //System.out.println(G2);
                }
                if (G3.isInside(q)) {
                    if(imp) {
                        System.out.println("Adentro del Poligono: G3");
                        imp=false;
                    }
                    //System.out.println(G3);
                }
                if (G4.isInside(q)) {
                    if(imp) {
                        System.out.println("Adentro del Poligono: G4");
                        imp=false;
                    }
                    //System.out.println(G4);
                }
                if (G5.isInside(q)) {
                    if(imp) {
                        System.out.println("Adentro del Poligono: G5");
                        imp=false;
                    }
                    //System.out.println(G5);
                }
            }else {
                imp=true;
            }
        }

    }
}