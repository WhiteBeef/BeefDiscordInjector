package ru.whitebeef.beefdiscordinjector.registry;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.beefcore.entities.User;
import ru.whitebeef.beefdiscordinjector.commands.AbstractDiscordSlashCommand;
import ru.whitebeef.beefdiscordinjector.repliers.DefaultRepliers;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class DiscordSlashCommandRegistry extends ListenerAdapter {


    private final Guild guild;
    private final DiscordUserRegistry discordUserRegistry;
    // label, command
    private final Map<String, AbstractDiscordSlashCommand> commands = new HashMap<>();

    @Autowired
    public DiscordSlashCommandRegistry(JDA jda, Guild guild, DiscordUserRegistry discordUserRegistry) {
        jda.addEventListener(this);
        this.guild = guild;
        this.discordUserRegistry = discordUserRegistry;
    }

    public void registerCommand(AbstractDiscordSlashCommand abstractDiscordSlashCommand) {
        guild.upsertCommand(abstractDiscordSlashCommand.getCommandData()).queue();
        commands.put(abstractDiscordSlashCommand.getCommandData().getName(), abstractDiscordSlashCommand);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        AbstractDiscordSlashCommand command = commands.get(event.getName());

        if (command == null) {
            DefaultRepliers.COMMAND_NOT_FOUND.reply(event);
            return;
        }

        User user = discordUserRegistry.getUserByDiscordUser(event.getUser());

        if (!user.hasPermission(command.getPermission())) {
            DefaultRepliers.NO_PERMISSION.reply(event);
            return;
        }
        command.onSlashCommandInteraction(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        super.onCommandAutoCompleteInteraction(event);

        AbstractDiscordSlashCommand command = commands.get(event.getName());
        if (command == null) {
            return;
        }

        User user = discordUserRegistry.getUserByDiscordUser(event.getUser());

        if (!user.hasPermission(command.getPermission())) {
            return;
        }
        command.onCommandAutoCompleteInteraction(event);
    }


    @PostConstruct
    public void registerCommands() {
    }
}
