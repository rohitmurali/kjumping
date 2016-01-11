package jump61;

import static jump61.Side.*;
import static jump61.Square.square;
import java.util.ArrayDeque;

/** A Jump61 board state that may be modified.
 *  @author Rohit Muralidharan
 */

class MutableBoard extends Board {

    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        history = new ArrayDeque<MutableBoard>();
        moves = 0;
        _N = N;
        board = new Square[(_N * _N)];
        for (int r = 0; r < board.length; r++) {
            board[r] = Square.square(WHITE, 1);
        }
    }

    /** A board whose initial contents are copied from BOARD0, but whose
     *  -undo history is clear. */
    MutableBoard(Board board0) {
        this(board0.size());
        this.copy(board0);
        history.clear();
    }

    @Override
    void clear(int N) {
        MutableBoard x = new MutableBoard(N);
        copy(x);
        history.clear();
        moves = 0;
        announce();
    }

    @Override
    void copy(Board board0) {
        _N = board0.size();
        board = new Square[_N * _N];
        for (int i = 0; i < _N * _N; i++) {
            internalSet(i, board0.get(i));
        }
    }

    /** Copy the contents of BOARD into me, without modifying my undo
     *  history.  Assumes BOARD and I have the same size.
     *  @param board0 I changed the paramater name to avoid conflict.
     */
    private void internalCopy(MutableBoard board0) {
        copy(board0);
    }

    @Override
    int size() {
        return _N;
    }

    @Override
    Square get(int n) {
        return board[n];
    }

    @Override
    int numOfSide(Side side) {
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            if (this.get(i).getSide().equals(side)) {
                count++;
            }
        }
        return count;
    }

    @Override
    int numPieces() {
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            count += get(i).getSpots();
        }
        return count;
    }

    @Override
    void addSpot(Side player, int r, int c) {
        addSpot(player, sqNum(r, c));
        announce();
    }

    @Override
    void addSpot(Side player, int n) {
        markUndo();
        int current = get(n).getSpots();
        if (current + 1 > neighbors(n)) {
            overflow(player, row(n), col(n));
        } else {
            set(n, current + 1, player);
        }
        announce();
    }

    /** A helper method of addSpot to perform overflows.
     * @param r row
     * @param c col
     * @param player the Side of the player.
     */
    void overflow(Side player, int r, int c) {
        int n = sqNum(r, c);
        int current = get(n).getSpots();
        set(n, current + 1, player);
        if (current + 1 > neighbors(n)) {
            set(n, 1, player);
            if (getWinner() == null) {
                if (c > 1) {
                    overflow(player, r, c - 1);
                }
                if (c < size()) {
                    overflow(player, r, c + 1);
                }
                if (r > 1) {
                    overflow(player, r - 1, c);
                }
                if (r < size()) {
                    overflow(player, r + 1, c);
                }
            }
        }
    }

    @Override
    void set(int r, int c, int num, Side player) {
        internalSet(sqNum(r, c), square(player, num));
    }

    @Override
    void set(int n, int num, Side player) {
        internalSet(n, square(player, num));
        announce();
    }

    @Override
    void undo() {
        copy(history.removeLast());
    }

    /** Record the beginning of a move in the undo history. */
    private void markUndo() {
        MutableBoard temp = new MutableBoard(this);
        history.add(temp);
        moves++;
    }

    /** Set the contents of the square with index IND to SQ. Update counts
     *  of numbers of squares of each color.  */
    private void internalSet(int ind, Square sq) {
        board[ind] = sq;
    }

    /** Notify all Observers of a change. */
    private void announce() {
        setChanged();
        notifyObservers();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MutableBoard)) {
            return obj.equals(this);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return numOfSide(Side.RED) * numOfSide(Side.WHITE) * numPieces();
    }
    /** Stores the history in an ArrayDeque for in-place clearing.*/
    private ArrayDeque<MutableBoard> history;

    /** Stores the squares for the board in an array. */
    private Square[] board;

    /** Integers to store the number of moves and the size.*/
    private int moves, _N;
}
