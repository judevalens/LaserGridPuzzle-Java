package lasers.ptui;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import lasers.model.Safe;
import lasers.model.Safe.Coordinate;

public class LasersPTUI {
    /**
     * The main method
     * 
     * @param args command line arguments
     */
    static Safe safe;
    static int inputType = 0;

    public LasersPTUI(String safePath, String inputPath) {
        safe = new Safe(safePath, this);
        printMatrix(safe);
    }

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

    public static void printMatrix(Safe safe) {
        Coordinate[][] mat = safe.getMatrix();
        
        for (int r = 0; r < mat.length+2; r++) {
            for (int c = 0; c < mat[0].length+2; c++) {

                if(r > 1){
                    if(c == 0){
                        System.out.print(String.valueOf((r-2)%10));
                    }else if(c == 1){
                        System.out.print("|");
                    }else {
                        System.out.print(mat[r-2][c-2].getELement() + " ");
                    }
                }else{
                    if(r == 0){
                        if(c < 2){
                            System.out.print(" ");
                        }else{
                            System.out.print(String.valueOf((c-2)%10)+" ");
                        }
                    }else{
                        if(c < 2){
                            System.out.print(" ");
                        }else{
                            if(c<(mat[0].length+1)){
                                System.out.print("--");
                            }else{
                                System.out.print("-");

                            }
                        }
                    }
                }
                
            }
            System.out.println();
        }
    }

    public void update(Safe model, String status) {
        // TODO Auto-generated method stub

        if(status != null){
            System.out.println(status);
        }
        printMatrix(model);

        if(inputType == 1){
            input(null);
        }

    }

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
                    System.out.println("> "+command);
                    excutor(commands, inputType);

                }
                inputType = 1;
                sc.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        sc = new Scanner(System.in);
        if(sc.hasNextLine()){ 
            command = sc.nextLine();
            commands = command.split(" ");
            excutor(commands, 1);

        }
        sc.close();

    }

    public static void excutor(String[] commands, int index) {
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
                printMatrix(safe);
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
