package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

public class SecondPartial {
    /*
    Profe, intente varias cosas y formas de recorrer el grafo para generar las conexiones, pero por alguna razon siempre mas sale
    un pozo de mas. El grafo se genera bien en las listas de adyacencia y al final se imprimen los wells y los pipes.
    */


    private double[] P;
    private double[][] T;
    private boolean[] registred;//Visitados
    private ArrayList<Integer> wells;//Pozos
    private ArrayList<Edge> pipes;//Tuberias
    private final int N;
    private int E;//Total de edges (para informacion del grafo)
    private ArrayList<Edge>[] adj;//Las adyacencias de nodos. Intente hacerlo con Bag pero se me hizo mas facil con una lista
    private ArrayList<Double> minimumWell;//Lista ordenada de los valores P de cada nodo
    private double weigth;//Costo

    public SecondPartial(In p, In t) {
        if (p == null || t == null) throw new IllegalArgumentException("argument is null");

        try {
            N = p.readInt();
            P = new double[N];
            T = new double[N][N];
            weigth=0;
            minimumWell=new ArrayList<>();
            for (int i = 0; i < N; i++) {
                P[i] = p.readDouble();
                for (int j = 0; j < N; j++) {
                    T[i][j] = t.readDouble();
                }
            }
            registred=new boolean[N];
            //Se agregan los valores de P a la lista
            for(int c=0;c<N;c++){
                minimumWell.add(P[c]);
            }
            Collections.sort(minimumWell);
            //Use la logica del EdgeWeightedGraph pero la implemente aqui, incluyendo los pesos (well) de cada nodo en el objeto Edge
            //El grafo esta almacenado en adj que es una lista
            this.E = 0;
            adj = new ArrayList[N];
            for (int n = 0; n < N; n++) {
                adj[n] = new ArrayList<>();
            }
			for(int i=0;i<adj.length;i++){
			    for(int j=i;j<N;j++){
			        if(i!=j) {
                        Edge e = new Edge(i, j, T[i][j]);
                        adj[i].add(e);
                        adj[j].add(e);
                        E++;
                    }
                }
            }
            //Inicio las listas de pozos y tuberias
            wells=new ArrayList<>();
			pipes= new ArrayList<>();
			//Agarro el primer pozo mas chico
            double minwell=minimumWell.remove(0);
			/*for(int s=0;s<P.length;s++){
			    if(P[s]==minwell){
			        wells.add(s);
			        registred[s]=true;
			        weigth+=minwell;
			        minwell=minimumWell.remove(0);
                }
            }
            */
            //Creo un Edge auxiliar para buscar el mas chico
            Edge e=adj[0].get(0);
            double minpipe=e.weight();
            //Como ya agrege un valor a wells, voy a buscar los restantes
            for(int b=1;b<N;b++){
                //Buscar el edge menor recorriendo la matriz de manera triangular, ya que es un grafo no dirigido
                for(int r=0;r<N;r++){
                    for(int q=r;q<adj[r].size();q++){
                        //Edge auxiliar para comparar
                        Edge g=adj[r].get(q);
                        //Checo que si ambos lados del Edge ya han sido visitados entonces cambi de Edge
                        if(registred[e.either()]&&registred[e.other(e.either())]){
                            e = g;
                            minpipe = e.weight();
                        }
                        //Checo si en mi Edge hay por lo menos uno que visitar

                        if(!registred[g.other(g.either())]&&registred[g.either()]) {
                            if (g.weight() < minpipe) {
                                e = g;
                                minpipe = e.weight();
                            }
                        }
                        if(registred[g.other(g.either())]&&!registred[g.either()]) {
                            if (g.weight() < minpipe) {
                                e = g;
                                minpipe = e.weight();
                            }
                        }
                        //Si ambos lados ya fueron visitados continua
                        if(!registred[g.other(g.either())]&&!registred[g.either()]) continue;
                    }
                }
                //Checo que es menor, si el pozo mas chico o la conexion mas chica
                if(minwell<minpipe){
                    //Si es el pozo busco a que nodo pertenece
                    for(int s=0;s<P.length;s++){
                        if(P[s]==minwell){
                            //Profe aqui hize varias pruebas y casos pero por alguna razon me mete un pozo de mas
                            //Lo que intente primero era checar si el pozo minimo ya estaba visitado para no agregarlo
                            //Luego intente agregarlo siempre
                            //Aqui lo que intente es probar dandole prioridad a los ejes
                            //Pero siempre o me da un poso de mas o los pipes se repiten
                            //Profe vi que el primer edge no lo toma como el mas chico y eso puede causar que agarre un pozo de mas
                            //LLevo desde las 11:30 intntando arreglarlo y esta es la iteracion que me da mas cerca
                            //Al principio pense quera un MST pero luego vi en el modulo del examen y el pdf que hay pipes que no generan un arbol o wells aislados
                            if(s!=e.either()&&s!=e.other(e.either())) {
                                if (!registred[s]) {
                                    wells.add(s);
                                    registred[s] = true;
                                    weigth += minwell;
                                    minwell = minimumWell.remove(0);
                                } else {
                                    minwell = minimumWell.remove(0);
                                    pipes.add(e);
                                    weigth += e.weight();
                                    int v = e.either();
                                    registred[v] = true;
                                    registred[e.other(v)] = true;
                                }
                            }else {
                                minwell = minimumWell.remove(0);
                                pipes.add(e);
                                weigth += e.weight();
                                int v = e.either();
                                registred[v] = true;
                                registred[e.other(v)] = true;
                            }

                        }

                    }
                    //Aqui se verifica cuando el eje es menor y se agrega a los pipes
                }else{
                    pipes.add(e);
                    weigth+=e.weight();
                    int v=e.either();
                    registred[v]=true;
                    registred[e.other(v)]=true;
                }

            }
            //Se imprimen los wells
            System.out.print("Wells: ");
            for (Integer i: wells) {
                System.out.print(i+", ");
            }
            System.out.println();
            //Se imprimen los pipes
            System.out.print("Pipes: ");
            for(Edge w:pipes){
                System.out.print(w.either()+"-"+w.other(w.either())+", ");
            }
            System.out.println();
            //Se imprime el costo
            System.out.print("Cost: "+weigth);
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in SecondPartial constructor", e);
        }
    }

    public int getN() {
        return N;
    }

    public double[] getP() {
        return P;
    }

    public double[][] getT() {
        return T;
    }


    public int getE() {
        return E;
    }

    public static void main(String args[]) {
        In p = new In("./WellCost6.txt");
        In t = new In("./PipeCost6.txt");
        SecondPartial exam = new SecondPartial(p, t);
    }
}
