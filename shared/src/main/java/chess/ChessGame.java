package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board;
    TeamColor teamTurn;
    ChessPosition enPassantTarget;
    boolean whiteKingMoved;
    boolean blackKingMoved;
    boolean whiteRookKingsideMoved;
    boolean whiteRookQueensideMoved;
    boolean blackRookKingsideMoved;
    boolean blackRookQueensideMoved;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private ChessBoard copyBoard(ChessBoard original) {
        ChessBoard copy = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                copy.addPiece(pos, original.getPiece(pos));
            }
        }
        return copy;
    }

    private boolean isInCheck(TeamColor teamColor, ChessBoard boardToCheck) {
        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = boardToCheck.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor
                        && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = new ChessPosition(row, col);
                }
            }
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = boardToCheck.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    for (ChessMove move : piece.pieceMoves(boardToCheck, pos)) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void addCastlingMoves(ChessPiece piece, ChessPosition startPosition, Collection<ChessMove> moves) {
        if (piece.getPieceType() != ChessPiece.PieceType.KING) { return; }
        int kingStartCol = 5;
        int kingStartRow = (piece.getTeamColor() == TeamColor.WHITE) ? 1 : 8;
        if (startPosition.getRow() != kingStartRow || startPosition.getColumn() != kingStartCol) {
            return; // king not on starting square, no castling
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            boolean kingMoved = (piece.getTeamColor() == TeamColor.WHITE) ? whiteKingMoved : blackKingMoved;
            boolean rookKingsideMoved = (piece.getTeamColor() == TeamColor.WHITE) ? whiteRookKingsideMoved : blackRookKingsideMoved;
            boolean rookQueensideMoved = (piece.getTeamColor() == TeamColor.WHITE) ? whiteRookQueensideMoved : blackRookQueensideMoved;
            int row = (piece.getTeamColor() == TeamColor.WHITE) ? 1 : 8;

            if (!kingMoved && !isInCheck(piece.getTeamColor())) {
                // kingside
                if (!rookKingsideMoved
                        && board.getPiece(new ChessPosition(row, 6)) == null
                        && board.getPiece(new ChessPosition(row, 7)) == null) {
                    // check king doesn't pass through col 6
                    ChessBoard col6Copy = copyBoard(board);
                    col6Copy.addPiece(new ChessPosition(row, 6), piece);
                    col6Copy.addPiece(new ChessPosition(row, 5), null);
                    // check king doesn't land in check on col 7
                    ChessBoard col7Copy = copyBoard(board);
                    col7Copy.addPiece(new ChessPosition(row, 7), piece);
                    col7Copy.addPiece(new ChessPosition(row, 5), null);
                    if (!isInCheck(piece.getTeamColor(), col6Copy)
                            && !isInCheck(piece.getTeamColor(), col7Copy)) {
                        moves.add(new ChessMove(startPosition, new ChessPosition(row, 7), null));
                    }
                }
                // queenside
                if (!rookQueensideMoved
                        && board.getPiece(new ChessPosition(row, 4)) == null
                        && board.getPiece(new ChessPosition(row, 3)) == null
                        && board.getPiece(new ChessPosition(row, 2)) == null) {
                    // check king doesn't pass through col 4
                    ChessBoard col4Copy = copyBoard(board);
                    col4Copy.addPiece(new ChessPosition(row, 4), piece);
                    col4Copy.addPiece(new ChessPosition(row, 5), null);
                    // check king doesn't land in check on col 3
                    ChessBoard col3Copy = copyBoard(board);
                    col3Copy.addPiece(new ChessPosition(row, 3), piece);
                    col3Copy.addPiece(new ChessPosition(row, 5), null);
                    if (!isInCheck(piece.getTeamColor(), col4Copy)
                            && !isInCheck(piece.getTeamColor(), col3Copy)) {
                        moves.add(new ChessMove(startPosition, new ChessPosition(row, 3), null));
                    }
                }
            }
        }
    }

    private void updateCastlingFlags(ChessPiece piece, ChessMove move) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        }
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (move.getStartPosition().equals(new ChessPosition(1, 1))) {
                whiteRookQueensideMoved = true;
            } else if (move.getStartPosition().equals(new ChessPosition(1, 8))) {
                whiteRookKingsideMoved = true;
            } else if (move.getStartPosition().equals(new ChessPosition(8, 1))) {
                blackRookQueensideMoved = true;
            } else if (move.getStartPosition().equals(new ChessPosition(8, 8))) {
                blackRookKingsideMoved = true;
            }
        }
    }

    private void handleCastlingRookMove(ChessPiece piece, ChessMove move) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            int colDiff = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
            if (Math.abs(colDiff) == 2) {
                int row = move.getStartPosition().getRow();
                if (colDiff == 2) {
                    board.addPiece(new ChessPosition(row, 6), new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
                    board.addPiece(new ChessPosition(row, 8), null);
                } else {
                    board.addPiece(new ChessPosition(row, 4), new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
                    board.addPiece(new ChessPosition(row, 1), null);
                }
            }
        }
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> moves = new ArrayList<>(piece.pieceMoves(board, startPosition));
        Collection<ChessMove> validMoves = new ArrayList<>();
        // enPassant
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && enPassantTarget != null) {
            int direction = (piece.getTeamColor() == TeamColor.WHITE) ? 1 : -1;
            if (enPassantTarget.getRow() == startPosition.getRow() &&
                    Math.abs(enPassantTarget.getColumn() - startPosition.getColumn()) == 1) {
                ChessMove enPassantMove = new ChessMove(startPosition,
                        new ChessPosition(startPosition.getRow() + direction, enPassantTarget.getColumn()), null);
                moves.add(enPassantMove);
            }
        }

        addCastlingMoves(piece, startPosition, moves);

        for (ChessMove move : moves) {
            ChessBoard boardCopy = copyBoard(board);
            ChessPiece pieceToPlace = piece;
            if (move.getPromotionPiece() != null) {
                pieceToPlace = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            }
            boardCopy.addPiece(move.getEndPosition(), pieceToPlace);
            boardCopy.addPiece(move.getStartPosition(), null);
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                int colDiff = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
                int row = move.getStartPosition().getRow();
                if (colDiff == 2) {
                    boardCopy.addPiece(new ChessPosition(row, 6), new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
                    boardCopy.addPiece(new ChessPosition(row, 8), null);
                } else if (colDiff == -2) {
                    boardCopy.addPiece(new ChessPosition(row, 4), new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
                    boardCopy.addPiece(new ChessPosition(row, 1), null);
                }
            }
            if (!isInCheck(piece.getTeamColor(), boardCopy)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if  (piece == null) {
            throw new InvalidMoveException();
        }
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException();
        }
        ChessPiece pieceToPlace = piece;
        if (move.getPromotionPiece() != null) {
            pieceToPlace = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                move.getStartPosition().getColumn() != move.getEndPosition().getColumn() &&
                board.getPiece(move.getEndPosition()) == null) {
            board.addPiece(enPassantTarget, null);
        }
        board.addPiece(move.getEndPosition(), pieceToPlace);
        board.addPiece(move.getStartPosition(), null);

        // enPassant
        enPassantTarget = null;
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int rowDiff = move.getEndPosition().getRow() - move.getStartPosition().getRow();
            if (Math.abs(rowDiff) == 2) {
                enPassantTarget = move.getEndPosition();
            }
        }

        updateCastlingFlags(piece, move);
        handleCastlingRookMove(piece, move);

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, this.board);
    }

    private boolean mateHelper(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if  (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(pos).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) { return false; }
        return mateHelper(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) { return false; }
        return mateHelper(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
