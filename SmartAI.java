import java.util.ArrayList;
import java.util.stream.IntStream;

public class SmartAI implements IOthelloAI {
    public static final int MAX_DEPTH = 7;
    public static final int CORNER_FACTOR = 2;
    private int aiColor;

    public Position decideMove(GameState state) {
        // The AI's tokens color: 1 means black, 2 means white
        aiColor = state.getPlayerInTurn();
        Tuple t = maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        return t.getPosition();
    }

    public Tuple maxValue(GameState state, int alpha, int beta, int depth) {
        // Return if any of the cut off conditions are fulfiled 
        if (isCutOff(state, depth)) {
            return new Tuple(evaluate(state), null);
        }

        ArrayList<Position> moves = state.legalMoves();
        // If there are no legal moves, create a state copy with player turn changed and continue the recursion.
        if (moves.isEmpty()) {
            GameState newState = changePlayerInCopy(state);
            return minValue(newState, alpha, beta, depth + 1);
        } else {
            // Set initial values for best move
            int maxValue = Integer.MIN_VALUE;
            Position maxMove = null;
            for (Position move : moves) {
                // Creates a copy of the current state and simulates the move
                Tuple min = minValue(insertTokenInCopy(state, move), alpha, beta, depth + 1);
                if (min.getValue() > maxValue) {
                    maxValue = min.getValue();
                    maxMove = move;
                    alpha = Math.max(alpha, maxValue);
                }
                // Beta pruning
                if (maxValue >= beta) {
                    return new Tuple(maxValue, maxMove);
                }
            }
            return new Tuple(maxValue, maxMove);
        }
    }

    public Tuple minValue(GameState state, int alpha, int beta, int depth) {
        // Return if any of the cut off conditions are fulfiled 
        if (isCutOff(state, depth)) {
            return new Tuple(evaluate(state), null);
        }

        ArrayList<Position> moves = state.legalMoves();
        // If there are no legal moves, create a state copy with player turn changed and continue the recursion.
        if (moves.isEmpty()) {
            return maxValue(changePlayerInCopy(state), alpha, beta, depth + 1);
        } else {
            // Set initial values for best move
            int minValue = Integer.MAX_VALUE;
            Position minMove = null;
            for (Position move : moves) {
                // Creates a copy of the current state and simulates the move
                Tuple max = maxValue(insertTokenInCopy(state, move), alpha, beta, depth + 1);
                if (max.getValue() < minValue) {
                    minValue = max.getValue();
                    minMove = move;
                    beta = Math.min(beta, minValue);
                }
                // Alpha pruning
                if (minValue <= alpha) {
                    return new Tuple(minValue, minMove);
                }
            }
            return new Tuple(minValue, minMove);
        }
    }

    // Cut off if the game is finished or the specified depth has been reached
    private boolean isCutOff(GameState state, int depth) {
        return state.isFinished() || depth > MAX_DEPTH;
    }

    private int evaluate(GameState state) {
        return countAITokens(state) + CORNER_FACTOR * countAICorners(state.getBoard());
    }

    // Counts the number of tokens that the AI has
    private int countAITokens(GameState state) {
        return state.countTokens()[getAITokenIndex()];
    }

    // Since the API returns the tokens in an array with two values where the indexes don't correspond to the player number (color) we need to correct that.
    private int getAITokenIndex() {
        return aiColor - 1;
    }

    // Counts the number of corners that the AI has. 
    private int countAICorners(int[][] board) {
        int n = board.length;
        return IntStream.of(board[0][0], board[0][n - 1], board[n - 1][0], board[n - 1][n - 1]).filter(x -> x == aiColor).sum();
    }

    // Returns a copy of the current state with a specific move applied.
    private GameState insertTokenInCopy(GameState state, Position move) {
        GameState copyState = getStateCopy(state);
        copyState.insertToken(move);
        return copyState;
    }

    // Returns a copy of the current state with changed player turn.
    private GameState changePlayerInCopy(GameState state) {
        GameState copyState = getStateCopy(state);
        copyState.changePlayer();
        return copyState;
    }

    // Returns a copy of the current state.
    private GameState getStateCopy(GameState state) {
        return new GameState(state.getBoard(), state.getPlayerInTurn());
    }

    // Simple class to hold a pair of position and its value
    class Tuple {

        private int value;
        private Position position;

        public Tuple(int value, Position position) {
            this.value = value;
            this.position = position;
        }

        public int getValue() {
            return value;
        }

        public Position getPosition() {
            return position;
        }
    }
}
