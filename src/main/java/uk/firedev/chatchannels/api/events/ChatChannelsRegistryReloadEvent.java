package uk.firedev.chatchannels.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;

/// Used to register non-persistent chat channels after the plugin reloads.
public class ChatChannelsRegistryReloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final ChatChannelRegistry registry;

    @ApiStatus.Internal
    public ChatChannelsRegistryReloadEvent(@NonNull ChatChannelRegistry registry) {
        this.registry = registry;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return handlers;
    }

    public @NonNull ChatChannelRegistry registry() {
        return this.registry;
    }

}
