package spreadsheet;

import common.api.CellLocation;
import common.api.ExpressionUtils;
import common.api.monitor.Tracker;
import common.api.value.InvalidValue;
import common.api.value.StringValue;
import common.api.value.Value;
import java.util.Set;

public class Cell implements Tracker<Cell>{

  private final Spreadsheet spreadsheet;
  private final CellLocation location;
  private String expression;
  private Value value;
  private Set<Cell> referencedCells;
  private Set<Tracker<Cell>> cellsReferencedBy;

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

  @Override
  public void update(Cell changed) {

  }

  public String getExpression() {
    return expression;
  }

  public Value getValue() {
    return value;
  }

  public void setExpression(String expression) {
    for (Cell referencedCell : referencedCells) {
      removeTracker(this);
    }
    referencedCells.clear();
    this.expression = expression;
    setValue(new InvalidValue(expression));
    addToInvalids();
    Set<CellLocation> referencedCells = ExpressionUtils.getReferencedLocations(expression);
    //referencedCells.stream().map(location -> spreadsheet.)
  }

  public void setValue(Value value) {
    this.value = value;
  }


  private void addToInvalids() {
    if (!spreadsheet.shouldBeRecomputed(this)) {
      spreadsheet.addInvalidCell(this);
    }
  }

  private void removeTracker(Tracker<Cell> tracker) {
    cellsReferencedBy.remove(tracker);
  }
}
