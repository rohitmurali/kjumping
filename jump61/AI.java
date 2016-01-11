package jump61;

import java.util.ArrayList;
import java.util.Random;

/** An automated Player.
 *  @author Rohit Muralidharan
 */
class AI extends Player {

    /** Time allotted to all but final search depth (milliseconds). */
    private static final long TIME_LIMIT = 15000;

    /** Number of calls to minmax between checks of elapsed time. */
    private static final long TIME_CHECK_INTERVAL = 10000;

    /** Number of milliseconds in one second. */
    private static final double MILLIS = 1000.0;

    /** An enum to choose possible AI strategies between. */
    public static enum Strategy { RANDOM, GREEDY, MINMAX };

    /** We start the strategy as minmax. */
    public static final Strategy AI_STRATEGY = Strategy.MINMAX;


    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    public AI(Game game, Side color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        int n;
        int size = getBoard().size();
        Random rand = new Random();
        if (AI_STRATEGY == Strategy.RANDOM) {
            do {
                n = getGame().randInt(size * size);
            } while (!getBoard().isLegal(getSide(), n));
        } else if (AI_STRATEGY == Strategy.GREEDY) {
            n = greedyHelper();
        } else {
            ArrayList<Integer> mvs = new ArrayList<Integer>();
            minmax(getSide(), getBoard(), size - 1, Integer.MAX_VALUE, mvs);
            n = mvs.get(0);
        }
        getGame().makeMove(n);
        getGame().reportMove(getSide(), getBoard().row(n), getBoard().col(n));

    }
    /** A Helper function for greedy. Returns the integer for the move. */
    private int greedyHelper() {
        int max = Integer.MIN_VALUE;
        int maxI = 0;
        int size = getBoard().size();
        MutableBoard b = new MutableBoard(getBoard());
        for (int i = 0; i < size * size; i++) {
            b = new MutableBoard(getBoard());
            if (b.isLegal(getSide(), i)) {
                b.addSpot(getSide(), i);
                if (staticEval(getSide(), b) > max) {
                    max = staticEval(getSide(), b);
                    maxI = i;
                }
            }
        }
        return maxI;

    }

    /** Return the minimum of CUTOFF and the minmax value of board B
     *  (which must be mutable) for player P to a search depth of D
     *  (where D == 0 denotes statically evaluating just the next move).
     *  If MOVES is not null and CUTOFF is not exceeded, set MOVES to
     *  a list of all highest-scoring moves for P; clear it if
     *  non-null and CUTOFF is exceeded. the contents of B are
     *  invariant over this call.
     */
    private int minmax(Side p, Board b, int d, int cutoff,
                       ArrayList<Integer> moves) {
        MutableBoard board = new MutableBoard(b);
        if (board.getWinner() == p) {
            return Integer.MAX_VALUE;
        } else if (board.getWinner() == p.opposite()) {
            return Integer.MIN_VALUE;
        } else if (d == 0) {
            return minHelper(p, board, cutoff, moves);
        }
        int best = Integer.MIN_VALUE;
        int size = board.size() * board.size();
        for (int i = 0; i < size; i++) {
            if (board.isLegal(p, i)) {
                board.addSpot(p, i);
                ArrayList<Integer> blank = new ArrayList<Integer>();
                int other = minmax(p.opposite(), board, d - 1, cutoff, blank);
                if (other < cutoff) {
                    cutoff = other;
                    moves.clear();
                    moves.add(i);
                } else if (other == best) {
                    moves.add(i);
                }
                board.undo();
            }
        }
        cutoff = best;
        return best;
    }

    /** A helper method for minmax for depth 0.
     * @param p Side
     * @param b the MutableBoard
     * @param cutoff the cutoff
     * @param moves the arraylist of moves.
     * @return returns the heuristic value for minMax to return */
    private int minHelper(Side p, MutableBoard b, int cutoff,
        ArrayList<Integer> moves) {
        int max = Integer.MIN_VALUE;
        int size = b.size();
        MutableBoard board = new MutableBoard(b);
        for (int i = 0; i < size * size; i++) {
            board = new MutableBoard(b);
            if (board.isLegal(p, i)) {
                board.addSpot(p, i);
                if (staticEval(p, board) > max) {
                    max = staticEval(p, board);
                    moves.clear();
                    moves.add(i);
                    if (max > cutoff) {
                        break;
                    }
                } else if (staticEval(p, board) == max) {
                    moves.add(i);
                }
                board.undo();
            }
        }
        return max;
    }

    /** Returns heuristic value of board B for player P.
     *  Higher is better for P. */
    private int staticEval(Side p, Board b) {
        return b.numOfSide(p) - b.numOfSide(p.opposite());
    }
}
