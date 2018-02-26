package spreadsheet;

import common.api.CellLocation;
import common.api.value.StringValue;
import common.api.value.Value;

public class Cell {

  private final Spreadsheet spreadsheet;
  private final CellLocation location;
  private String expression;
  private Value value;

  public Cell(Spreadsheet spreadsheet, CellLocation location, String expression,
      Value value) {
    this.spreadsheet = spreadsheet;
    this.location = location;
    this.expression = expression;
    this.value = value;
  }

  public Cell(Spreadsheet spreadsheet, CellLocation location) {
    this.spreadsheet = spreadsheet;
    this.location = location;
    expression = "";
    value = new StringValue("");
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public Value getValue() {
    return value;
  }

  public void setValue(Value value) {
    this.value = value;
  }
}
