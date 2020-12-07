package util;

import java.util.LinkedList;

public class Board {
    int[][] tiles;

    public Board(int[][] tiles) {// construct a board from an N-by-N array of tiles
        this.tiles = tiles;
    }

    private int[][] copy(int[][] blocks) {
        int[][] copy = new int[blocks.length][blocks.length];
        for (int row = 0; row < blocks.length; row++)
            for (int col = 0; col < blocks.length; col++)
                copy[row][col] = blocks[row][col];

        return copy;
    }

    public int hamming() {              // return number of blocks out of place
        int c=0;
        for(int row=0;row<tiles.length;row++){
            for (int col=0;col<tiles.length;col++){
                if(isInPlace(row,col)) c++;
            }
        }
        return c;
    }

    private boolean isInPlace(int row, int col){
        int act=tiles[row][col];
        return act!=0 && act!=numberOfPos(row, col);
    }

    private int numberOfPos(int row, int col){
        return row*tiles.length+col+1;
    }

    public int manhattan(){          // return sum of Manhattan distances between blocks and goal
        int sum=0;
        for(int row =0;row<tiles.length;row++){
            for(int col=0;col<tiles.length;col++){
                sum+=distance(row,col);
            }
        }
        return sum;
    }

    private int distance(int row, int col){
        int act= tiles[row][col];
        return act==0? 0:Math.abs(row-(act-1)/tiles.length)+Math.abs(col-(act-1)%tiles.length);
    }

    public boolean isCorrect(){
        for (int row=0;row<tiles.length;row++){
            for (int col=0;col<tiles.length;col++){
                if(isInPlace(row,col)) return false;
            }
        }
        return true;
    }

    private int[][] swap(int row1, int col1, int row2, int col2) {
        int[][] copy = copy(tiles);
        int tmp = copy[row1][col1];
        copy[row1][col1] = copy[row2][col2];
        copy[row2][col2] = tmp;

        return copy;
    }

    public boolean equals(Object y){  // does this board equal y
        if (this==y) return true;
        if(y==null||((Board) y).tiles.length!= tiles.length ) return false;
        for(int row =0;row<tiles.length;row++){
            for (int col=0;col<tiles.length;col++){
                if(((Board) y).tiles[row][col]!=tiles[row][col]) return false;
            }
        }
        return true;
    }

    public Iterable<Board> neighbors() {
        LinkedList<Board> neighbors = new LinkedList<Board>();

        int[] location = zeroLocation();
        int spaceRow = location[0];
        int spaceCol = location[1];

        if (spaceRow > 0) neighbors.add(new Board(swap(spaceRow, spaceCol, spaceRow - 1, spaceCol)));
        if (spaceRow < tiles.length - 1) neighbors.add(new Board(swap(spaceRow, spaceCol, spaceRow + 1, spaceCol)));
        if (spaceCol > 0) neighbors.add(new Board(swap(spaceRow, spaceCol, spaceRow, spaceCol - 1)));
        if (spaceCol < tiles.length - 1) neighbors.add(new Board(swap(spaceRow, spaceCol, spaceRow, spaceCol + 1)));

        return neighbors;
    }

    private int[] zeroLocation() {
        for (int row = 0; row < tiles.length; row++)
            for (int col = 0; col < tiles.length; col++)
                if (tiles[row][col]==0) {
                    int[] location = new int[2];
                    location[0] = row;
                    location[1] = col;

                    return location;
                }
        throw new RuntimeException("Zero not found");
    }

    public Board changeFirstTwo() {
        for (int row = 0; row < tiles.length; row++)
            for (int col = 0; col < tiles.length - 1; col++)
                if (tiles[row][col]!=0 && tiles[row][col+1]!=0)
                    return new Board(swap(row, col, row, col+1));
        throw new RuntimeException("Invalid Board");
    }

    public String toString(){           // return a string representation of the board
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles.length; col++)
                sb.append(String.format("%2d ", tiles[row][col]));
            sb.append("\n");
        }

        return sb.toString();
    }
}