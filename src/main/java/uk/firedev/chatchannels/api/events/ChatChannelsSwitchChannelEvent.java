package uk.firedev.chatchannels.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.chatchannels.api.ChatChannel;

public class ChatChannelsSwitchChannelEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final ChatChannel to;
    private final ChatChannel from;
    private final Player player;
    private boolean cancel;

    @ApiStatus.Internal
    public ChatChannelsSwitchChannelEvent(@Nullable ChatChannel to, @Nullable ChatChannel from, @NonNull Player player) {
        this.to = to;
        this.from = from;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return handlers;
    }

    public @Nullable ChatChannel to() {
        return this.to;
    }

    public @Nullable ChatChannel from() {
        return this.from;
    }

    public @NonNull Player player() {
        return this.player;
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
