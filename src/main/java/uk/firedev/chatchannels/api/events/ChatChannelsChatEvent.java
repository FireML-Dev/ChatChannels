package uk.firedev.chatchannels.api.events;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import uk.firedev.chatchannels.api.ChatChannel;

public class ChatChannelsChatEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final ChatChannel channel;
    private final Player sender;
    private Component message;
    private boolean cancel;

    @ApiStatus.Internal
    public ChatChannelsChatEvent(@NotNull ChatChannel channel, @NotNull Player sender, @NotNull Component message) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public @NotNull ChatChannel channel() {
        return this.channel;
    }

    public @NotNull Player sender() {
        return this.sender;
    }

    public @NotNull Component message() {
        return this.message;
    }

    public void message(@NotNull Component message) {
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
        System.out.println("Calling ChatChannelsChatEvent.");
        System.out.println("Sender: " + sender.getName());
        System.out.println("Channel: " + channel.name());
        System.out.println("Message: " + message);
        return super.callEvent();
    }

}
