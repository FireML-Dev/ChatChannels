package uk.firedev.chatchannels.channels;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

public class GlobalChat extends ChatChannel {

    public GlobalChat() {
        super("channels/global.yml");
    }

    @NotNull
    @Override
    public String name() {
        return "global";
    }

    @NotNull
    @Override
    public ComponentSingleMessage defaultFormat() {
        return ComponentMessage.componentMessage(
            "<color:#00fb9a>[Global]</color> <white>{name} ➻ {message}</white>"
        );
    }

    @Override
    public boolean shouldSendToTarget(@NotNull Player player, @NotNull Player target) {
        return true;
    }

}
