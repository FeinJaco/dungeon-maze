package maze;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.Scanner;


public class MazeModel {

    private final int RELICS_REQUIRED = 3;
    private static final boolean MEMORY = false;
    private static final int DIVISIONS = 90;
    private final int MAP_SIZE = 45;
    private final double SPAWN_RATE = 0.05;

    private final int[][] RELICS = {{38, 33}, {40, 7}, {11, 27}, {23, 32}, {29, 17}, {9, 9}};    //add relicCount at certain coordinates (6 total)

    private int creatCount = 0, visCreatures = 0; //number of creatures
    private int py, px, selectX, selectY; //coordinates of player, coordinates of selected enemy creature
    private int relicCount = 0; //how many relics the player has collected
    private int creatSelected;
    private boolean help = true;    //help menu on or off

    private Scanner input = new Scanner(System.in);
    private Random r = new Random();
    /**
     * 0: empty, 1: wall, 2: door, 3: start, 4: end, 5: unknown
     */
    private int[][] maze = new int[MAP_SIZE][MAP_SIZE];  //array of map cells on grid
    private Creature[][] creatures = new Creature[MAP_SIZE][MAP_SIZE];   //array of creatures on grid
    private int[][] visArray = new int[MAP_SIZE][MAP_SIZE]; //array of cells visible on grid
    private int[][] visCreatArr = new int[1][2];

    /*
    Constructor
     */ //Creates array of integers representing different cells (e.g. wall, empty, door) based on downloaded txt file
    public MazeModel() throws FileNotFoundException, IOException {
        int x, y, index, end;   //grid coordinates, index of row String, index of character after cell abbreviation in txt file
        String line, cell;  //current line being read from txt file, cell abbreviation in txt file
        /*
        for (y = 0; y < 45; y++) {
            for (x = 0; x < 45; x++) {
                if (y == 0 || y == 44 || x == 0 || x == 44)
                    maze[y][x] = 1;
                else
                    maze[y][x] = 0;
            }
        }
         */

        FileReader reader = new FileReader("MazeMap45.txt");    //FileReader for map txt file
        try (BufferedReader in = new BufferedReader(reader)) //BufferedReader to read txt file
        {
            for (y = 0; (line = in.readLine()) != null; y++) {
                //loop through rows in matrix and txt file until no more lines
                for (index = 0, x = 0; index < line.length(); index++, x++) {   //loop through indeces and x-values in matrix
                    if (line.substring(index, index + 1).equals("\t")) {    //encode wall if txt file has tab
                        maze[y][x] = 1; //wall at current coordinates in maze
                    } else {
                        //group characters between tabs
                        for (end = index; !line.substring(end, end + 1).equals("\t"); end++) {
                        }
                        cell = line.substring(index, end);
                        //encode cells into mazeArray based on txt file
                        switch (cell) {
                            case "F":   //empty space
                            case "SU":  //not door or start
                            case "SD":  //not door or end
                                maze[y][x] = 0;  //encode empty space into mazeArray
                                if (r.nextDouble() < SPAWN_RATE) {
                                    creatures[y][x] = new Creature("X", 10 + r.nextInt(6), 11, true);
                                    creatCount++;
                                }
                                break;
                            case "SUU": //start
                                maze[y][x] = 2;  //encode starting place into mazeArray
                                creatures[y][x] = new Creature("Player", 50, 14, true);  //create player Character at start in creatArray
                                creatures[y][x].setVisible(true);
                                //set player coordinates
                                px = x;
                                py = y;
                                creatCount++;   //increase creature count
                                break;
                            case "SDD": //end
                                maze[y][x] = 3;  //encode end
                                break;
                            default:    //door is default because it is represented by different groups of characters for different door types in txt file
                                switch (cell.substring(cell.length() - 1)) {    //final letter designates door in
                                    case "T":
                                        maze[y][x] = 6;
                                        break;
                                    case "B":
                                        maze[y][x] = 6;
                                        break;
                                    case "R":
                                        maze[y][x] = 7;
                                        break;
                                    case "L":
                                        maze[y][x] = 7;
                                        break;
                                }
                                break;
                        }
                        index = end;    //move the current index to the end of the cell just encoded
                    }
                }
                maze[y][x] = 1;  //add wall at the end of each row since this is not encoded naturally
            }
            //stop BufferedReader
        }

        //creatures[13][29] = new Creature("Player", 50, 13, 10);  //create player Character at start in creatArray
        //set player coordinates
        //px = 29;
        //py = 13;
        //creatures[3][13] = new Creature("Goblin", 20, 11, 10, true);
        //creatCount++;
        //creatures[3][13].setSelect(true);
        addRelics();    //add relicCount to maze array at their coordinates
        clearVis(false); //set vision array values to unknown for the whole maze
        checkVisibility();  //look around the player and print maze with visible cells
        display();
        //displayAll();//
    }

    public void gameLoop() {
        String in;
        //boolean attacking = false;
        while (maze[py][px] != 3 || relicCount < 3) {  //run program until the cell at the player's coordinates is the "end" and they have gotten at least 3 relics
            //loop to ensure the in is valid
            in = null;
            while (true) {
                in = input.nextLine();   //user input for in
                if (in != null) {
                    if (in.length() > 0)
                        in = in.substring(in.length() - 1);
                    if (in.equals("e")) {
                        creatures[py][px].switchWeapon();
                        break;
                    }
                    if (in.equals("q")) {
                        return;
                    }
                    if (in.equals("f"))
                        help = !help;

                    //make visCreatArr
                    //if (!attacking) {
                    if (in.equals("w") || in.equals("a") || in.equals("s") || in.equals("d")) {
                        move(px, py, in, 2);
                        checkVisibility();
                        break;
                    }
                    if (in.equals("W") || in.equals("A") || in.equals("S") || in.equals("D")) {
                        move(px, py, in, 1);
                        checkVisibility();
                        break;
                    }
                    /*} else {
                        if (in.equals("d")) {
                            nextVisCreat();
                            //move down in list of target creatures
                            break;
                        }
                        if (in.equals("a")) {
                            prevVisCreat();
                            //move up in list of target creatures
                            break;
                        }
                    }*/
                    if (in.equals(" ") && visCreatures > 0) {
                        //attacking = !attacking;
                        target();
                        //attack
                        break;
                    }

                    display();
                }
            }
            display();
        }
        for (int i = 0; i < 25; i++)
            System.out.println();
        System.out.println("                              You Win!                              ");
        for (int i = 0; i < 25; i++)
            System.out.println();
    }

    //Add relicCount to maze array at each relic's coordinates
    private void addRelics() {
        int x, y;
        for (int r = 0; r < RELICS.length; r++) {   //loop through rows of relic array
            x = RELICS[r][0];   //x-value of new relic
            y = RELICS[r][1];   //y-value of new relic
            maze[y][x] = 5; //encode relic into maze
            creatures[y][x] = null; //kill any creatures that spawned on a relic
        }
    }

    private void move(int x1, int y1, String direction, int dist) {  //coordinates of moving creature, in of movement, maximum movement distance
        //direction: 1 = up, 2 = right, 3 = down, 4 = left
        Creature c = creatures[y1][x1]; //create creature copy of creature at given x coordinates
        /*
        int x = c.getX();
        int y = c.getY();
        int x1 = xIn;
        int y1 = yIn;
         */
        int x2, y2; //coordinates of cell creature c is moving into
        boolean player = x1 == px && y1 == py; //creature c is the player if the input coordinates are the same as the player's coordinates

        //loop through spaces up to maximum movement, testing if a wall is hit
        for (int d = 0; d < dist; d++) {
            //set future coordinates to current coordinates (for now)
            x2 = x1;
            y2 = y1;

            //test if a wall blocks movement
            if ((direction.equals("w") || direction.equals("W")) && maze[y1 - 1][x1] != 1) {   //can move up?
                y2--;    //move up
            } else if ((direction.equals("s") || direction.equals("S")) && maze[y1 + 1][x1] != 1) {    //can move down?
                y2++;    //move down
            } else if ((direction.equals("d") || direction.equals("D")) && maze[y1][x1 + 1] != 1) {    //can move right?
                x2++;    //move right
            } else if ((direction.equals("a") || direction.equals("A")) && maze[y1][x1 - 1] != 1) {    //can move left?
                x2--;    //move left
            } else {    //cannot move?
                return;   //leave move method
            }
            if (creatures[y2][x2] != null) //other creature is in the way?
            {
                return; //leave move method (can't move past creature)
            }
            //move creature c
            creatures[y1][x1] = null;   //delete creature from current place in creature array
            creatures[y2][x2] = c;  //add creature to new place in creature array

            //set player's new coordinates if creature c is the player
            if (player) {
                px = x2;
                py = y2;
            }

            //obtaining a relic
            if (maze[py][px] == 5) {    //player is on a relic?
                maze[py][px] = 0;   //set player location to empty space
                relicCount++;   //increase player's number of relicCount
            }

            //set current coordinates to new coordinates (for loop to try to move more spaces)
            x1 = x2;
            y1 = y2;
        }
    }

    private void display() {
        int cellInt, relicsLeft = 0;
        if (relicCount < RELICS_REQUIRED) {
            relicsLeft = RELICS_REQUIRED - relicCount;
        }
        System.out.println("\n");
        for (int i = 0; i < MAP_SIZE; i++) {
            System.out.print("- ");
        }
        System.out.println("\n\n\n\n");

        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                cellInt = visArray[y][x];
                Creature c = creatures[y][x];
                if (c == null || cellInt == 4 || !c.getVisible()) {
                    //cellInt = maze[y][x]; //if no vision, only map matters
                    switch (cellInt) {
                        case 0:
                            System.out.print(" ");
                            break;
                        case 1:
                            System.out.print("O");
                            break;
                        case 2:
                            System.out.print("S");
                            break;
                        case 3:
                            System.out.print("E");
                            break;
                        case 4:
                            System.out.print("*");
                            break;
                        case 5:
                            System.out.print("@");
                            break;
                        case 6:
                            System.out.print("-");
                            break;
                        case 7:
                            System.out.print("|");
                            break;
                    }
                } else {
                    System.out.print(c.getType().substring(0, 1));
                }
                if (c != null && c.getSelect()) {
                    System.out.print(")");
                } else if (x < MAP_SIZE - 1 && creatures[y][x + 1] != null && creatures[y][x + 1].getSelect()) {
                    System.out.print("(");
                } else {
                    System.out.print(" ");
                }
            }

            System.out.print("\t");
            if (y == 0) {
                System.out.print("--------------------");
            } else if (y == 1) {
                System.out.print("Relics Found: " + relicCount);
            } else if (y == 2) {
                System.out.print("Relics Needed: " + relicsLeft);
            } else if (y == 4) {
                System.out.print("Weapon: ");
                if (creatures[py][px].getMelee()) {
                    System.out.print("Sword");
                } else {
                    System.out.print("Bow");
                }
            } else if (y == 6) {
                System.out.print("Visible Creatures: " + visCreatures);
            } else if (y == 7) {
                System.out.print("--------------------");
            } else if (help) {
                switch (y) {
                    case 9:
                        System.out.print("CONTROLS");
                        break;
                    case 10:
                        System.out.print("--------");
                        break;
                    case 11:
                        System.out.print("Hit enter after each command");
                        break;
                    case 12:
                        System.out.print("The last character you type on a line is what is accepted");
                        break;
                    case 14:
                        System.out.print("w = 2 forward");
                        break;
                    case 15:
                        System.out.print("a = 2 left");
                        break;
                    case 16:
                        System.out.print("s = 2 down");
                        break;
                    case 17:
                        System.out.print("d = 2 right");
                        break;
                    case 18:
                        System.out.print("Capitalize above letters to move only 1 in that direction");
                        break;
                    case 20:
                        System.out.print("/space/ = attack (or target creature if multiple creatures visible)");
                        break;
                    case 21:
                        System.out.print("a/d = change target back/forth");
                        break;
                    case 22:
                        System.out.print("e = switch weapon");
                        break;
                    case 23:
                        System.out.print("f = toggle help");
                        break;
                    case 24:
                        System.out.print("q = quit");
                        break;
                    case 27:
                        System.out.print("SYMBOLS");
                        break;
                    case 28:
                        System.out.print("-------");
                        break;
                    case 29:
                        System.out.print("P = player");
                        break;
                    case 30:
                        System.out.print("O = wall");
                        break;
                    case 31:
                        System.out.print("* = unknown");
                        break;
                    case 32:
                        System.out.print("S = start");
                        break;
                    case 33:
                        System.out.print("X = creature");
                        break;
                    case 34:
                        System.out.print("(X) = selected creature");
                        break;
                    case 35:
                        System.out.print("| and - = door");
                        break;
                    case 36:
                        System.out.print("@ = relic");
                        break;
                    case 37:
                        System.out.print("E = end");
                        break;
                    case 40:
                        System.out.print("RULES");
                        break;
                    case 41:
                        System.out.print("-----");
                        break;
                    case 42:
                        System.out.print("You must be within 1 space of a creature to use your sword");
                        break;
                    case 43:
                        System.out.print("You must be more than 1 space away from a creature to use your bow");
                        break;
                    case 44:
                        System.out.print("You must acquire (move into) at least 3/5 relics and then reach the end (E) to win");
                        break;
                }
            }
            System.out.println();
        }
    }

    /*public void checkVisibility() {
        final int divisions = 5;    //number of divisions per eight of circle (40 total)
        double slope;
        boolean xRun = true;
        boolean blocked;
        int runFact = 1, riseFact = 1, tempFact;
        int run, rise;
        int toggle;
        int cellInt;
        int xLook, yLook;
        
        //clearVis();
    
        for (int i = 0; i < 8; i++) {
            toggle = i % 2;
            for (int loop = toggle; loop < toggle + divisions - 1; loop++) {
                //for (int loop = 0; loop < divisions - 1; loop++) {
                slope = loop / (double) divisions;
                blocked = false;
                for (run = 1; !blocked; run++) {
                    rise = (int) (run * slope + 0.5) * riseFact;

                    if (xRun) {
                        xLook = px + run * runFact;
                        yLook = py + rise;
                    } else {
                        xLook = px + rise;
                        yLook = py + run * runFact;
                    }
                    System.out.println(xLook + ", " + yLook);

                    cellInt = mazeArray[yLook][xLook];
                    if (creatArray[yLook][xLook] != null) {
                        blocked = true;
                    } else {
                        visArray[yLook][xLook] = cellInt;
                        if (cellInt != 1 && cellInt != 2) {
                            blocked = true;
                        }
                    }
                }
            }

            if (toggle == 0) {
                tempFact = runFact;
                runFact = riseFact;
                riseFact = tempFact;
            } else {
                xRun = !xRun;
            }

        }

        display(true);
    }
     */
    //Apply vision check for each eight of the coordinate plane and call the display method
    private void checkVisibility() {
        clearVis(MEMORY);
        visCheckHall(0, 1);
        visCheckHall(1, 0);
        visCheckHall(0, -1);
        visCheckHall(-1, 0);
        visCheckAngle(1, -1, true);
        visCheckAngle(1, -1, false);
        visCheckAngle(-1, -1, false);
        visCheckAngle(-1, -1, true);
        visCheckAngle(-1, 1, true);
        visCheckAngle(-1, 1, false);
        visCheckAngle(1, 1, false);
        visCheckAngle(1, 1, true);
        visArray[py][px] = maze[py][px];
    }

    private void visCheckHall(int xChange, int yChange) {
        boolean blocked = false;
        int xLook, yLook, cellInt;
        for (int i = 1; !blocked; i++) {
            xLook = px + i * xChange;
            yLook = py + i * yChange;
            cellInt = maze[yLook][xLook];
            if (cellInt == 1 || cellInt > 5 || creatures[yLook][xLook] != null) {//            
                blocked = true;
            }
            checkVisCreat(xLook, yLook);

            xLook += yChange;
            yLook += xChange;
            visArray[yLook][xLook] = maze[yLook][xLook];

            xLook -= 2 * yChange;
            yLook -= 2 * xChange;
            visArray[yLook][xLook] = maze[yLook][xLook];
        }
    }

    private void visCheckAngle(int xFact, int yFact, boolean xRun) {
        double slope;
        int run, rise;
        //streakStart, riseStreak = -1
        int xLook, yLook;
        int cellInt, fill;
        boolean blocked;

        //make sure each eighth of coordinate grid has correct number of lines
        if (xRun ^ xFact == yFact) {
            fill = 0;
        } else {
            fill = 1;
        }

        for (int angle = fill; angle < DIVISIONS + fill; angle++) {
            slope = angle / (double) DIVISIONS;
            blocked = false;
            //streakStart = 1;
            for (run = 1; !blocked; run++) {
                rise = (int) (run * slope + 0.5);
                if (xRun) {
                    xLook = px + run * xFact;
                    yLook = py + rise * yFact;
                } else {
                    xLook = px + rise * xFact;
                    yLook = py + run * yFact;
                }

                cellInt = maze[yLook][xLook];
                visArray[yLook][xLook] = cellInt;
                if (cellInt == 1 || cellInt > 5 || creatures[yLook][xLook] != null) {// 
                    blocked = true;

                }

                checkVisCreat(xLook, yLook);
                /*
                if (creatures[yLook][xLook] != null) {
                    visArray[yLook][xLook] = 7;
                    blocked = true;
                } else {
                    visArray[yLook][xLook] = cellInt;
                    if (cellInt == 1 || cellInt == 2)
                        blocked = true;
                }*/
            }
            /*
            if (rise != riseStreak) {
                if (riseStreak != -1) {
                    for (int r = streakStart; r < run - 1; r++) {
                        if (xRun) {
                            //visArray[riseStreak][r] = maze[riseStreak][r];
                        } else {
                            //visArray[r][riseStreak] = maze[r][riseStreak];
                        }
                    }
                }
                streakStart = run;
            } else
                riseStreak = rise;
             */
        }
    }

    //loop through the vision array and make all cells unknown
    private void clearVis(boolean memory) {
        visCreatures = 0;
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                if (creatures[y][x] != null && (y != py || x != px)) {
                    creatures[y][x].setVisible(false);
                    creatures[y][x].setDist(0);
                }
                if (!memory) {
                    visArray[y][x] = 4; //encode unknown in vision array
                } else if (creatures[y][x] != null && visArray[y][x] != 4 && !creatures[y][x].getVisible()) {
                    visArray[y][x] = maze[y][x];
                }
            }
        }
    }

    private void checkVisCreat(int x, int y) {
        if (creatures[y][x] != null && creatures[y][x].getDist() == 0) {
            creatures[y][x].setVisible(true);
            creatures[y][x].setDist(px - y, py - x);
            visCreatures++;
        }
    }

    private void target() {
        String in;
        int count = 0;
        creatSelected = visCreatures - 1;
        visCreatArr = new int[visCreatures][2];
        for (int y = 0; y < MAP_SIZE && count < visCreatures; y++) {
            for (int x = 0; x < MAP_SIZE && count < visCreatures; x++) {
                if (creatures[y][x] != null && creatures[y][x].getVisible() && (px != x || py != y)) {
                    visCreatArr[count][0] = y;
                    visCreatArr[count][1] = x;
                    count++;
                }
            }
        }
        //TODO: sort by closeness to player

        //System.out.println(creatures[visCreatArr[creatSelected][0]][visCreatArr[creatSelected][1]].getHP());
        if (visCreatures > 1) {
            //creatures[visCreatArr[0][0]][visCreatArr[0][1]].setSelect(true);

            if (select(1)) {
                display();
                //loop to ensure the in is valid
                while (true) {
                    in = input.nextLine();   //user input for in
                    if (in != null && !in.equals("")) {
                        in = in.substring(in.length() - 1);

                        if (in.equals("d")) {
                            select(1); //move down in list of target creatures
                        }
                        if (in.equals("a")) {
                            select(-1); //move up in list of target creatures
                        }

                        if (in.equals(" ")) {
                            //if ((creatures[py][px].getMelee() && creatures[visCreatArr[creatSelected][0]][visCreatArr[creatSelected][1]].getDist() < 2) || (!creatures[py][px].getMelee() && creatures[visCreatArr[creatSelected][0]][visCreatArr[creatSelected][1]].getDist() >= 2))
                            if ((creatures[py][px].getMelee() && (Math.abs(visCreatArr[creatSelected][0] - py) <= 1 && Math.abs(visCreatArr[creatSelected][1] - px) <= 1)) || (!creatures[py][px].getMelee() && (Math.abs(visCreatArr[creatSelected][0] - py) > 1 || Math.abs(visCreatArr[creatSelected][1] - px) > 1))) {
                                attack(visCreatArr[creatSelected][0], visCreatArr[creatSelected][1], 10, 8, 2);
                            }
                            break;
                        }
                        if (in.equals("e")) {
                            creatures[py][px].switchWeapon();
                            creatures[visCreatArr[creatSelected][0]][visCreatArr[creatSelected][1]].setSelect(false);
                            target();
                            return;
                        }
                        if (in.equals("q")) {
                            break;
                        }
                        display();
                    }
                }
                if (creatures[visCreatArr[creatSelected][0]][visCreatArr[creatSelected][1]] != null) {
                    creatures[visCreatArr[creatSelected][0]][visCreatArr[creatSelected][1]].setSelect(false);
                    System.out.println("removing select");
                    System.out.println("x: " + visCreatArr[creatSelected][1]);
                    System.out.println("y: " + visCreatArr[creatSelected][0]);
                }
            } else
                display();
        } else {
            if ((creatures[py][px].getMelee() && (Math.abs(visCreatArr[creatSelected][0] - py) <= 1 && Math.abs(visCreatArr[creatSelected][1] - px) <= 1)) || (!creatures[py][px].getMelee() && (Math.abs(visCreatArr[creatSelected][0] - py) > 1 || Math.abs(visCreatArr[creatSelected][1] - px) > 1))) {
                attack(visCreatArr[creatSelected][0], visCreatArr[creatSelected][1], 10, 8, 2);
            }
            //System.out.println(creatures[py][px].getMelee());
            //System.out.println(creatures[visCreatArr[creatSelected][0]][visCreatArr[creatSelected][1]].getDist());
            //System.out.println(creatures[visCreatArr[creatSelected][0]][visCreatArr[creatSelected][1]].getHP());
        }
        //TODO: attack creature
        //attack(visCreatArr[creatSelected][0], visCreatArr[creatSelected][1]);
    }

    private boolean select(int inc) {
        int i;
        creatures[visCreatArr[creatSelected][0]][visCreatArr[creatSelected][1]].setSelect(false);
        for (i = 0; i < visCreatures; i++) {
            System.out.println("creat before: " + creatSelected);
            creatSelected += inc;
            if (creatSelected < 0) {
                creatSelected += visCreatures;
            }
            creatSelected %= visCreatures;
            System.out.println(creatSelected);
            if ((creatures[py][px].getMelee() && Math.abs(visCreatArr[creatSelected][0] - py) <= 1 && Math.abs(visCreatArr[creatSelected][1] - px) <= 1) || (!creatures[py][px].getMelee() && (Math.abs(visCreatArr[creatSelected][0] - py) > 1 || Math.abs(visCreatArr[creatSelected][1] - px) > 1)))
                break;
        }
        System.out.println("creat after: " + creatSelected + "\n");
        if (i < visCreatures) {
            creatures[visCreatArr[creatSelected][0]][visCreatArr[creatSelected][1]].setSelect(true);
        } else
            return false;
        return true;
    }

    private void attack(int y, int x, int ATK, int dam, int damVar) {
        if (ATK + 1 + r.nextInt(10) >= creatures[y][x].getAC()) {
            creatures[y][x].loseHP(dam + r.nextInt(damVar) + 1);
        }
        if (creatures[y][x].getHP() <= 0) {
            creatures[y][x] = null;
            visCreatures--;
            checkVisibility();
        }
    }
    /*
    Accessors
    
    //return player's x-value
    public int getPX() {
        return px;
    }

    //return player's y-value
    public int getPY() {
        return py;
    }

    //return the number representing the cell at given coordinates
    public int getCell(int x, int y) {
        return maze[y][x];
    }

    //return the number of relics the player has gotten
    public int getRelicCount() {
        return relicCount;
    }
     */
}
