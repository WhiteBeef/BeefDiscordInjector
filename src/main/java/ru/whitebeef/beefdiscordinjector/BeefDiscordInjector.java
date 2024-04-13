package ru.whitebeef.beefdiscordinjector;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import ru.whitebeef.beefcore.plugin.BeefPlugin;
import ru.whitebeef.beefcore.plugin.PluginClassLoader;
import ru.whitebeef.beefcore.plugin.PluginInfo;

public class BeefDiscordInjector extends BeefPlugin {

    @Getter
    private static BeefDiscordInjector instance;

    public BeefDiscordInjector(@NotNull PluginInfo info, @NotNull PluginClassLoader pluginClassLoader, @NotNull GenericApplicationContext pluginApplicationContext) {
        super(info, pluginClassLoader, pluginApplicationContext);
    }

    @Bean
    public JDA startJDA() throws InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(getConfig().get("token").getAsString())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .enableCache(CacheFlag.ACTIVITY)
                .setActivity(Activity.watching("на БиБиф'а"));
        return builder.build().awaitReady();
    }

    @Bean
    public Guild connectToGuild(JDA jda) {
        return jda.getGuildById(getConfig().get("guild_id").getAsString());
    }

}