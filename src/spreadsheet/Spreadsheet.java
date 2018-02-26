package spreadsheet;

import common.api.CellLocation;
import common.api.Tabular;
import common.api.value.StringValue;
import common.api.value.Value;
import java.util.HashMap;
import java.util.Map;

public class Spreadsheet implements Tabular {

  private Map<CellLocation, Cell> cellMap;

  public Spreadsheet() {
    this.cellMap = new HashMap<>();
  }

  @Override
  public void setExpression(CellLocation location, String expression) {
    if(cellMap.containsKey(location)) {
      System.out.println("here");
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
    return new StringValue("");
  }

  @Override
  public void recompute() {

  }

  @Override
  public String getExpression(CellLocation location) {
    if(cellMap.containsKey(location)) {
      return cellMap.get(location).getExpression();
    }
    return "";
  }
}
