package ru.whitebeef.beefdiscordinjector.repliers;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

@FunctionalInterface
public interface Replier {

    void reply(IReplyCallback replyCallback);

}
