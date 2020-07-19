import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import sudoku.Sudoku;
import sudoku.SudokuGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Callable;

@Command(name = "sudokusolver", mixinStandardHelpOptions = true)
public class SudokuSolver implements Callable<Integer> {

    @Parameters(paramLabel = "DIMENSIONS", description = "The dimensions of the sudoku and its blocks, in the order \"width height blockwidth blockheight\"")
    private int[] dimensions = {9, 9, 3, 3};

    @Option(names = {"-i", "--input"}, description = "The file containing the sudoku.")
    private String infile = "";

    @Option(names = {"-o", "--output"}, description = "The file to write the solved or generated sudoku to (OPTIONAL).")
    private String outfile = "";

    @Option(names = {"-g", "--generate"}, description = "Generates a new sudoku.")
    private boolean generate;

    public static void main(String[] args) {
        System.exit(new CommandLine(new SudokuSolver()).execute(args));
    }

    @Override
    public Integer call() {
        Sudoku newSudoku = null;
        if (generate) {
            if (dimensions[0] != dimensions[1] || dimensions[0] != dimensions[2] * dimensions[3]) {
                System.err.println("Invalid dimensions of sudoku or blocks.");
                System.exit(-1);
            }
            newSudoku = SudokuGenerator.generateNewSudoku(dimensions[1], dimensions[0], dimensions[3], dimensions[2]);
        } else {
            try {
                File input = new File(infile);
                if (infile.equals("") || input.createNewFile()) {
                    System.err.println("Invalid input file.");
                    System.exit(-1);
                } else {
                    newSudoku = SudokuGenerator.finishSudoku(readSudoku(input, dimensions[1], dimensions[0]), dimensions[3], dimensions[2]);
                }
            } catch (IOException e) {
                System.err.println("Invalid input file.");
            }
        }

        if (!outfile.equals("")) {
            try {
                File output = new File(outfile);
                FileWriter writer = new FileWriter(outfile);
                output.createNewFile();
                writer.write(newSudoku.toString());
                writer.close();
            } catch (IOException e) {
                System.err.println("Invalid output file.");
            }
        }

        System.out.println(newSudoku.toString());

//        int[][] matrix9 = {
//                {8, 0, 0, 0, 0, 0, 0, 0, 0},
//                {0, 0, 3, 6, 0, 0, 0, 0, 0},
//                {0, 7, 0, 0, 9, 0, 2, 0, 0},
//                {0, 5, 0, 0, 0, 7, 0, 0, 0},
//                {0, 0, 0, 0, 4, 5, 7, 0, 0},
//                {0, 0, 0, 1, 0, 0, 0, 3, 0},
//                {0, 0, 1, 0, 0, 0, 0, 6, 8},
//                {0, 0, 8, 5, 0, 0, 0, 1, 0},
//                {0, 9, 0, 0, 0, 0, 4, 0, 0}};
//        int[][] matrix6 = {
//                {0, 0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0, 0}
//        };
//        System.out.println(SudokuGenerator.finishSudoku(matrix9, 3, 3));
//        System.out.println(SudokuGenerator.finishSudoku(matrix6, 2, 3));
        return 0;
    }

    private int[][] readSudoku(File file, int height, int width) {
        int[][] sudoku = new int[height][width];
        int dim = 0;
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] numbers = line.split(" ");
                for (int i = 0; i < numbers.length; i++) {
                    int number = Integer.parseInt(numbers[i]);
                    if (number < 0 || number > width || numbers.length != width) {
                        System.err.println("Sudoku width not consistent with given width.");
                        System.exit(-1);
                    } else {
                        sudoku[dim][i] = number;
                    }
                }
                dim++;
            }
            if (dim != height) {
                System.err.println("Sudoku height not consistent with given height.");
                System.exit(-1);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Invalid input file.");
            System.exit(-1);
        }
        return sudoku;
    }
}
