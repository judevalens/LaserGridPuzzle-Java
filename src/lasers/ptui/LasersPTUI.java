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


    public LasersPTUI(String safePath, String inputPath){
        safe = new Safe(safePath,this);
    }

   

    public static void main(String[] args) {
        // check sanity of input
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java LasersPTUI safe-file [input]");
        } else {
            
            if(args.length == 1){
                new LasersPTUI(args[0],null);
                printMatrix();
            }else{
                new LasersPTUI(args[0],args[1]);
            }
        }
    }

    public static void printMatrix(){
        String[][] mat = safe.getMatrix();

        for(int r = 0; r < mat.length; r++){
            for(int c = 0; c < mat[0].length;c++){
                System.out.print(mat[r][c] + " ");
            }
            System.out.println();
        }
    }

    public void readFile(String path, String type) {

        File myObj = new File(path);
        Scanner myReader;
        try {
            myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
              }
              myReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        

    }

    public void update(Safe model, String status) {
        // TODO Auto-generated method stub

    }

    public void input(String inputPath){
        if(inputPath != null){
        Path path = Paths.get("data/"+inputPath);
        File myObj = new File(path.toString());
        Scanner myReader;
        try {
            myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                String command = myReader.nextLine();
                System.out.println(command);


                String[] commands = command.split(" ");

                if


            


              }
              myReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    }
    


    
    public void excutor(String command,Integer row, Integer col){
        if(c)

    }

    enum Command{
        a,
        d,
        h,
        r,
        v,
        q
    }

}
