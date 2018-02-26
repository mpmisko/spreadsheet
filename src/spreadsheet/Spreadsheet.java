package spreadsheet;

import common.api.CellLocation;
import common.api.Tabular;
import common.api.value.Value;

public class Spreadsheet implements Tabular {

  @Override
  public void setExpression(CellLocation location, String expression) {

  }

  @Override
  public String getExpression(CellLocation location) {
    return null;
  }

  @Override
  public Value getValue(CellLocation location) {
    return null;
  }

  @Override
  public void recompute() {

  }
}
