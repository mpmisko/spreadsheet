package spreadsheet;

import common.api.CellLocation;
import common.api.ExpressionUtils;
import common.api.monitor.Tracker;
import common.api.value.InvalidValue;
import common.api.value.StringValue;
import common.api.value.Value;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Cell implements Tracker<Cell>{

  private final Spreadsheet spreadsheet;
  private final CellLocation location;
  private String expression;
  private Value value;
  private Set<Cell> referencedCells;
  private Set<Tracker<Cell>> cellsReferencedBy;

  public Cell(Spreadsheet spreadsheet, CellLocation location, String expression, Value value) {
    this.spreadsheet = spreadsheet;
    this.location = location;
    this.expression = expression;
    this.value = value;
    this.referencedCells = new HashSet<>();
    this.cellsReferencedBy = new HashSet<>();
  }

  public Cell(Spreadsheet spreadsheet, CellLocation location) {
    this(spreadsheet, location,"", new StringValue(""));
  }

  @Override
  public void update(Cell changed) {
    if(!spreadsheet.shouldBeRecomputed(this)) {
      spreadsheet.addInvalidCell(this);
      setValue(new InvalidValue(expression));
      notifyTrackers();
    }
  }

  public CellLocation getLocation() {
    return location;
  }

  public String getExpression() {
    return expression;
  }

  public Value getValue() {
    return value;
  }

  public void setExpression(String expression) {
    referencedCells.forEach(cell ->  cell.removeTracker(this));
    referencedCells.clear();
    this.expression = expression;
    setValue(new InvalidValue(expression));
    addToInvalids();
    Set<Cell> newCells = ExpressionUtils
        .getReferencedLocations(expression)
        .stream()
        .map(location -> spreadsheet.getCell(location))
        .collect(Collectors.toSet());
    referencedCells = newCells;
    referencedCells.forEach(cell ->  cell.addTracker(this));
    notifyTrackers();
  }

  public void setValue(Value value) {
    this.value = value;
  }

  protected Set<Cell> getReferencedCells() {
    return referencedCells;
  }

  private void addToInvalids() {
    if (!spreadsheet.shouldBeRecomputed(this)) {
      spreadsheet.addInvalidCell(this);
    }
  }

  private void removeTracker(Tracker<Cell> tracker) {
    cellsReferencedBy.remove(tracker);
  }

  private void addTracker(Tracker<Cell> tracker) {
    cellsReferencedBy.add(tracker);
  }

  private void notifyTrackers(){
    for (Tracker<Cell> cell : cellsReferencedBy) {
      cell.update(this);
    }
  }
}
