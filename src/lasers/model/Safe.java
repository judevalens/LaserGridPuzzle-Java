package lasers.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import lasers.ptui.LasersPTUI;

public class Safe {
    String[][] safeMatrix;
    String[][] safeMatrixSol;
    LasersPTUI lasersPTUI;

    Coordinate[][] safeMatrixC;
    Coordinate[][] safeMatrixSolC;

    String LASER = "L";
    String BEAM = "*";
    String PILLARS = "0124X";
    String FREE_SPOT = ".";




    public Safe(String safePath, LasersPTUI lasersPTUI) {
        this.lasersPTUI = lasersPTUI;
        createSafe(safePath);
    }

    public void createSafe(String safePath) {
        Path path = Paths.get("data/" + safePath);
        File myObj = new File(path.toString());
        Scanner myReader;
        try {
            myReader = new Scanner(myObj);
            int row = 0;
            int col = 0;
            int counter = 0;
            int nLinesToSol = 5;

            while (myReader.hasNextLine()) {

                if (counter == 0) {
                    String size = myReader.nextLine();

                    row = Integer.parseInt(size.substring(0, 1));
                    col = Integer.parseInt(size.substring(2, 3));
                    safeMatrix = new String[row][col];
                    safeMatrixSol = new String[row][col];

                    safeMatrixC =  new Coordinate[row][col];
                    safeMatrixSolC =  new Coordinate[row][col];
                } else if (counter == 1) {

                    for (int r = 0; r < row; r++) {
                        String dataRow = myReader.nextLine();
                        int c2 = 0;
                        for (int c = 0; c < col + col - 1; c += 2) {
                            safeMatrix[r][c2] = dataRow.substring(c, c + 1);

                            safeMatrixC[r][c2] = new Coordinate(r, c2, dataRow.substring(c, c + 1));
                            c2++;
                        }
                    }

                    for (int i = 0; i < nLinesToSol; i++) {
                        myReader.nextLine();
                    }

                } else {

                    for (int r = 0; r < row; r++) {
                        String dataRow = myReader.nextLine();

                        int c2 = 0;
                        for (int c = 2; c < (col + 2) + (col - 1); c += 2) {
                            safeMatrixSol[r][c2] = dataRow.substring(c, c + 1);
                            safeMatrixSolC[r][c2] = new Coordinate(r, c2, dataRow.substring(c, c + 1));
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

    public void addLaser(int row, int col) {
        String response;
            boolean status = safeMatrixC[row][col].updateElement(LASER);
            
            if(status){
                response = "Laser added at: (" + row + ", " + col + ")";
                beamRow(row, col, 0,1,safeMatrixC[row][col]);
                beamCol(row, col, 0,1);
            }else {
            response = "Error adding laser at: (" + row + ", " + col + ")";
        }

        lasersPTUI.update(this, response);
    }

    public void removeLaser(int row, int col) {
        String response;
            boolean status = safeMatrixC[row][col].updateElement(FREE_SPOT);
            if(status){
            response = "Laser removed at: (" + row + ", " + col + ")";
            beamRow(row, col, 0,-1);
            beamCol(row, col, 0,-1);
        } else {
            response = "Error removing laser at: (" + row + ", " + col + ")"; 
        }

        lasersPTUI.update(this, response);

    }

    public void verify() {

    }

    public void display() {

    }

    public void beamRow(int row, int col, int direction, int action, Coordinate laser) {
        if (row >= 0 && row < safeMatrix.length) {
            boolean status;
            String cell = safeMatrix[row][col];
            if (direction == 0) {
                beamRow(row + 1, col, 1, action,laser);
                beamRow(row - 1, col, -1, action,laser);
            }

            if (action > 0) {


                status = safeMatrixC[row][col].updateElement(BEAM,LASER);

                

                if (cell.equals(FREE_SPOT) || cell.equals(BEAM)) {
                    safeMatrix[row][col] = BEAM;
                        beamRow(row+direction, col, direction, action,laser);

                    } 
                }else{
                    if (cell.equals(BEAM)) {
                        safeMatrix[row][col] = FREE_SPOT;
                        beamRow(row+direction, col, direction, action,laser);
                    }
                }

        }
    }

    public void beamCol(int row, int col, int direction, int action) {
        if (col >= 0 && col < safeMatrix[0].length) {

            String cell = safeMatrix[row][col];

            if (direction == 0) {
                beamCol(row, col + 1, 1, action);
                beamCol(row, col - 1, -1, action);
            }

            if (action > 0) {
            if (cell.equals(FREE_SPOT) || cell.equals(BEAM)) {
                    safeMatrix[row][col] = BEAM;
                    beamCol(row, col + direction, direction, action);

                } 
            }else{
                if (cell.equals(BEAM) ) {
                    safeMatrix[row][col] = FREE_SPOT;
                    beamCol(row, col + direction, direction, action);
                } 
            }

        }
        }


    public String[][] getMatrix() {
        return safeMatrix;
    }

    class Coordinate{
        private int row,col;
        private String element;
        private ArrayList<Coordinate> laserDepency = new ArrayList<>();

        public Coordinate(int row,int col,String element){
            this.row  =  row;
            this.col = col;
            this.element = element;
        }

        public String getELement(){
            
            return element;
        }

        public boolean updateElement(String e){
            boolean r =  false;
            if (e.equals(LASER)){

                if(element.equals(FREE_SPOT) || element.equals(BEAM)){
                    element =  e;
                    r = true;
                }
                
            }else if(e.equals(FREE_SPOT)){
                if(element.equals(LASER)){
                    element = FREE_SPOT;
                    r = true;
                }
            }
            this.element = e;

            return r;
        }

        public boolean updateElement(String e, Coordinate laser,int action){
            boolean r = false;

            if(action > 0){
                if(element.equals(FREE_SPOT) || element.equals(BEAM)){
                    element= e;
                    laserDepency.add(laser);
                }
            }
            
           
            
            

            return false;
        }
        public void addDepency(Coordinate c){
            laserDepency.add(c);
        }

    }

}
