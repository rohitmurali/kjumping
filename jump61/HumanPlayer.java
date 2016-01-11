package jump61;

/** A Player that gets its moves from manual input.
 *  @author Rohit Muralidharan
 */
class HumanPlayer extends Player {

    /** A new player initially playing COLOR taking manual input of
     *  moves from GAME's input source. */
    HumanPlayer(Game game, Side color) {
        super(game, color);
    }

    @Override
    /** Retrieve moves using getGame().getMove() until a legal one is found and
     *  make that move in getGame().  Report erroneous moves to player. */
    void makeMove() {
        int[] moves = new int[2];
        if (getGame().getMove(moves)) {
            if (getBoard().isLegal(getSide(), moves[0], moves[1])) {
                getGame().makeMove(moves[0], moves[1]);
            } else {
                String str = " is not a valid move";
                throw new GameException(moves[0] + " " + moves[1] + str);
            }
        }
    }
}
