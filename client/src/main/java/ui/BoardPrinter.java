package ui;

import chess.*;

public class BoardPrinter {

    private static final String SET_BG_COLOR_FOREST_GREEN = "\u001b[48;5;22m";

    public static void draw(ChessBoard board, boolean isBlack) {
        System.out.println();
        printBoard(board, isBlack);
        System.out.println();
    }

    private static void printBoard(ChessBoard board, boolean isBlack) {
        String[] colLabels = isBlack ?
                new String[]{"h", "g", "f", "e", "d", "c", "b", "a"} :
                new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};

        printColLabels(colLabels);

        int rowStart = isBlack ? 1 : 8;
        int rowEnd = isBlack ? 8 : 1;
        int rowStep = isBlack ? 1 : -1;

        for (int row = rowStart; row != rowEnd + rowStep; row += rowStep) {
            System.out.print(SET_BG_COLOR_FOREST_GREEN);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            System.out.print(" " + row + " ");

            int colStart = isBlack ? 8 : 1;
            int colEnd = isBlack ? 1 : 8;
            int colStep = isBlack ? -1 : 1;

            for (int col = colStart; col != colEnd + colStep; col += colStep) {
                boolean isLightSquare = (row + col) % 2 != 0;
                if (isLightSquare) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                System.out.print(getPieceString(piece));
            }

            System.out.print(SET_BG_COLOR_FOREST_GREEN);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            System.out.print(" " + row + " ");
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.println();
        }

        printColLabels(colLabels);
    }

    public static void drawWithHighlights(ChessBoard board, boolean isBlack, java.util.Collection<ChessMove> highlights) {
        System.out.println();
        printBoardWithHighlights(board, isBlack, highlights);
        System.out.println();
    }

    private static void printBoardWithHighlights(ChessBoard board, boolean isBlack, java.util.Collection<ChessMove> highlights) {
        String[] colLabels = isBlack ?
                new String[]{"h", "g", "f", "e", "d", "c", "b", "a"} :
                new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};

        printColLabels(colLabels);

        int rowStart = isBlack ? 1 : 8;
        int rowEnd = isBlack ? 8 : 1;
        int rowStep = isBlack ? 1 : -1;

        for (int row = rowStart; row != rowEnd + rowStep; row += rowStep) {
            System.out.print(SET_BG_COLOR_FOREST_GREEN);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            System.out.print(" " + row + " ");

            int colStart = isBlack ? 8 : 1;
            int colEnd = isBlack ? 1 : 8;
            int colStep = isBlack ? -1 : 1;

            for (int col = colStart; col != colEnd + colStep; col += colStep) {
                ChessPosition pos = new ChessPosition(row, col);
                boolean isHighlighted = highlights != null && highlights.stream()
                        .anyMatch(m -> m.getEndPosition().equals(pos));
                boolean isLightSquare = (row + col) % 2 != 0;

                if (isHighlighted) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                } else if (isLightSquare) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }

                ChessPiece piece = board.getPiece(pos);
                System.out.print(getPieceString(piece));
            }

            System.out.print(SET_BG_COLOR_FOREST_GREEN);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            System.out.print(" " + row + " ");
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.println();
        }

        printColLabels(colLabels);
    }

    private static void printColLabels(String[] labels) {
        System.out.print(SET_BG_COLOR_FOREST_GREEN);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.print("   ");
        for (String label : labels) {
            System.out.print("\u2003" + label + " ");
        }
        System.out.print("   ");
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
    }

    private static String getPieceString(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }
        String colorCode = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                EscapeSequences.SET_TEXT_COLOR_WHITE :
                EscapeSequences.SET_TEXT_COLOR_MAGENTA;
        String pieceStr = switch (piece.getPieceType()) {
            case KING   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                    EscapeSequences.WHITE_KING   : EscapeSequences.BLACK_KING;
            case QUEEN  -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                    EscapeSequences.WHITE_QUEEN  : EscapeSequences.BLACK_QUEEN;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                    EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                    EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                    EscapeSequences.WHITE_ROOK   : EscapeSequences.BLACK_ROOK;
            case PAWN   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                    EscapeSequences.WHITE_PAWN   : EscapeSequences.BLACK_PAWN;
        };
        return colorCode + pieceStr;
    }
}