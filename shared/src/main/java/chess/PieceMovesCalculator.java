package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

    /** Gives the offsets for the king and calls its helper function
     *
     * @param board the chess board
     * @param myPosition position of the current piece
     */
    public static Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] offsets = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
        return MovesHelper.jumpMoves(board, myPosition, offsets);
    }

    /** Calls the horizontal/vertical and diagonal slider functions to make the array of queen moves
     *
     * @param board the chess board
     * @param myPosition position of the current piece
     */
    public static Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(MovesHelper.calculateDiagonals(board, myPosition));
        moves.addAll(MovesHelper.calculateSides(board, myPosition));
        return moves;
    }

    /** Calls the diagonal slider functions to make the array of bishop moves
     *
     * @param board the chess board
     * @param myPosition position of the current piece
     */
    public static Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        return new ArrayList<>(MovesHelper.calculateDiagonals(board, myPosition));
    }

    /** Gives the offsets for the knight and calls its helper function
     *
     * @param board the chess board
     * @param myPosition position of the current piece
     */
    public static Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] offsets = {
                {-1, 2}, {1, 2},
                {-2, 1}, {2, 1},
                {-1, -2}, {1, -2},
                {-2, -1}, {2, -1},
        };
        return MovesHelper.jumpMoves(board, myPosition, offsets);
    }

    /** Calls the diagonal horizontal/vertical functions to make the array of rook moves
     *
     * @param board the chess board
     * @param myPosition position of the current piece
     */
    public static Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        return new ArrayList<>(MovesHelper.calculateSides(board, myPosition));
    }

    /** Calls the pawn's helper function to return an array of its moves
     *
     * @param board the chess board
     * @param myPosition position of the current piece
     */
    public static Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        return MovesHelper.calculatePawnMoves(board, myPosition);
    }
}
