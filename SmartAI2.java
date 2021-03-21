import java.util.ArrayList;
import java.util.stream.IntStream;

public class SmartAI2 implements IOthelloAI {
    public static final int CORNER_FACTOR = 2;
    private int myNumber;


    public Position decideMove(GameState state) {
        Tuple t = maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        return t.getPosition();
    }

    public Tuple maxValue(GameState state, int alfa, int beta, int depth) {
        if (isCutOff(state, depth)) {
            return new Tuple(evaluate(state), new Position(-3, -3));
        }
        ArrayList<Position> moves = state.legalMoves();
        int maxValue = Integer.MIN_VALUE;
        Position maxMove = new Position(-3, -3);
        for (Position move : moves) {
            GameState copyState = new GameState(state.getBoard(), state.getPlayerInTurn());
            copyState.insertToken(move);
            Tuple t = minValue(copyState, alfa, beta, depth + 1);
            if (t.getValue() > maxValue) {
                maxValue = t.getValue();
                maxMove = move;
                alfa = Math.max(alfa, maxValue);
            }
            if (maxValue >= beta) {
                return new Tuple(maxValue, maxMove);
            }
        }
        if (moves.isEmpty()) {
            // state.changePlayer();
            return maxValue(state, alfa, beta, depth + 1);
        }
        return new Tuple(maxValue, maxMove);
    }

    private int evaluate(GameState state) {
        return countMyTokens(state) + CORNER_FACTOR * countMyCorners(state.getBoard());
    }

    private int countMyTokens(GameState state) {
        return state.countTokens()[amIWhite() ? 1: 0];
    }

    private boolean amIWhite() {
        return myNumber == 2;
    }

    private int countMyCorners(int[][] board) {
        int n = board.length;
        return IntStream.of(board[0][0], board[0][n - 1], board[n - 1][0], board[n - 1][n - 1]).filter(x -> x == myNumber).sum();
    }

    private boolean isCutOff(GameState state, int depth) {
        return state.isFinished() || depth > 7;
    }


    public Tuple minValue(GameState state, int alfa, int beta, int depth) {
        if (isCutOff(state, depth)) {
            return new Tuple(evaluate(state), new Position(-3, -3));
        }
        ArrayList<Position> moves = state.legalMoves();
        int minValue = Integer.MAX_VALUE;
        Position minMove = new Position(-3, -3);
        for (Position move : moves) {
            GameState copyState = new GameState(state.getBoard(), state.getPlayerInTurn());
            copyState.insertToken(move);
            Tuple t = maxValue(copyState, alfa, beta, depth + 1);
            if (t.getValue() < minValue) {
                minValue = t.getValue();
                minMove = move;
                beta = Math.min(beta, minValue);
            }
            if (minValue <= alfa) {
                return new Tuple(minValue, minMove);
            }
        }
        if (moves.isEmpty()) {
            // state.changePlayer();
            return minValue(state, alfa, beta, depth + 1);
        }
        return new Tuple(minValue, minMove);
    }

    class Tuple {
        private int v;
        private Position p;

        public Tuple(int v, Position p) {
            this.v = v;
            this.p = p;
        }

        public int getValue() {
            return v;
        }

        public Position getPosition() {
            return p;
        }
    }
}
