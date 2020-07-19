package sudoku;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Sudoku {
    private int[][] sudokuMatrix;
    private int[][][] possibilityMatrix;
    private int sudokuHeight;
    private int sudokuWidth;
    private int blockHeight;
    private int blockWidth;

    Sudoku(int[][] sudokuMatrix, int blockHeight, int blockWidth) {
        this.sudokuHeight = sudokuMatrix.length;
        this.sudokuWidth = sudokuMatrix[0].length;
        this.sudokuMatrix = new int[sudokuHeight][sudokuWidth];
        this.possibilityMatrix = new int[sudokuHeight][sudokuWidth][];
        this.blockHeight = blockHeight;
        this.blockWidth = blockWidth;
        for (int i = 0; i < sudokuHeight; i++) {
            System.arraycopy(sudokuMatrix[i], 0, this.sudokuMatrix[i], 0, sudokuWidth);
        }
    }

    Sudoku(int sudokuHeight, int sudokuWidth, int blockHeight, int blockWidth) {
        this.sudokuHeight = sudokuHeight;
        this.sudokuWidth = sudokuWidth;
        this.blockHeight = blockHeight;
        this.blockWidth = blockWidth;
        this.sudokuMatrix = new int[sudokuHeight][sudokuWidth];
        this.possibilityMatrix = new int[sudokuHeight][sudokuWidth][];
    }

    private int[] getCoordinatePossibilities(int r, int c) {
        int[] taken = new int[sudokuHeight * 3];
        int index = 0;
        if (sudokuMatrix[r][c] > 0) {
            return new int[]{sudokuMatrix[r][c]};
        }
        for (int i = 0; i < sudokuWidth; i++) {
            taken[index] = sudokuMatrix[r][i];
            index++;
        }
        for (int i = 0; i < sudokuHeight; i++) {
            taken[index] = sudokuMatrix[i][c];
            index++;
        }
        r = (r / blockHeight) * blockHeight;
        c = (c / blockWidth) * blockWidth;
        for (int i = r; i < r + blockHeight; i++) {
            for (int j = c; j < c + blockWidth; j++) {
                taken[index] = sudokuMatrix[i][j];
                index++;
            }
        }
        int[] intermediateResult = new int[sudokuHeight];
        int resultIndex = 0;
        for (int i = 1; i <= sudokuHeight; i++) {
            boolean isTaken = false;
            for (int alreadyTaken : taken) {
                if (alreadyTaken == i) {
                    isTaken = true;
                    break;
                }
            }
            if (!isTaken) {
                intermediateResult[resultIndex] = i;
                resultIndex++;
            }
        }
        return Arrays.copyOf(intermediateResult, resultIndex);
    }

    private void optimizePossibilities() {
        int reduced;
        do {
            reduced = 0;
            for (int i = 0; i < sudokuHeight; i++) {
                for (int j = 0; j < sudokuWidth; j++) {
                    possibilityMatrix[i][j] = getCoordinatePossibilities(i, j);
                    if (possibilityMatrix[i][j].length == 1 && this.sudokuMatrix[i][j] == 0) {
                        this.sudokuMatrix[i][j] = possibilityMatrix[i][j][0];
                        reduced++;
                    }
                }
            }
        } while (reduced > 0);
    }

    private int[][] solveRecursive() {
        int currentHeight = 0;
        int currentWidth = 0;
        int r = -1;
        int c = -1;
        while (currentHeight < sudokuHeight) {
            if (possibilityMatrix[currentHeight][currentWidth].length > 1) {
                r = currentHeight;
                c = currentWidth;
                break;
            } else if (possibilityMatrix[currentHeight][currentWidth].length < 1) {
                return null;
            } else {
                sudokuMatrix[currentHeight][currentWidth] = possibilityMatrix[currentHeight][currentWidth][0];
            }
            currentWidth++;
            if (currentWidth == sudokuWidth) {
                currentHeight++;
                currentWidth = 0;
            }
        }
        if (r == -1) {
            return sudokuMatrix;
        } else {
            int[] possibilities = possibilityMatrix[r][c];
            shuffleArray(possibilities);
            for (int i : possibilities) {
                sudokuMatrix[r][c] = i;
                Sudoku trialSudoku = new Sudoku(sudokuMatrix, this.blockHeight, this.blockWidth);
                trialSudoku.optimizePossibilities();
                int[][] potentialSolution = trialSudoku.solveRecursive();
                if (potentialSolution != null) {
                    return sudokuMatrix;
                }
            }
        }
        return null;
    }

    private void shuffleArray(int[] ar)
    {
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    Sudoku solve() {
        try {
            boolean completed;
            do {
                optimizePossibilities();
                completed = true;
                for (int i = 0; i < sudokuHeight && completed; i++) {
                    if (IntStream.of(sudokuMatrix[i]).anyMatch(x -> x == 0)) {
                        completed = false;
                    }
                }
                if (!completed) {
                    sudokuMatrix = solveRecursive();
                }
            } while (!completed);
            return this;
        } catch (NullPointerException e) {
            System.err.println("Sudoku is unsolvable.");
            System.exit(-1);
            return this;
        }
    }

//    private boolean checkValidSudoku() {
//        for (int i = 0; i < sudokuHeight; i++) {
//            Set<Integer> horizontal = new HashSet<>();
//            for (int j = 0; j < sudokuWidth; j++) {
//                horizontal.add(sudokuMatrix[i][j]);
//            }
//            if (horizontal.size() < sudokuWidth || horizontal.contains(0)) return false;
//        }
//        for (int i = 0; i < sudokuWidth; i++) {
//            Set<Integer> vertical = new HashSet<>();
//            for (int j = 0; j < sudokuHeight; j++) {
//                vertical.add(sudokuMatrix[j][i]);
//            }
//            if (vertical.size() < sudokuHeight || vertical.contains(0)) return false;
//        }
//        int[] blocksVertical = new int[sudokuHeight / blockHeight];
//        int[] blocksHorizontal = new int[sudokuWidth / blockWidth];
//        for (int i = 0; i < blocksVertical.length; i++) {
//            blocksVertical[i] = i * blockHeight;
//        }
//        for (int i = 0; i < blocksHorizontal.length; i++) {
//            blocksHorizontal[i] = i * blockWidth;
//        }
//        for (int i : blocksVertical) {
//            for (int j : blocksHorizontal) {
//                Set<Integer> block = new HashSet<>();
//                for (int k = i; k < i + blockHeight; k++) {
//                    for (int l = j; l < j + blockWidth; l++) {
//                        block.add(sudokuMatrix[k][l]);
//                    }
//                }
//                if (block.size() < blockHeight * blockWidth) return false;
//            }
//        }
//        return true;
//    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int[] i : sudokuMatrix) {
            for (int j : i) {
                builder.append(j);
                builder.append(' ');
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}