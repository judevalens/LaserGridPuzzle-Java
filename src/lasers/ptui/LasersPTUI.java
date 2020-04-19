package lasers.ptui;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import lasers.model.Safe;

public class LasersPTUI {
    /**
     * The main method
     * 
     * @param args command line arguments
     */
    static Safe safe;

    public LasersPTUI(String safePath, String inputPath) {
        safe = new Safe(safePath, this);
    }

    public static void main(String[] args) {
        // check sanity of input
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java LasersPTUI safe-file [input]");
        } else {

            if (args.length == 1) {
                new LasersPTUI(args[0], null);
                printMatrix();
            } else {
                new LasersPTUI(args[0], args[1]);
            }
        }
    }

    public static void printMatrix() {
        String[][] mat = safe.getMatrix();

        for (int r = 0; r < mat.length; r++) {
            for (int c = 0; c < mat[0].length; c++) {
                System.out.print(mat[r][c] + " ");
            }
            System.out.println();
        }
    }

    public void update(Safe model, String status) {
        // TODO Auto-generated method stub

    }

    public void input(String inputPath) {
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
                    System.out.println(command);

                    commands = command.split(" ");

                    excutor(commands, 0);

                }
                sc.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        sc = new Scanner(System.in);
        command = sc.nextLine();
        commands = command.split(" ");
        excutor(commands, 1);
        sc.close();

    }

    public void excutor(String[] commands, int index) {
        String initial = commands[index].substring(0, 1).toLowerCase();

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
                safe.display();
                break;
            case "q":
                System.exit(0);
            case "h":
                break;

        }

    }

    enum Command {
        a, d, h, r, v, q
    }

}
