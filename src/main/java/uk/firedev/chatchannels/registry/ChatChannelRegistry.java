package uk.firedev.chatchannels.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.channels.GlobalChat;
import uk.firedev.chatchannels.channels.LocalChat;

import java.util.Map;
import java.util.TreeMap;

public class ChatChannelRegistry {

    private static final ChatChannelRegistry instance = new ChatChannelRegistry();

    private final Map<String, ChatChannel> registry = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private final GlobalChat globalChat = new GlobalChat();
    private final LocalChat localChat = new LocalChat();

    private ChatChannelRegistry() {}

    public static ChatChannelRegistry getInstance() {
        return instance;
    }

    public void register(@NotNull ChatChannel channel) {
        register(channel, false);
    }

    public void register(@NotNull ChatChannel channel, boolean force) {
        String name = channel.name();
        if (!force && registry.containsKey(name)) {
            return;
        }
        registry.put(name, channel);
    }

    public boolean isEmpty() {
        return registry.isEmpty();
    }

    public void init(@NotNull ChatChannels plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ChatListener(), plugin);
        register(globalChat);
        register(localChat);
    }

    public void reload() {
        registry.values().forEach(ChatChannel::reload);
    }

    public @NotNull Map<String, ChatChannel> getRegistry() {
        return Map.copyOf(registry);
    }

    public @Nullable ChatChannel getChatChannel(@Nullable String id) {
        if (id == null) {
            return null;
        }
        return registry.get(id);
    }

    public GlobalChat getGlobalChat() {
        return globalChat;
    }

    public LocalChat getLocalChat() {
        return localChat;
    }

}
