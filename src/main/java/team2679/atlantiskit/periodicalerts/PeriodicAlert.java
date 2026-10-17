package team2679.atlantiskit.periodicalerts;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.wpilib.util.Alert;

public class PeriodicAlert {
  private final PeriodicAlertsGroup group;
  private final Supplier<String> messageSupplier;
  private final BooleanSupplier isActive;
  private final Alert alert;

  protected PeriodicAlert(PeriodicAlertsGroup group, Supplier<String> messageSupplier, BooleanSupplier isActive, Alert alert) {
    this.group = group;
    this.messageSupplier = messageSupplier;
    this.isActive = isActive;
    this.alert = alert;
  }

  protected void update() {
    alert.setText(messageSupplier.get());
    alert.set(isActive.getAsBoolean());
  }

  public PeriodicAlertsGroup getGroup() {
    return group;
  }


  public String getMessage() {
    return messageSupplier.get();
  }

  public boolean getIsActive() {
    return isActive.getAsBoolean();
  }

  public Alert.Level getAlertLevel() {
    return alert.getLevel();
  }
}