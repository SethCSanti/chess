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

    public static Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        int newRow = myPosition.getRow();
        int newCol = myPosition.getColumn();
        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startingRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (direction == 1) ? 8 : 1;

        //normal one square forward
        ChessPiece pieceAtCandidate = board.getPiece(new ChessPosition(newRow + direction, newCol));
        if (pieceAtCandidate == null) {
            //initial two square forward
            if (myPosition.getRow() == startingRow) {
                ChessPiece pieceAtCandidate2 = board.getPiece(new ChessPosition(newRow + direction * 2, newCol));
                if (pieceAtCandidate2 == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction * 2, newCol), null));
                }
            }
            if (newRow + direction == promotionRow) {
                moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol), ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol), ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol), ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol), ChessPiece.PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol), null));
            }
        }

        // diagonal right
        if (newCol + 1 <= 8) {
            pieceAtCandidate = board.getPiece(new ChessPosition(newRow + direction, newCol + 1));
            if (pieceAtCandidate != null && pieceAtCandidate.getTeamColor() != piece.getTeamColor()) {
                if (newRow + direction == promotionRow) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol + 1), ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol + 1), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol + 1), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol + 1), ChessPiece.PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol + 1), null));
                }
            }
        }

        // diagonal left
        if (newCol - 1 >= 1) {
            pieceAtCandidate = board.getPiece(new ChessPosition(newRow + direction, newCol - 1));
            if (pieceAtCandidate != null && pieceAtCandidate.getTeamColor() != piece.getTeamColor()) {
                if (newRow + direction == promotionRow) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol - 1), ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol - 1), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol - 1), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol - 1), ChessPiece.PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, newCol - 1), null));
                }
            }
        }
        return moves;
    }
}
