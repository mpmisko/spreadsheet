package spreadsheet;

import common.api.CellLocation;
import common.api.ExpressionUtils;
import common.api.Tabular;
import common.api.value.DoubleValue;
import common.api.value.InvalidValue;
import common.api.value.LoopValue;
import common.api.value.StringValue;
import common.api.value.Value;
import common.api.value.ValueEvaluator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
      cellMap.put(location, new Cell(this, location, expression, new StringValue(expression)));
    }
  }

  @Override
  public Value getValue(CellLocation location) {
    if(cellMap.containsKey(location)) {
      return cellMap.get(location).getValue();
    }
    Cell c = new Cell(this, location);
    cellMap.put(location, c);
    return c.getValue() ;
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
    Cell c = new Cell(this, location);
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
    if (!checkForLoops(cell, new LinkedHashSet<>())) {
      LinkedList<Cell> cellsToRecompute = new LinkedList<>();
      cellsToRecompute.add(cell);
      while(!cellsToRecompute.isEmpty()) {
        boolean hasUncomputedReferencedCells = false;
        Cell currCell = cellsToRecompute.poll();
        for (Cell referencedCell : currCell.getReferencedCells()) {
          if(shouldBeRecomputed(referencedCell)) {
            cellsToRecompute.addFirst(referencedCell);
            hasUncomputedReferencedCells = true;
          }
        }
        if (hasUncomputedReferencedCells) {
          cellsToRecompute.addLast(currCell);
        } else {
          calculateCellValue(currCell);
          invalidCells.remove(currCell);
        }
      }
    }
  }

  private boolean checkForLoops(Cell c, LinkedHashSet<Cell> cellsSeen) {
    if(cellsSeen.contains(c)) {
      markAsValidatedLoop(c, cellsSeen);
      return true;
    } else {
      boolean seen = false;
      cellsSeen.add(c);
      for (Cell cell : c.getReferencedCells()) {
        seen = seen || checkForLoops(cell, cellsSeen);
      }
      cellsSeen.remove(c);
      return seen;
    }
  }

  private void markAsValidatedLoop(Cell startCell, LinkedHashSet<Cell> cells) {
    Iterator<Cell> i = cells.iterator();
    while (i.hasNext()) {
      Cell c = i.next();
      c.setValue(new InvalidValue(c.getExpression()));
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

  private void calculateCellValue(Cell cell) {
    Map<CellLocation, Double> cellVals = new HashMap<>();
    for (Cell referencedCell : cell.getReferencedCells()) {
      referencedCell.getValue().evaluate(new ValueEvaluator() {
        @Override
        public void evaluateDouble(double value) {
          cellVals.put(referencedCell.getLocation(), value);
        }

        @Override
        public void evaluateLoop() {

        }

        @Override
        public void evaluateString(String expression) {

        }

        @Override
        public void evaluateInvalid(String expression) {

        }
      });
    }
    Value val = ExpressionUtils.computeValue(cell.getExpression(), cellVals);
    cell.setValue(val);
  }
}
