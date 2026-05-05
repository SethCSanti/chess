package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

    public static Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] offsets = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
        return MovesHelper.jumpMoves(board, myPosition, offsets);
    }

    public static Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(MovesHelper.calculateDiagonals(board, myPosition));
        moves.addAll(MovesHelper.calculateSides(board, myPosition));
        return moves;
    }

    public static Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        return new ArrayList<>(MovesHelper.calculateDiagonals(board, myPosition));
    }

    public static Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] offsets = {
                {-1, 2}, {1, 2},
                {-2, 1}, {2, 1},
                {-1, -2}, {1, -2},
                {-2, -1}, {2, -1},
        };
        return MovesHelper.jumpMoves(board, myPosition, offsets);
    }

    public static Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        return new ArrayList<>(MovesHelper.calculateSides(board, myPosition));
    }

    public static Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        return MovesHelper.calculatePawnMoves(board, myPosition);
    }
}
