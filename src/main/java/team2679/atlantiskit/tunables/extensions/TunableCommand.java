package atlantis2679.lib.tunables.extensions;

import java.util.function.Function;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj2.command.Command;
import atlantis2679.lib.tunables.SendableType;
import atlantis2679.lib.tunables.Tunable;
import atlantis2679.lib.tunables.TunablesTable;

public abstract class TunableCommand extends Command implements Tunable {
    public Tunable fullTunable() {
        return (builder) -> {
            builder.setSendableType(SendableType.LIST);
            builder.addChild("run button", (Sendable) this);
            initTunable(builder);
        };
    };

    public TunableCommand extendTunable(Tunable tunable) {
        return wrap(this, (builder) -> {
            this.initTunable(builder);
            tunable.initTunable(builder);
        });
    }

    public static TunableCommand wrap(Function<TunablesTable, Command> commandFactory, SendableType sendableType) {
        return new TunableWrapperCommand(commandFactory, sendableType);
    }

    public static TunableCommand wrap(Function<TunablesTable, Command> commandFactory) {
        return new TunableWrapperCommand(commandFactory);
    }

    public static TunableCommand wrap(Command command, Tunable tunable) {
        return new TunableWrapperCommand(command, tunable);
    }
}
