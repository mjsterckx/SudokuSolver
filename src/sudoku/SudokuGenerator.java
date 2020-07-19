package sudoku;

public class SudokuGenerator {
    private static Sudoku generateSudoku(Sudoku sudoku) {
        return sudoku.solve();
    }

    public static Sudoku generateNewSudoku(int sudokuHeight, int sudokuWidth, int blockHeight, int blockWidth) {
        return generateSudoku(new Sudoku(sudokuHeight, sudokuWidth, blockHeight, blockWidth).solve());
    }

    public static Sudoku finishSudoku(int[][] sudokuMatrix, int blockHeight, int blockWidth) {
        return generateSudoku(new Sudoku(sudokuMatrix, blockHeight, blockWidth).solve());
    }
}
