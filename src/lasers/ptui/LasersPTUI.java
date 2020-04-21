package lasers.ptui;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import lasers.model.Safe;
import lasers.model.Safe.Coordinate;

/**
 * Plain User Interface
 * 
 * @author Jude Paulemon
 */

public class LasersPTUI {

    static Safe safe;
    static int inputType = 0;
    static String commandList = "arhqvd";

    /**
     * Create a ptui and creates a safe object
     * 
     * @param safePath
     * @param inputPath
     */
    public LasersPTUI(String safePath, String inputPath) {
        safe = new Safe(safePath, this);
        printMatrix(safe);
    }

    /**
     * The main method
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // check sanity of input
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java LasersPTUI safe-file [input]");
        } else {

            if (args.length == 1) {
                new LasersPTUI(args[0], null);
                inputType = 1;
                input(null);
            } else {
                new LasersPTUI(args[0], args[1]);
                input(args[1]);
                input(null);
            }
        }
    }

    /**
     * print a matrix that display the current configuration of a safe
     * 
     * @param safe
     */
    public static void printMatrix(Safe safe) {
        Coordinate[][] mat = safe.getMatrix();

        for (int r = 0; r < mat.length + 2; r++) {
            for (int c = 0; c < mat[0].length + 2; c++) {

                if (r > 1) {
                    if (c == 0) {
                        System.out.print(String.valueOf((r - 2) % 10));
                    } else if (c == 1) {
                        System.out.print("|");
                    } else {
                        System.out.print(mat[r - 2][c - 2].getELement() + " ");
                    }
                } else {
                    if (r == 0) {
                        if (c < 2) {
                            System.out.print(" ");
                        } else {
                            System.out.print(String.valueOf((c - 2) % 10) + " ");
                        }
                    } else {
                        if (c < 2) {
                            System.out.print(" ");
                        } else {
                            if (c < (mat[0].length + 1)) {
                                System.out.print("--");
                            } else {
                                System.out.print("-");

                            }
                        }
                    }
                }

            }
            System.out.println();
        }
    }

    /**
     * Updates the view when called by a model
     * 
     * @param model
     * @param status
     */
    public static void update(Safe model, String status) {
        // TODO Auto-generated method stub

        if (status != null) {
            System.out.println(status);
        }
        if (model != null) {
            printMatrix(model);
        }

        if (inputType == 1) {
            input(null);
        }

    }

    /**
     * Get input either from standard input or a text file
     * 
     * @param inputPath
     */
    public static void input(String inputPath) {
        Scanner sc;
        String command;
        String[] commands;
        if (inputPath != null) {
            Path path = Paths.get("data/" + inputPath);
            File myObj = new File(path.toString());
            try {
                sc = new Scanner(myObj);

                while (sc.hasNextLine()) {
                    command = sc.nextLine();

                    commands = command.split(" ");
                    inputType = 0;
                    System.out.println("> " + command);
                    excutor(commands, inputType);

                }
                inputType = 1;
                sc.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        sc = new Scanner(System.in);
        if (sc.hasNextLine()) {
            command = sc.nextLine().toLowerCase();
            commands = command.split(" ");
            excutor(commands, 1);

        }
        sc.close();

    }

    /**
     * Call diffrent methods based on the command given
     * 
     * @param commands
     * @param index
     */
    public static void excutor(String[] commands, int index) {

        Boolean isCommandValid = true;
        String badCommand = "";

        try {
            String initial = commands[index].substring(0, 1).toLowerCase();

            if (!commandList.contains(initial)) {
                update(null, "Unrecognized command: " + commands[index]);
                isCommandValid  = false;
            } else if (initial.equals("a") || initial.equals("r")) {
                if (commands.length != (3 + index)) {
                    update(null, "Incorrect coordinates");
                    isCommandValid  = false;

                }
            }

            if(isCommandValid){

            

            switch (initial) {
                case "a":
                    safe.addLaser(Integer.parseInt(commands[index + 1]), Integer.parseInt(commands[index + 2]));
                    break;
                case "r":
                    safe.removeLaser(Integer.parseInt(commands[index + 1]), Integer.parseInt(commands[index + 2]));
                    break;
                case "v":
                    safe.verify();
                    break;
                case "d":
                    update(safe, null);
                    break;
                case "q":
                    System.exit(0);
                case "h":
                    printHelp();

                    update(null, null);
                    break;

            }

        }
        } catch (ArrayIndexOutOfBoundsException e) {
            update(null, "Invalid number a argument");
            //e.printStackTrace();
        }

    }

    /**
     * Prints a help message when the user ask for it
     */
    static void printHelp() {
        String[] helpMessage = { "a|add r c: Add laser to (r,c)", "d|display: Display safe",
                "h|help: Print this help message", "q|quit: Exit program", "r|remove r c: Remove laser from (r,c)",
                "v|verify: Verify safe correctness" };

        for (String h : helpMessage) {
            System.out.println(h);
        }
    }

}
