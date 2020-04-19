package lasers.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import lasers.ptui.LasersPTUI;

public class Safe {
    String[][] safeMatrix;
    String[][] safeMatrixSol;
    LasersPTUI lasersPTUI;

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
                } else if (counter == 1) {

                    for (int r = 0; r < row; r++) {
                        String dataRow = myReader.nextLine();
                        int c2 = 0;
                        for (int c = 0; c < col + col - 1; c += 2) {
                            safeMatrix[r][c2] = dataRow.substring(c, c + 1);
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
        if (safeMatrix[row][col].equals(FREE_SPOT) || safeMatrix[row][col].equals(BEAM)) {
            safeMatrix[row][col] = LASER;
            response = "Laser added at: (" + row + ", " + col + ")";
            beamRow(row, col, 0,1);
            beamCol(row, col, 0,1);

        } else {
            response = "Error adding laser at: (" + row + ", " + col + ")";
        }

        lasersPTUI.update(this, response);
    }

    public void removeLaser(int row, int col) {
        String response;
        if (safeMatrix[row][col].equals(LASER)) {
            safeMatrix[row][col] = FREE_SPOT;
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

    public void beamRow(int row, int col, int direction, int action) {
        if (row >= 0 && row < safeMatrix.length) {

            String cell = safeMatrix[row][col];
            if (direction == 0) {
                beamRow(row + 1, col, 1, action);
                beamRow(row - 1, col, -1, action);
            }

            if (action > 0) {
                if (cell.equals(FREE_SPOT) || cell.equals(BEAM)) {
                    safeMatrix[row][col] = BEAM;
                        beamRow(row+direction, col, direction, action);

                    } 
                }else{
                    if (cell.equals(BEAM) ) {
                        safeMatrix[row][col] = FREE_SPOT;
                        beamRow(row+direction, col, direction, action);
                    }else if(cell.equals(LASER)){
                        beamRow(row, col, direction*-1, action*-1);
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

}
