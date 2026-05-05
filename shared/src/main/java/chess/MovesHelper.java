package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class abstracts away the duplicate code for jumps and slides.
 */
public class MovesHelper {
    /**
     * Abstracts away the duplicate code used in diagonal, horizontal, and vertical slides.
     */
    public static Collection<ChessMove> sliderHelper(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        for (int[] direction : directions) {
            int step = 1;
            int newRow = myPosition.getRow() + (direction[0] * step);
            int newCol = myPosition.getColumn() + (direction[1] * step);
            while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPiece pieceAtCandidate = board.getPiece(new ChessPosition(newRow, newCol));
                if (pieceAtCandidate == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                } else {
                    if (pieceAtCandidate.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                        break;
                    } else if (pieceAtCandidate.getTeamColor() == piece.getTeamColor()) {
                        break;
                    }
                }
                step++;
                newRow = myPosition.getRow() + (direction[0] * step);
                newCol = myPosition.getColumn() + (direction[1] * step);
            }
        }
        return moves;
    }

    /**
     * Gives directions for sliderHelper and returns the moves.
     */
    public static Collection<ChessMove> calculateDiagonals(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1, 1}, {-1, 1}, {-1, -1}, {1, -1}
        };

        return sliderHelper(board, myPosition, directions);
    }

    /**
     * Gives directions for sliderHelper and returns the moves.
     */
    public static Collection<ChessMove> calculateSides(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        return sliderHelper(board, myPosition, directions);
    }

    /**
     * Takes offsets and returns the moves for more specific movements. (King, Knight)
     */
    public static Collection<ChessMove> jumpMoves(ChessBoard board, ChessPosition myPosition, int[][] offsets) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        for (int[] offset : offsets) {
            int newRow = myPosition.getRow() + offset[0];
            int newCol = myPosition.getColumn() + offset[1];
            // border & color checks
            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPiece pieceAtCandidate = board.getPiece(new ChessPosition(newRow, newCol));
                if (pieceAtCandidate != null) {
                    if (pieceAtCandidate.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                    }
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                }
            }
        }
        return moves;
    }
}
