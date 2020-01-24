package nl.freelist.domain.crossCuttingConcerns.dtrValidations;

import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.crossCuttingConcerns.TimeHelper;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.DtrConstraint;

public class NoThursdaysDtrValidation extends DtrValidation {

  public Result validate(DateTimeRange dateTimeRange) {
    return TimeHelper.checkIfDayNotPresentInDtr(dateTimeRange, "THURSDAY");
  }

  @Override
  public List<DtrConstraint> list() {
    DtrConstraint dtrConstraint = DtrConstraint.Create("NOTHURSDAYS", null);
    List<DtrConstraint> dtrConstraintList = new ArrayList<>();
    dtrConstraintList.add(dtrConstraint);
    return dtrConstraintList;
  }

}
