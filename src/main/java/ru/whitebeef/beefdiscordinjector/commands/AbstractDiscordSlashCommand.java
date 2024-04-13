package ru.whitebeef.beefdiscordinjector.commands;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.beefcore.entities.Permission;
import ru.whitebeef.beefdiscordinjector.registry.DiscordSlashCommandRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class AbstractDiscordSlashCommand implements DiscordCommandExecutor, DiscordCommandTabCompleter {

    private static final MessageCreateData NO_BEHAVIOR = MessageCreateData.fromContent("У команды нет поведения!");
    @Getter
    private final Permission permission;
    @Getter
    private final CommandData commandData;
    private final Consumer<SlashCommandInteractionEvent> onCommand;
    private final Map<String, Function<AutoCompleteQuery, List<Command.Choice>>> onTabComplete;

    public AbstractDiscordSlashCommand(@NotNull CommandData commandData, @Nullable Permission permission, @Nullable Consumer<SlashCommandInteractionEvent> onCommand, Map<String, Function<AutoCompleteQuery, List<Command.Choice>>> onTabComplete) {
        this.commandData = commandData;
        this.permission = Objects.requireNonNullElseGet(permission, () -> Permission.of("*"));
        this.onCommand = onCommand;
        this.onTabComplete = onTabComplete;
    }

    public static Builder builder(CommandData commandData, Class<? extends AbstractDiscordSlashCommand> clazz) {
        return new Builder(commandData, clazz);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (onCommand != null) {
            onCommand.accept(event);
            return;
        }
        event.reply(NO_BEHAVIOR).queue();
    }

    public AbstractDiscordSlashCommand register(DiscordSlashCommandRegistry registry) {
        registry.registerCommand(this);
        return this;
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        event.replyChoices(onTabComplete.get(event.getFocusedOption().getName()).apply(event.getFocusedOption())).queue();
    }

    public static class Builder {
        private final Class<? extends AbstractDiscordSlashCommand> clazz;
        private final CommandData commandData;
        private final Map<String, Function<AutoCompleteQuery, List<Command.Choice>>> onTabComplete = new HashMap<>();
        private String permission = "*";
        private Consumer<SlashCommandInteractionEvent> onCommand;

        public Builder(CommandData commandData, Class<? extends AbstractDiscordSlashCommand> clazz) {
            this.commandData = commandData;
            this.clazz = clazz;
        }

        public Builder setOnCommand(Consumer<SlashCommandInteractionEvent> onCommand) {
            this.onCommand = onCommand;
            return this;
        }


        public Builder setPermission(String permission) {
            this.permission = permission;
            return this;
        }

        public Builder addAutoComplete(String name, Function<AutoCompleteQuery, List<Command.Choice>> onTabComplete) {
            this.onTabComplete.put(name, onTabComplete);
            return this;
        }


        public AbstractDiscordSlashCommand build() {
            try {
                return clazz.getDeclaredConstructor(CommandData.class, Permission.class, Consumer.class, Map.class)
                        .newInstance(commandData, Permission.of(permission), onCommand, onTabComplete);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

}
