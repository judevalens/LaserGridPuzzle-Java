package lasers.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import lasers.ptui.LasersPTUI;

/**
 * This class represents a safe
 * 
 * @author Jude Paulemon
 */
public class Safe {
    String[][] safeMatrix;
    String[][] safeMatrixSol;
    LasersPTUI lasersPTUI;

    Coordinate[][] safeMatrixC;
    Coordinate[][] safeMatrixSolC;

    String LASER = "L";
    String BEAM = "*";
    String PILLARS = "01234X";
    String FREE_SPOT = ".";

    /**
     * Creates a safe object
     * 
     * @param safePath   path that contains the configuration to build a safe
     * @param lasersPTUI the PTUI that will interact with this model
     */
    public Safe(String safePath, LasersPTUI lasersPTUI) {
        this.lasersPTUI = lasersPTUI;
        createSafe(safePath);
    }

    /**
     * Reads a file and build a matrix from the file. This method loads the safe and
     * the safe solution into two distinct matrices
     * 
     * @param safePath path that contains the configuration to build a safe
     */
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
                    String[] coordinates = myReader.nextLine().split(" ");
                    row = Integer.parseInt(coordinates[0]);
                    col = Integer.parseInt(coordinates[1]);
                    safeMatrix = new String[row][col];
                    safeMatrixSol = new String[row][col];

                    safeMatrixC = new Coordinate[row][col];
                    safeMatrixSolC = new Coordinate[row][col];
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
                    if (myReader.hasNextLine()) {
                        for (int i = 0; i < nLinesToSol; i++) {
                            myReader.nextLine();
                        }

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
            System.out.println("File cannot be found");
            System.exit(1);
            // e.printStackTrace();
        }
    }

    /**
     * adds lazer at the given coordinates
     * 
     * @param row
     * @param col
     */
    public void addLaser(int row, int col) {
        String response;
        boolean status = safeMatrixC[row][col].updateElement(LASER);

        if (status) {
            response = "Laser added at: (" + row + ", " + col + ")";
            beamRow(row, col, 0, 1, safeMatrixC[row][col]);
            beamCol(row, col, 0, 1, safeMatrixC[row][col]);
        } else {
            response = "Error adding laser at: (" + row + ", " + col + ")";
        }

        lasersPTUI.update(this, response);
    }

    /**
     * removes lazer at the given coordinates
     * 
     * @param row
     * @param col
     */
    public void removeLaser(int row, int col) {
        String response;
        boolean status = safeMatrixC[row][col].updateElement(FREE_SPOT);
        if (status) {
            response = "Laser removed at: (" + row + ", " + col + ")";
            beamRow(row, col, 0, -1, safeMatrixC[row][col]);
            beamCol(row, col, 0, -1, safeMatrixC[row][col]);
        } else {
            response = "Error removing laser at: (" + row + ", " + col + ")";
        }

        lasersPTUI.update(this, response);

    }

    /**
     * verifies that a safe is correct
     * 
     * @return true if safe is correct, false otherwise
     */
    public void verify() {
        boolean error = false;
        int r = 0;
        int c = 0;
        while (r < safeMatrixC.length && !error) {
            c = 0;
            while (c < safeMatrixC[0].length) {
                Coordinate cell = safeMatrixC[r][c];
                String element = cell.getELement();
                if (element.equals(LASER)) {
                    if (cell.getAdjacentLasers() > 0) {
                        error = true;
                        break;
                    }
                } else if (PILLARS.contains(element)) {
                    if (!element.equals("X")) {
                        if (cell.getAdjacentPillarNumber() != cell.getPillarNumber()) {
                            
                            error = true;
                            break;
                        }
                    }
                } else if (!element.equals(BEAM)) {

                    error = true;
                    break;
                }

                c++;
            }
            if (error) {
                break;
            }

            r++;
        }

        String response;

        if (error) {
            response = "Error verifying at: (" + r + ", " + c + ")";
        } else {
            response = "Safe is fully verified!";
        }

        lasersPTUI.update(this, response);

    }

    /**
     * when a lazer is added this method adds beam on the cells that Vertically
     * adjacent to the lazer It stops when a pillar is encountered
     * 
     * @param row
     * @param col
     * @param direction up or down (1 or -1)
     * @param action    add or remove beams (1 or -1)
     * @param laser     the lazer that emits those beams
     */
    public void beamRow(int row, int col, int direction, int action, Coordinate laser) {
        if (row >= 0 && row < safeMatrix.length) {
            boolean status;
            if (direction == 0) {
                beamRow(row + 1, col, 1, action, laser);
                beamRow(row - 1, col, -1, action, laser);
            } else {
                if (action > 0) {

                    status = safeMatrixC[row][col].updateElement(BEAM, laser, action);

                    if (status) {
                        beamRow(row + direction, col, direction, action, laser);

                    }
                } else {
                    status = safeMatrixC[row][col].updateElement(BEAM, laser, action);

                    if (status) {
                        beamRow(row + direction, col, direction, action, laser);
                    }
                }

            }

        }
    }

    /**
     * when a lazer is added this method adds beam on the cells that horizontally
     * adjacent to the lazer It stops when a pillar is encountered
     * 
     * @param row
     * @param col
     * @param direction up or down (1 or -1)
     * @param action    add or remove beams (1 or -1)
     * @param laser     the lazer that emits those beams
     */
    public void beamCol(int row, int col, int direction, int action, Coordinate laser) {
        if (col >= 0 && col < safeMatrix[0].length) {

            boolean status;
            if (direction == 0) {
                beamCol(row, col + 1, 1, action, laser);
                beamCol(row, col - 1, -1, action, laser);
            } else {
                if (action > 0) {

                    status = safeMatrixC[row][col].updateElement(BEAM, laser, action);

                    if (status) {
                        beamCol(row, col + direction, direction, action, laser);

                    }
                } else {
                    status = safeMatrixC[row][col].updateElement(BEAM, laser, action);

                    if (status) {
                        beamCol(row, col + direction, direction, action, laser);
                    }

                }
            }

        }
    }

    /**
     * Nested class that represent a coordinate
     *
     * @author Jude Paulemon
     */
    public Coordinate[][] getMatrix() {
        return safeMatrixC;
    }

    public class Coordinate {
        private int row, col;
        private String element;
        private int adjacentLasers = 0;
        private int pillarNumber = 0;
        private int adjacentPillarNumber = 0;

        private ArrayList<Coordinate> laserDepency = new ArrayList<>();

        /**
         * Create a coordinate object
         * 
         * @param row
         * @param col
         * @param element can be a lazer, a pilar , a beam or a free cell
         */
        public Coordinate(int row, int col, String element) {
            this.row = row;
            this.col = col;
            this.element = element;

            if (PILLARS.contains(element)) {
                if (element.equals("X")) {
                    pillarNumber = -1;
                } else {
                    pillarNumber = Integer.parseInt(element);
                }
            }
        }

        /**
         * 
         * @return return the current element that this cell contains
         */
        public String getELement() {

            return element;
        }

        /**
         * this method add or remove a lazer
         * 
         * @param e the element to update the cell with
         * @return true if a lazer has been added or removed
         */
        public boolean updateElement(String e) {
            boolean r = false;
            if (e.equals(LASER)) {

                if (element.equals(FREE_SPOT) || element.equals(BEAM)) {
                    element = e;
                    r = true;
                }
            } else if (e.equals(FREE_SPOT)) {
                if (element.equals(LASER)) {
                    if (laserDepency.size() == 0) {
                        this.element = FREE_SPOT;

                    } else {
                        this.element = BEAM;
                    }
                    r = true;
                }
            }

            return r;
        }

        /**
         * This method adds and removes beams
         * 
         * @param e      the element to update the cell with
         * @param laser  the lazer to wich this beam depent
         * @param action > 0 we add a bea. < 0 we remove beam
         * @return return false when a pillar has been encountered
         */
        public boolean updateElement(String e, Coordinate laser, int action) {

            boolean r = false;
            // when its a free spot or beam/ we add the new laze depency and change the
            // symbol to a bea
            // when its a laser we just add the depency and check the adjacent cells
            // only a pilar can stop a lazer's trajectory

            if (action > 0) {

                if (element.equals(FREE_SPOT) || element.equals(BEAM)) {
                    element = e;
                    laserDepency.add(laser);
                    r = true;
                } else if (element.equals(LASER)) {
                    laser.setAdjacentLasers(laser.getAdjacentLasers() + 1);
                    laserDepency.add(laser);
                    adjacentLasers++;
                    r = true;
                } else if (PILLARS.contains(element)) {
                    int lDistance = (Math.abs(this.row - laser.getRow()));
                    int hDistance = (Math.abs(this.col - laser.getCol()));

                    if (lDistance == 1 || hDistance == 1) {
                     

                        adjacentPillarNumber += 1;
                    }
                }
            } else {
                if (element.equals(BEAM)) {


                    laserDepency.remove(laserDepency.size() - 1);
                    if (laserDepency.size() == 0) {
                        element = FREE_SPOT;
                    }
                    r = true;
                } else if (element.equals(LASER)) {
                    laserDepency.remove(laserDepency.size() - 1);
                    laser.setAdjacentLasers(laser.getAdjacentLasers() - 1);
                    adjacentLasers -= 1;
                    r = true;
                } else if (PILLARS.contains(element)) {
                    int lDistance = (Math.abs(this.row - laser.getRow()));
                    int hDistance = (Math.abs(this.col - laser.getCol()));

                    if (lDistance == 1 || hDistance == 1) {
                       
                        adjacentPillarNumber -= 1;
                    }
                }
            }

            return r;
        }

        /**
         * Getters and setter
         **/
        public int getAdjacentLasers() {
            return adjacentLasers;
        }

        public void setAdjacentLasers(int adjacentLasers) {
            this.adjacentLasers = adjacentLasers;
        }

        public int getPillarNumber() {
            return pillarNumber;
        }

        public void setPillarNumber(int pillarNumber) {
            this.pillarNumber = pillarNumber;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public int getAdjacentPillarNumber() {
            return adjacentPillarNumber;
        }

        public void setAdjacentPillarNumber(int adjacentPillarNumber) {
            this.adjacentPillarNumber = adjacentPillarNumber;
        }

    }

}
