package team2679.atlantiskit.periodicalerts;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.wpilib.util.Alert;

public class PeriodicAlert {
  private final PeriodicAlertsGroup group;
  private final Supplier<String> messageSupplier;
  private final BooleanSupplier isActive;
  private final Alert alert;

  public PeriodicAlert(PeriodicAlertsGroup group, Supplier<String> messageSupplier, BooleanSupplier isActive, Alert.Level alertLevel) {
      this.messageSupplier = messageSupplier;
      this.alert = new Alert(group.getName(), messageSupplier.get(), alertLevel);
      this.group = group;
      this.isActive = isActive;
  }

  protected void update() {
      alert.setText(messageSupplier.get());
      alert.set(isActive.getAsBoolean());
  }

  public boolean getIsActive() {
    return isActive.getAsBoolean();
  }

  public String getMessage() {
    return messageSupplier.get();
  }

  public Alert.Level getAlertLevel() {
    return alert.getLevel();
  }

  public PeriodicAlertsGroup getGroup() {
    return group;
  }
}