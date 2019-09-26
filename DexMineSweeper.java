import java.util.Scanner;
import java.util.Random;

//TODO: validation for input (integer) when selecting cell

public class DexMineSweeper {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        int dimensions, difficulty=0;
        
        System.out.println("Symbols: ");
        System.out.printf("* = Bomb\n%%d = No. of surrounding bombs\n- = Untested cell\nX - Exploded mine\nM - Defused mine\nD - Deactivated EMPTY field\n\n");
        System.out.println("Rules: ");
        System.out.println("1. Each cell location is either empty or contains a mine.");
        System.out.println("2. Revealing a location tells you how many mines are in its proximity (up down left right).");
        System.out.println("3. Deactivating a location defuses the mine, and your goal is to deactivate all mines.");
        System.out.println("4. However, revealing a mine or deactivating THREE empty fields will cause the game to end.");
        System.out.println("");
        
        //User input - dimensions
        System.out.print("Enter dimension of square playing area (i.e. '5' for 5x5 board with a LIMIT OF 26): ");
        dimensions = sc.nextInt();
        while (!(dimensions >=1 && dimensions <=26)){
            System.out.printf("Game currently supports only up to 26x26 grids, so max location is Z26. Try again: ");
            dimensions = sc.nextInt();
        }
        
        //User input - difficulty
        System.out.print("Enter difficulty (1-5, with 1 being the hardest): ");
        difficulty = sc.nextInt();
        while (!(difficulty >=1 && difficulty <=5)){
            System.out.print("Enter a valid difficulty between 1 and 5: ");
            difficulty = sc.nextInt();
        }
        
        System.out.println("");
        
        char[][] board = new char[dimensions][dimensions];
        boolean[][] play = new boolean[dimensions][dimensions];
        //play is used to determine which cell values to display and which cell values to hide 
        //(in other words, which cells the user has revealed/deactivated)
        int endResult=0; 
        //end result valuations:
        //0 = ongoing, -1 = loss due to revealed mine, -2 = loss due to three deactivated empty fields, 1 = victory
        int mines, deactivatedMines=0;
        
        //board is created and total number of mines in the board is assigned to variable mines
        //variable is later used to determine when all mines have been deactivated i.e. victory has been attained
        mines = createBoard(board, play, difficulty);
        showFullBoard(board); //use this to test the game, it shows all the positions of each mine
        //showBoard(board, play); //decomment this when you want to show only the board with hidden mines
        
        while (endResult >= 0){
            endResult = selectCell(board, play, true, 0, 0);
            deactivatedMines += endResult; 
            //since endresult returns 0 upon revealing a cell and 1 upon successful deactivation
            //adding endresult to deactivatedMines ensures the variable will hold the total number of deactivated mines while the user is playing
            //eventually, the while loop is broken when all mines have been deactivated, like so:
            if (deactivatedMines == mines){
                endResult = 1;
                break;
            }
        }
        
        switch (endResult) {
            case -2:
                System.out.printf("\nYou deactivated too many empty fields! Thanks for playing!\n\n");
                break;                
            case -1:
                System.out.printf("\nYou revealed a mine! Thanks for playing!\n\n");
                break;
            case 1:
                System.out.printf("\nYou have won. Congrats!\n\n");
                break;
        }
        System.out.println("FULL BOARD:");
        showFullBoard(board);

    }
    
    public static int createBoard(char[][] board, boolean[][] playBoard, int difficulty){
        int i, j, totalMines=0;
        Random rd = new Random();
        /*
        Chance of a mine appearing in specific cell based on difficulty:
        Based on rd.nextInt(difficulty+1)==0
        Difficulty       Chance (%)
            1               50
            2               33.3
            3               25
            4               20
            5               16.7
        */
        for (i=0; i<board.length; i++){
            for (j=0; j<board[0].length; j++){
                board[i][j] = rd.nextInt(difficulty+1)==0 ? '*' : '.';
                if (board[i][j] == '*'){totalMines++;}
                playBoard[i][j] = false;
            }
        }
        
        return totalMines;
    }
    
    public static void showBoard(char[][] board, boolean[][] playBoard){
        int i, j;
        
        //setup board layout
        System.out.print("  ");
        if (board.length > 9){System.out.printf(" ");}
        for (i=0; i<board[0].length; i++){
            System.out.printf(" %c ", i+65);
        }
        System.out.println("");
        
        for (i=0; i<board.length; i++){
            System.out.printf("%d ", i+1);
            if (i < 9 && board.length > 9){System.out.printf(" ");}
            for (j=0; j<board[0].length; j++){
                if (!playBoard[i][j]){
                    System.out.print(" - ");
                } else {
                    System.out.print(" " + board[i][j]+ " ");
                }
            }
            System.out.println("");
        }
    }
    
    public static void showFullBoard(char[][] board){
        int i, j;
        
        //setup board layout
        System.out.print("  ");
        if (board.length > 9){System.out.printf(" ");}
        for (i=0; i<board[0].length; i++){
            System.out.printf(" %c ", i+65);
        }
        System.out.println("");
        
        for (i=0; i<board.length; i++){
            System.out.printf("%d ", i+1);
            if (i < 9 && board.length > 9){System.out.printf(" ");}
            for (j=0; j<board[0].length; j++){
                System.out.print(" " + board[i][j] + " ");
            }
            System.out.println("");
        }    
    }
    
    public static int selectCell(char[][] board, boolean[][] playBoard, boolean promptInput, int xAutoInput, int yAutoInput){
        Scanner sc = new Scanner(System.in);
        int xCell=0, yCell=0, testCellResult, mineAdd=0, failedDeactivation=0, i, j;
        String input="";        
        boolean validInput=false;
        //some validation for cell location
        
        //promptInput is used to detect whether selectCell was called recursively or from the main function
        //true means it was called from main and will prompt user for input
        //xautoinput and yautoinput are only needed when promptInput is false (its used in place of xCell and yCell
        if (promptInput){
            while (!validInput){
                showBoard(board, playBoard);  
                System.out.printf("\nSelect a cell location to reveal (r) or deactivate (d) (i.e. 'r B3' or 'd B4'): ");
                input = sc.nextLine().toUpperCase();
                System.out.println("");
                if ((input.charAt(0) == 68 || input.charAt(0) == 82) && input.charAt(1) == 32){ 
                    //makes sure first character is r or d, and second character is a white space
                    if (input.charAt(2) >= 65 && input.charAt(2) <= 90) { //simple validation
                        //separate validation for input depending on whether it has 4 or 5 characters i.e. 'r B4' or 'd B25'
                        //essentially ensures that input is within the range (A-Z)(1-26)
                        
                        if (input.length() == 4) {
                            if (input.charAt(3) >=49 && input.charAt(3) <=57) {
                                xCell = Integer.parseInt(input.substring(3)); //digits 1-9 have ASCII values 49-57
                                yCell = input.charAt(2)-64; //uppercase letters have ASCII values 65-90
                                if (xCell <= board.length && yCell <= board[0].length){
                                    validInput = true;
                                    continue;   
                                }
                            }
                        }
                        
                        if (input.length() == 5) {
                            if (input.charAt(3) >=49 && input.charAt(3) <=50 && input.charAt(4) >= 48 && input.charAt(4) <= 57) {
                                xCell = Integer.parseInt(input.substring(3)); //digits 1-9 have ASCII values 49-57
                                yCell = input.charAt(2)-64; //uppercase letters have ASCII values 65-90
                                if (xCell <= board.length && yCell <= board[0].length){
                                    validInput = true;
                                    continue;   
                                }
                            }
                        }
                    }
                } 
                System.out.printf("Ensure that command is in a valid format e.g. 'r B4' or 'd A3' and within board range!\n");
            }            

            testCellResult = input.charAt(0) == 'R' ? testCell(board, xCell, yCell) : defuseCell(board, xCell, yCell);   
        } else {
            //set xCell and yCell to be the new neighboring cell location (xAutoInput/yAutoInput) so it can then be called recursively again
            xCell = xAutoInput; 
            yCell = yAutoInput;
            testCellResult = testCell(board, xCell, yCell);
        }

        if (playBoard[xCell-1][yCell-1] == true){
            System.out.printf("Cell has already been activated! Try another cell.\n\n");
        } else {
            //set board cell state to number of neighboring mines or deactivated mine symbol 'M'
            if (!promptInput || input.charAt(0) == 'R'){


                if (testCellResult == -1){ //-1 returned means testCell revealed a mine
                    showFullBoard(board);
                    return -1;
                } 


                board[xCell-1][yCell-1] = Integer.toString(testCellResult).charAt(0);
                playBoard[xCell-1][yCell-1] = true;

                if (testCellResult == 0){
                    //If revealed cell has no mines in its proximity, reveal all neighboring cells
                    //This is done with a nested for loop that runs through a 3x3 grid with the original revealed cell as its middle cell
                    //The middle cell is of course excluded
                    //Neighboring cells are passed to testCell only if it has not been revealed (playBoard = false)
                    for (i=xCell-1; i<=xCell+1; i++){
                        for (j=yCell-1; j<=yCell+1; j++){
                            //makes sure that whatever board cell is tested remains within array boundaries
                            if (i > 0 && i <= board.length && j > 0 && j <= board[0].length){
                                //if it is not the middle cell being tested
                                if (!(i == xCell && j == yCell)){
                                    if (playBoard[i-1][j-1] == false){
                                        selectCell(board, playBoard, false, i, j);
                                    } 
                                }

                            }
                        }
                    }
                } 
            } else {
                playBoard[xCell-1][yCell-1] = true;

                if (testCellResult == 1) {
                    board[xCell-1][yCell-1] = 'D';
                    //diffusecell returns 1 when an empty field has been deactivated
                    //faileddeactivation keeps track of how many times an empty field has been deactivated
                    //once it hits 3, the user loses the game
                    for (i=0; i<board.length; i++){
                        for (j=0; j<board.length; j++){
                            if (board[i][j] == 'D'){
                                failedDeactivation++;
                            }
                        }
                    }
                    if (failedDeactivation == 3){
                        return -2;
                    }
                } else {
                    board[xCell-1][yCell-1] = 'M';
                    mineAdd++;   
                }
            }
        }
        
        //value either returns mineAdd=0 or mineAdd=1
        //1 is returned when a mine has been sucessfully deactivated and an M appears on the board
        return mineAdd;
    }
    
    public static int defuseCell(char[][] board, int xDim, int yDim){
        if (board[xDim-1][yDim-1] == '*'){
            return 0;
        } else {
            return 1;
        }
    }
    
    public static int testCell(char[][] board, int xDim, int yDim){
        int count=0;

        if (board[xDim-1][yDim-1] == '*'){ //if cell itself is a mine, terminate
            board[xDim-1][yDim-1] = 'X';
            return -1;
        } else {
            //if cell is not on the uppermost row, check cell above it, and then its left/right
            if (xDim-1>0){
                if (board[xDim-2][yDim-1] == '*' || board[xDim-2][yDim-1] == 'M'){count++;}
                //checks diagonally top left and top right
                if (yDim-1>0){
                    if (board[xDim-2][yDim-2] == '*' || board[xDim-2][yDim-2] == 'M'){count++;}
                }
                if (yDim<board[0].length){
                    if (board[xDim-2][yDim] == '*' || board[xDim-2][yDim] == 'M'){count++;}
                }
            } 
            
            //if cell is not on the bottommost row, check cell below it, and its left/right
            if (xDim<board.length){
                if (board[xDim][yDim-1] == '*' || board[xDim][yDim-1] == 'M'){count++;}
                //checks diagonally bottom left and bottom right
                if (yDim-1>0){
                    if (board[xDim][yDim-2] == '*' || board[xDim][yDim-2] == 'M'){count++;}
                }
                if (yDim<board[0].length){
                    if (board[xDim][yDim] == '*' || board[xDim][yDim] == 'M'){count++;}
                }
            }
            
            //if cell is not on the leftmost column, check cell to its immediate left
            if (yDim-1>0){
                if (board[xDim-1][yDim-2] == '*' || board[xDim-1][yDim-2] == 'M'){count++;}
            }
            //if cell is not on the rightmost column, check cell to its immediate right
            if (yDim<board[0].length){
                if (board[xDim-1][yDim] == '*' || board[xDim-1][yDim] == 'M'){count++;}
            }
        }
        return count;
    }
}
