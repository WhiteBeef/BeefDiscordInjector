package ru.whitebeef.beefdiscordinjector.registrars;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.whitebeef.beefcore.command.AbstractCommand;
import ru.whitebeef.beefcore.command.SimpleCommand;
import ru.whitebeef.beefcore.registry.CommandRegistry;
import ru.whitebeef.beefdiscordinjector.commands.defaultcommands.permission.PermissionGetUserCommand;
import ru.whitebeef.beefdiscordinjector.commands.defaultcommands.permission.PermissionSetUserCommand;

@Component
public class PermissionCommandRegistrar {

    private final CommandRegistry commandRegistry;

    public PermissionCommandRegistrar(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @PostConstruct
    public void registerCommand() {
        AbstractCommand.builder("permission", SimpleCommand.class)
                .addSubCommand(AbstractCommand.builder("get", PermissionGetUserCommand.class).build())
                .addSubCommand(AbstractCommand.builder("set", PermissionSetUserCommand.class).build())
                .setDescription("Управление правами")
                .setUsageMessage("permission <get|set>")
                .build().register(commandRegistry);
    }
}
