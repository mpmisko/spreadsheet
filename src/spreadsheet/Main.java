package spreadsheet;

import common.gui.SpreadsheetGUI;

public class Main {

  private static final int DEFAULT_NUM_ROWS = 5000;
  private static final int DEFAULT_NUM_COLUMNS = 5000;

  public static void main(String[] args) {
    Spreadsheet s = new Spreadsheet();
    SpreadsheetGUI gui = new SpreadsheetGUI(s, DEFAULT_NUM_ROWS, DEFAULT_NUM_COLUMNS);
    if(args.length > 0) {
      int rows = Integer.parseInt(args[0]);
      int columns = Integer.parseInt(args[1]);
      gui = new SpreadsheetGUI(s, rows, columns);
    }
    gui.start();
  }

}
