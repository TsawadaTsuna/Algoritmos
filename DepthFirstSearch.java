package util;

import java.util.Stack;

public class DepthFirstSearch {
    private boolean[] marked;
    private int count;
    private final int s;
    private int[] edgeTo;

    public DepthFirstSearch(Graph g,int s){
        this.s=s;
        marked = new boolean[g.V()];
        edgeTo = new int[g.V()];
        count = 0;
        dfs(g,s);
    }

    private void dfs(Graph g, int v){
        count++;
        marked[v] = true;
        for(int w: g.adj(v)){
            if (!marked[w]){
                edgeTo[w]=v;
                dfs(g,w);
            }
        }
    }

    private boolean hasPathTo(int v){
        return marked[v];
    }

    public Iterable<Integer> pathTo(int v){
        if(!hasPathTo(v)) return null;
        Stack<Integer> path = new Stack<>();
        for (int x=v; x!=s;x=edgeTo[x]){
            path.push(x);
        }
        path.push(s);
        return path;
    }

    public static void main(String[] args) {
        In in = new In("");
    }
}
