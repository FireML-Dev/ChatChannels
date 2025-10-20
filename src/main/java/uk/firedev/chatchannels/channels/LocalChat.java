package uk.firedev.chatchannels.channels;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;

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

    @Nullable
    @Override
    public Replacer replacer(@NotNull Player player) {
        return null;
    }

}
