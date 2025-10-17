package uk.firedev.chatchannels.channels;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

public class LocalChat extends ChatChannel {

    public LocalChat() {
        super("channels/local.yml", ChatChannels.getInstance());
    }

    @NotNull
    @Override
    public String name() {
        return "local";
    }

    @NotNull
    @Override
    public ComponentSingleMessage defaultFormat() {
        return ComponentMessage.componentMessage(
            "<color:#DAA520>[Local]</color> <white>{name} ➻ {message}</white>"
        );
    }

    @Override
    public boolean shouldSendToTarget(@NotNull Player player, @NotNull Player target) {
        return hasAccess(target);
    }

}
