package ru.whitebeef.beefdiscordinjector.registry;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.whitebeef.beefcore.entities.User;
import ru.whitebeef.beefcore.registry.UserRegistry;

import java.util.concurrent.ExecutionException;

@Component
@Log4j2
public class DiscordUserRegistry {

    private final JDA jda;
    private final UserRegistry userRegistry;

    @Autowired
    public DiscordUserRegistry(JDA jda, UserRegistry userRegistry) {
        this.jda = jda;
        this.userRegistry = userRegistry;
    }

    public net.dv8tion.jda.api.entities.User getDiscordUserByUser(@NotNull User discordUser) {
        return jda.getUserById(discordUser.getId());
    }

    public User getUserByDiscordUser(@NotNull net.dv8tion.jda.api.entities.User discordUser) {
        User user = null;
        try {
            user = userRegistry.getUsers().get(discordUser.getIdLong());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (user != null) {
            return user;
        }

        userRegistry.getUsers().put(discordUser.getIdLong(), userRegistry.getUserLoader().getDefaultUser(discordUser.getIdLong()));
        return userRegistry.getUsers().getUnchecked(discordUser.getIdLong());
    }
}
