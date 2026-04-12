package uk.firedev.chatchannels.api.events;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.chatchannels.api.ChatChannel;

public class ChatChannelsChatEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final ChatChannel channel;
    private final Player sender;
    private Component message;
    private boolean cancel;

    @ApiStatus.Internal
    public ChatChannelsChatEvent(@NonNull ChatChannel channel, @Nullable Player sender, @NonNull Component message) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return handlers;
    }

    public @NonNull ChatChannel channel() {
        return this.channel;
    }

    public @Nullable Player sender() {
        return this.sender;
    }

    public @NonNull Component message() {
        return this.message;
    }

    public void message(@NonNull Component message) {
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean callEvent() {
        return super.callEvent();
    }

}
