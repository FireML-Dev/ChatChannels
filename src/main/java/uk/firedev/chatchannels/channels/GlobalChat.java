package uk.firedev.chatchannels.channels;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;

public class GlobalChat extends ChatChannel {

    public GlobalChat() {
        super("channels/global.yml", ChatChannels.getInstance());
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

    @Nullable
    @Override
    public Replacer replacer(@NotNull Player player) {
        return null;
    }

    @Override
    public void handleEvent(@NotNull AsyncChatEvent event) {
        if (!isEnabled()) {
            event.setCancelled(false);
            return;
        }
        super.handleEvent(event);
    }

}
