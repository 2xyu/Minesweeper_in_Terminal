import java.util.*;
import java.util.regex.Pattern;

public class Minesweeper {
    static final String BOMB_RED = "\u001B[48;2;255;0;0m" + "\u001B[30m";
    static final String RESET = "\u001B[0m";
    static final String B1_BLUE = "\u001B[38;2;0;0;255m" + "\u001B[48;2;170;170;170m";
    static final String B2_GREEN = "\u001B[38;2;0;123;0m" + "\u001B[48;2;170;170;170m";
    static final String B3_RED = "\u001B[38;2;255;0;0m" + "\u001B[48;2;170;170;170m";
    static final String B4_DARKBLUE = "\u001B[38;2;0;123;255m" + "\u001B[48;2;170;170;170m";
    static final String B5_DARKRED = "\u001B[38;2;123;0;255m" + "\u001B[48;2;170;170;170m";
    static final String B6_TURQUOISE = "\u001B[38;2;42;148;148m" + "\u001B[48;2;170;170;170m";
    static final String B7_BLACK = "\u001B[38;2;0;0;0" + "\u001B[48;2;170;170;170m";
    static final String B8_GREY = "\u001B[38;2;123;123;123m" + "\u001B[48;2;170;170;170m";
    static final String TEXTHISTORY_DARKGREY = "\u001B[38;2;128;128;128m";

    static byte height;
    static byte width;
    static boolean[][] grid;
    static boolean[][] seen;
    static boolean[][] flagged;
    static int bombs;

    public static void clearTerminal(){

        System.out.print("\033c");
    }


    public static void displayGrid(){
        String outP = "\t ";
        for (byte i = 0; i < width; i++){
            outP += ((char) ('a' + i) + " ");
        }
        outP += "\n\t " + "_".repeat(width * 2 - 1);
        outP += "\n";
        for (byte y = 0; y < height; y++){
            outP += y + 1 + "\t|";
            for (byte x = 0; x < width; x++){

                if (flagged[y][x]){
                    outP += BOMB_RED + "F" + RESET + " ";
                }
                else if (seen[y][x]) {

                    boolean cell = grid[y][x];
                    byte bombs = countBombsAround(x, y);

                    if (cell) {
                        outP += BOMB_RED + "X" + RESET + " ";
                    }
                    else if (bombs == 0){
                        outP += "  ";
                    }
                    else if (!cell) {
                        if (bombs == 1){
                            outP += B1_BLUE + bombs + RESET + " ";
                        }
                        else if (bombs == 2){
                            outP += B2_GREEN + bombs + RESET + " ";
                        }
                        else if (bombs == 3){
                            outP += B3_RED + bombs + RESET + " ";
                        }
                        else if (bombs == 4){
                            outP += B4_DARKBLUE + bombs + RESET + " ";
                        }
                        else if (bombs == 5){
                            outP += B5_DARKRED + bombs + RESET + " ";
                        }
                        else if (bombs == 6){
                            outP += B6_TURQUOISE + bombs + RESET + " ";
                        }
                        else if (bombs == 7){
                            outP += B7_BLACK + bombs + RESET + " ";
                        }
                        else if (bombs == 8){
                            outP += B8_GREY + bombs + RESET + " ";
                        }

                    }
                }

                else {
                    outP += "? ";
                }

            }
            outP = outP.substring(0, outP.length() - 1) + "|\n";
        }
        outP += "\t " + "-".repeat(width * 2 - 1);
        System.out.println(outP);
    }

    public static boolean clickGrid(byte x, byte y){

        if (grid[y][x]){
            for (byte col = 0; col < width; col++){
                for (byte row = 0; row < height; row++){
                    seen[row][col] = true;
                }
            }
            return true;
        }
        else {
            seen[y][x] = true;
            if (countBombsAround(x, y) == 0) {
                for (byte col = (byte) Math.max(0, x - 1); col < Math.min(width, x + 2); col++) {
                    for (byte row = (byte) Math.max(0, y - 1); row < Math.min(height, y + 2); row++) {
                        if (row == y && col == x) {
                            continue;
                        }
                        if (!grid[row][col] && !seen[row][col]) {
                            clickGrid(col, row);
                        }
                    }
                }
            }
        }
        return false;


    }

    public static boolean gameWon(){

        for (byte x = 0; x < width; x++){
            for (byte y = 0; y < height; y++){
                if (grid[y][x] != flagged[y][x]){
                    return false;
                }
            }
        }
        return true;
    }

    public static byte countBombsAround(byte x, byte y) {
        byte count = 0;

        for (byte col = (byte) Math.max(0, x - 1); col < Math.min(width, x + 2); col++){
            for (byte row = (byte) Math.max(0, y - 1); row < Math.min(height, y + 2); row++){
                if (row == y && col == x){
                    continue;
                }
                if (grid[row][col]){
                    count++;
                }
            }
        }
        return count;
    }

    public static void flagGrid(byte x, byte y){

        flagged[y][x] = !flagged[y][x];

    }

    public static void resizeGird(byte x, byte y){
        width = x;
        height = y;
        grid = new boolean[y][x];
        seen = new boolean[y][x];
        flagged = new boolean[y][x];
    }


    public static void addbomb(int b){
        bombs = b;
        short max = (short) (width * height);

        List<Short> temp = new ArrayList<>();


        for (short i = 0; i < max; i++){
            temp.add(i);
        }
        Collections.shuffle(temp);

        for (byte i = 0; i < bombs; i++){
            byte y = (byte) (temp.get(i) % height);
            byte x = (byte) (temp.get(i) / height);


            grid[y][x] = true;
        }
    }


    public static void main(String[] args) {

        boolean lost = false;
        boolean won = false;

        resizeGird((byte) 6, (byte) 6);
        addbomb((short) 3);
        displayGrid();

        Scanner scanner = new Scanner(System.in);
        System.out.print("run " + TEXTHISTORY_DARKGREY + "> help" + RESET + " for help\n\n> ");

        while (scanner.hasNextLine()){
            clearTerminal();
            String action = scanner.nextLine().trim();
            Pattern click = Pattern.compile("^click [a-z]-[1-9][0-9]?", Pattern.CASE_INSENSITIVE);
            Pattern flag = Pattern.compile("^flag [a-z]-[1-9][0-9]?", Pattern.CASE_INSENSITIVE);
            Pattern resize = Pattern.compile("^resize [1-9][0-9]?x[1-9][0-9]?", Pattern.CASE_INSENSITIVE);
            Pattern addbomb = Pattern.compile("^addbomb (?:[0-9]{1,3}|[1-8][0-9]{3}|9[0-7][0-9]{2}|980[0-1])$", Pattern.CASE_INSENSITIVE);


            if (action.equals("")){
                displayGrid();
            }
            else if (action.equalsIgnoreCase("help")){
                displayGrid();
                System.out.print(TEXTHISTORY_DARKGREY +
                        "\t| help  ------------------> Shows help menu.\n" +
                        "\t| reset ------------------> Reset grid. (also randomizes bomb locations).\n" +
                        "\t| addbomb <n> ------------> Places n bombs randomly on grid.\n" +
                        "\t| click <a...z>-<1...99> -> Clicks a cell. (example: click b-5)\n" +
                        "\t| flag <a...z>-<1...99> --> Flags a cell. (example: flag h-2)\n" +
                        "\t| resize <X>x<Y> -> Resizes grid to x by y units. (units can only be 1 to 99) (example: resize 16x57)\n\n" +
                        RESET);
            }
            else if (click.matcher(action).matches()){
                byte x = (byte) (action.split(" ")[1].split("-")[0].charAt(0) - 'a');
                byte y = (byte) (Integer.parseInt(action.split(" ")[1].split("-")[1]) - 1);
                if (x >= width || y >= height){
                    System.out.println("Unable to click " + B3_RED + action.split(" ")[1] + RESET + " error: out of bounds to the grid.");
                    continue;
                }
                lost = clickGrid(x, y);
                won = gameWon();
                displayGrid();

                if (!lost){
                    System.out.println(TEXTHISTORY_DARKGREY + "\t> " + action + "\n" +
                            "\t| <clicked " + action.split(" ")[1] + ">" + RESET + "\n");
                }

            }
            else if (flag.matcher(action).matches()){
                byte x = (byte) (action.split(" ")[1].split("-")[0].charAt(0) - 'a');
                byte y = (byte) (Integer.parseInt(action.split(" ")[1].split("-")[1]) - 1);

                if (x >= width || y >= height){
                    System.out.println("Unable to flag " + B3_RED + action.split(" ")[1] + RESET + " error: out of bounds to the grid.");
                    continue;
                }
                flagGrid(x, y);
                won = gameWon();
                displayGrid();
                System.out.println(TEXTHISTORY_DARKGREY + "\t> " + action + "\n" +
                        "\t| <flagged " + action.split(" ")[1] + ">" + RESET + "\n");

            }
            else if (resize.matcher(action).matches()){
                byte x = (byte) Integer.parseInt(action.split(" ")[1].split("x")[0]);
                byte y = (byte) Integer.parseInt(action.split(" ")[1].split("x")[1]);

                if (x > 26 || y > 99){
                    System.out.println("Unable to resize to " + B3_RED + action.split(" ")[1] + RESET + " error: unit bigger than 99.");
                    continue;
                }
                resizeGird(x ,y);
                displayGrid();
                System.out.println(TEXTHISTORY_DARKGREY + "\t> " + action + "\n" +
                        "\t| <resized grid to " +  action.split(" ")[1] + ">" + RESET + "\n");
            }
            else if (addbomb.matcher(action).matches()){
                int bombs = (byte) Integer.parseInt(action.split(" ")[1]);

                if (bombs > width * height){
                    System.out.println("Unable to add " + B3_RED + action.split(" ")[1] + RESET + " error: more bombs than the grid can fit.");
                    continue;
                }

                resizeGird(width, height);
                addbomb(bombs);
                displayGrid();
                System.out.println(TEXTHISTORY_DARKGREY + "\t> " + action + "\n" +
                        "\t| <added " +  action.split(" ")[1] + " bombs>" + RESET + "\n");
            }

            if (won && !lost){
                won = false;
                System.out.println(B2_GREEN + "YOU WIN!!!\n" + RESET +
                        "Resetting grid now.\n" +
                        "Press any key to continue.\n");
                scanner.nextLine();
                clearTerminal();
                resizeGird(width, height);
                addbomb(bombs);
                displayGrid();
            }
            else if (lost){
                lost = false;
                System.out.println(TEXTHISTORY_DARKGREY + "\t> " + action + "\n" +
                        "\t| " + "\u001B[38;2;255;0;0m" + "BOOM!!! GAME OVER." + RESET + "\n" +
                        "Resetting grid now.\n" +
                        "Press any key to continue.\n");
                scanner.nextLine();
                clearTerminal();
                resizeGird(width, height);
                addbomb(bombs);
                displayGrid();
            }

            System.out.print("run " + TEXTHISTORY_DARKGREY + "> help" + RESET + " for help\n\n> ");
        }
    }
}