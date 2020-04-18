package lasers.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Safe {
    String[][] safeMatrix;
    String[][] safeMatrixSol;
    public Safe(String safePath){
        createSafe(safePath);
    }

    public void createSafe(String safePath){
        Path path = Paths.get("data/"+safePath);
        File myObj = new File(path.toString());
        Scanner myReader;
        try {
            myReader = new Scanner(myObj);
            int row = 0;
            int col = 0;
            int counter = 0;
            int nLinesToSol = 5;

            while (myReader.hasNextLine()) {

                if(counter == 0){
                    String size = myReader.nextLine();

                    row = Integer.parseInt(size.substring(0,1));
                    col = Integer.parseInt(size.substring(2,3));
                    safeMatrix = new String[row][col];
                    safeMatrixSol = new String[row][col];
                }else if(counter == 1){

                    for(int r = 0; r < row; r++){
                        String dataRow = myReader.nextLine();
                        int c2 = 0;
                        for(int c = 0; c < col+col-1; c += 2){
                            safeMatrix[r][c2] = dataRow.substring(c,c+1);
                            c2++;
                        }
                    }

                    for(int i =0; i < nLinesToSol; i++){
                        myReader.nextLine();
                    }

                }else{

                    for(int r = 0; r < row; r++){
                        String dataRow = myReader.nextLine();

                        int c2 = 0;
                        for(int c = 2; c < (col+2)+(col-1); c += 2){
                            safeMatrixSol[r][c2] = dataRow.substring(c,c+1);
                            c2++;
                        }
                    }

                }
                
                counter++;
                
              }
              myReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addLaser(int row, int col){
        safeMatrix[row][col] = "L";
    }
   
    public String[][] getMatrix(){
return safeMatrixSol;
    }

}
