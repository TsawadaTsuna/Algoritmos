package util;

public class Solver {
    private class Move implements Comparable<Move> {
        private Move previous;
        private Board board;
        private int numMoves = 0;

        public Move(Board board) {
            this.board = board;
        }

        public Move(Board board, Move previous) {
            this.board = board;
            this.previous = previous;
            this.numMoves = previous.numMoves + 1;
        }

        public int compareTo(Move move) {
            return (this.board.manhattan() - move.board.manhattan()) + (this.numMoves - move.numMoves);
        }
    }

    private Move lastMove;
    private int numberOfMoves =-1;

    public Solver(Board initial) {
        MinPQ<Move> moves = new MinPQ<Move>();
        moves.insert(new Move(initial));

        MinPQ<Move> changedMoves = new MinPQ<Move>();
        changedMoves.insert(new Move(initial.changeFirstTwo()));

        while(true) {
            lastMove = findNextMove(moves);
            if (lastMove != null || findNextMove(changedMoves) != null) return;
        }
    }

    private Move findNextMove(MinPQ<Move> moves) {
        if(moves.isEmpty()) return null;
        Move bestMove = moves.delMin();
        if (bestMove.board.isCorrect()) return bestMove;
        for (Board neighbor : bestMove.board.neighbors()) {
            if (bestMove.previous == null || !neighbor.equals(bestMove.previous.board)) {
                moves.insert(new Move(neighbor, bestMove));
            }
        }
        return null;
    }

    public boolean hasSolution() {
        return (lastMove != null);
    }

    public int moves() {
        return hasSolution() ? lastMove.numMoves : -1;
    }

    public Iterable<Board> solution() {
        numberOfMoves =moves();
        if (!hasSolution()) return null;
        Stack<Board> moves = new Stack<Board>();
        while(lastMove != null) {
            moves.push(lastMove.board);
            lastMove = lastMove.previous;
        }

        return moves;
    }


    @Override
    public String toString() {
        StringBuilder sb= new StringBuilder();
        if(hasSolution()) {
            for (Board b : solution()) {
                sb.append(b + "\n");
            }
        }
        sb.append("Number of moves: "+ numberOfMoves);
        return sb.toString();
    }

    public static void main(String[] args) {
        //Solvable
        Board b1=new Board(new int[][]{{0,1,3},{4,2,5},{7,8,6}});
        //Not Solvable
        Board b2=new Board(new int[][]{{8,1,2},{0,2,3},{7,6,5}});
        //Solvable
        Board b3=new Board(new int[][]{{4,2,1,3},{5,6,7,8},{9,10,11,12},{13,14,0,15}});
        //Not Solvable
        Board b4 = new Board(new int[][]{{1,2,3,4},{5,6,7,8},{9,10,11,15},{13,14,12,0}});
        Board p1=new Board(new int[][]{{8,6,7},{2,5,4},{3,0,1}});
        Board p2=new Board(new int[][]{{6,4,7},{8,5,0},{3,2,1}});
        long time1=System.nanoTime();
        Solver s= new Solver(b3);
        long time2=System.nanoTime();
        long total=time2-time1;
        double tot=(double) total/1000000000;
        System.out.println(s);
        System.out.println("Tiempo tatal: "+tot);
    }
}