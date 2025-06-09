package atlantis2679.lib.tunables.extensions;

import java.util.Set;
import java.util.function.Function;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import atlantis2679.lib.tunables.SendableType;
import atlantis2679.lib.tunables.Tunable;
import atlantis2679.lib.tunables.TunableBuilder;
import atlantis2679.lib.tunables.TunablesTable;

public class TunableWrapperCommand extends TunableCommand {
    private final Command command;
    private final Tunable tunable;

    public TunableWrapperCommand(Function<TunablesTable, Command> commandFactory, SendableType sendableType) {
        TunablesTable tunablesTable = new TunablesTable(sendableType);
        this.command = commandFactory.apply(tunablesTable);
        this.tunable = tunablesTable;

        setName(command.getName());
    }

    public TunableWrapperCommand(Function<TunablesTable, Command> commandFactory) {
        this(commandFactory, SendableType.NONE);
    }

    public TunableWrapperCommand(Command command, Tunable tunable) {
        this.command = command;
        this.tunable = tunable;

        setName(command.getName());
    }

    public void initialize() {
        command.initialize();
    }

    public void execute() {
        command.execute();
    }

    public void end(boolean interrupted) {
        command.end(interrupted);
    }

    public boolean isFinished() {
        return command.isFinished();
    }

    public Set<Subsystem> getRequirements() {
        return command.getRequirements();
    }

    public boolean runsWhenDisabled() {
        return command.runsWhenDisabled();
    }

    public InterruptionBehavior getInterruptionBehavior() {
        return command.getInterruptionBehavior();
    }

    @Override
    public void initTunable(TunableBuilder builder) {
        tunable.initTunable(builder);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        command.initSendable(builder);
    }
}