package spreadsheet;

import common.api.CellLocation;
import common.api.Tabular;
import common.api.value.LoopValue;
import common.api.value.StringValue;
import common.api.value.Value;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Spreadsheet implements Tabular {

  private Map<CellLocation, Cell> cellMap;
  private Set<Cell> invalidCells;

  public Spreadsheet() {
    this.cellMap = new HashMap<>();
    this.invalidCells = new LinkedHashSet<>();
  }

  @Override
  public void setExpression(CellLocation location, String expression) {
    if(cellMap.containsKey(location)) {
      Cell cell = cellMap.get(location);
      cell.setExpression(expression);
      cell.setValue(new StringValue(expression));
    } else {
      cellMap.put(location, new Cell(this, expression, new StringValue(expression)));
    }
  }

  @Override
  public Value getValue(CellLocation location) {
    if(cellMap.containsKey(location)) {
      return cellMap.get(location).getValue();
    }
    return new StringValue("");
  }

  @Override
  public void recompute() {
    Set<Cell> cellsToIterate = invalidCells.stream().collect(Collectors.toSet());
    cellsToIterate.forEach(cell -> recomputeCell(cell));
    invalidCells.clear();
  }

  @Override
  public String getExpression(CellLocation location) {
    if(cellMap.containsKey(location)) {
      return cellMap.get(location).getExpression();
    }
    return "";
  }

  protected Cell getCell(CellLocation location) {
    if(cellMap.containsKey(location)) {
      return cellMap.get(location);
    }
    Cell c = new Cell(this);
    cellMap.put(location, c);
    return c;
  }


  protected boolean shouldBeRecomputed(Cell cell) {
    return invalidCells.contains(cell);
  }

  protected void addInvalidCell(Cell cell) {
    invalidCells.add(cell);
  }

  private void recomputeCell(Cell cell) {
    checkForLoops(cell, new LinkedHashSet<>());
  }

  private void checkForLoops(Cell c, LinkedHashSet<Cell> cellsSeen) {
    if(cellsSeen.contains(c)) {
      markAsValidatedLoop(c, cellsSeen);
      return;
    }

    cellsSeen.add(c);
    c.getReferencedCells().forEach(referencedCell -> checkForLoops(referencedCell, cellsSeen));
    cellsSeen.remove(c);
  }

  private void markAsValidatedLoop(Cell startCell, LinkedHashSet<Cell> cells) {
    Iterator<Cell> i = cells.iterator();
    while (i.hasNext()) {
      Cell c = i.next();
      invalidCells.remove(c);
      if(c.equals(startCell)) {
        c.setValue(LoopValue.INSTANCE);
        i.forEachRemaining(
            cell -> {
              invalidCells.remove(cell);
              cell.setValue(LoopValue.INSTANCE);
            });
      }
    }
  }
}
