package ru.whitebeef.beefdiscordinjector.registrars;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.beefcore.command.AbstractCommand;
import ru.whitebeef.beefcore.command.SimpleCommand;
import ru.whitebeef.beefcore.registry.CommandRegistry;
import ru.whitebeef.beefcore.registry.RoleRegistry;
import ru.whitebeef.beefcore.registry.UserRegistry;

@Component
@Log4j2
public class CacheCommandRegistrar {
    private final JDA jda;
    private final UserRegistry userRegistry;
    private final RoleRegistry roleRegistry;
    private final CommandRegistry commandRegistry;

    @Autowired
    public CacheCommandRegistrar(JDA jda, UserRegistry userRegistry, RoleRegistry roleRegistry, CommandRegistry commandRegistry) {
        this.jda = jda;
        this.userRegistry = userRegistry;
        this.roleRegistry = roleRegistry;
        this.commandRegistry = commandRegistry;
    }

    @PostConstruct
    public void registerCommand() {
        AbstractCommand.builder("cache", SimpleCommand.class)
                .addSubCommand(AbstractCommand.builder("get", SimpleCommand.class)
                        .addSubCommand(AbstractCommand.builder("users", SimpleCommand.class)
                                .setOnCommand(args -> {
                                    log.info("Загруженные пользователи:");
                                    userRegistry.getLoadedUsers().forEach(user -> log.info(user.getId() + ": " + jda.getUserById(user.getId())));
                                    log.info("Всего: " + userRegistry.getLoadedUsers().size() + " пользователей");
                                })
                                .build())
                        .addSubCommand(AbstractCommand.builder("roles", SimpleCommand.class)
                                .setOnCommand(args -> {
                                    log.info("Загруженные роли:");
                                    roleRegistry.getLoadedRoles()
                                            .forEach(role -> {
                                                log.info(role.getName() + ":");
                                                role.getPermissions().forEach((key, value) -> log.info((value.toBoolean() ? "+ '" : "- '") + key.getPermission() + "'"));
                                            });
                                    log.info("Всего: " + roleRegistry.getLoadedRoles().size() + " ролей");
                                })
                                .build())
                        .build())
                .setDescription("Просмотр кэша")
                .setUsageMessage("cache get <users|roles>")
                .build().register(commandRegistry);
    }

}
