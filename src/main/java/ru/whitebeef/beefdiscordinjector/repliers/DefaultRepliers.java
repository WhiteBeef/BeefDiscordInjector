package ru.whitebeef.beefdiscordinjector.repliers;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public enum DefaultRepliers {


    SUCCESS((replyCallback) -> {
        replyCallback.reply("Успех!").setEphemeral(true).queue();
    }),
    COMMAND_NOT_FOUND((replyCallback) -> {
        replyCallback.reply("Команда не найдена!").setEphemeral(true).queue();
    }),
    NO_PERMISSION((replyCallback) -> {
        replyCallback.reply("Недостаточно прав!").setEphemeral(true).queue();
    });

    private final Replier replier;

    DefaultRepliers(Replier replier) {
        this.replier = replier;
    }

    public void reply(IReplyCallback replyCallback) {
        replier.reply(replyCallback);
    }
}
