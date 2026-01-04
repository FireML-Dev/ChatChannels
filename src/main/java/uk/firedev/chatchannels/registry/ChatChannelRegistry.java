package uk.firedev.chatchannels.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.channels.ChannelLoader;
import uk.firedev.chatchannels.configs.MainConfig;
import uk.firedev.daisylib.registry.Registry;
import uk.firedev.daisylib.util.Loggers;

import java.util.Map;
import java.util.TreeMap;

public class ChatChannelRegistry implements Registry<ChatChannel> {

    private static final ChatChannelRegistry instance = new ChatChannelRegistry();

    private final ChatChannels plugin = ChatChannels.getInstance();
    private final Map<String, ChatChannel> registry = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final ChannelLoader loader = new ChannelLoader(this);

    private ChatChannelRegistry() {}

    public static ChatChannelRegistry getInstance() {
        return instance;
    }

    public boolean register(@NonNull ChatChannel channel, boolean force) {
        String name = channel.name();
        if (!force && registry.containsKey(name)) {
            Loggers.warn(ChatChannels.getInstance().getComponentLogger(), "Attempted to register already existing ChatChannel: " + name);
            return false;
        }
        registry.put(name, channel);
        // If the plugin is not currently loading, reload.
        if (!plugin.isLoading()) {
            plugin.reload();
        }
        Loggers.info(ChatChannels.getInstance().getComponentLogger(), "Registered ChatChannel " + name);
        return true;
    }

    @Override
    public boolean isEmpty() {
        return registry.isEmpty();
    }

    @Override
    public void clear() {
        registry.clear();
    }

    public void init(@NonNull ChatChannels plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ChatListener(), plugin);
        loader.loadChannels();
    }

    public void reload() {
        cleanRegistry();
        loader.loadChannels();
    }

    private void cleanRegistry() {
        registry.values().removeIf(channel -> !channel.persistent());
    }

    public @NonNull Map<String, ChatChannel> getRegistry() {
        return Map.copyOf(registry);
    }

    @Nullable
    @Override
    public ChatChannel get(@NonNull String s) {
        return registry.get(s);
    }

    @Override
    public boolean unregister(@NonNull String s) {
        return registry.remove(s) != null;
    }

    @NonNull
    @Override
    public ChatChannel getOrDefault(@NonNull String s, @NonNull ChatChannel channel) {
        ChatChannel val = get(s);
        return val == null ? channel : val;
    }

    public @Nullable ChatChannel getChatChannel(@Nullable String id) {
        if (id == null) {
            return null;
        }
        return get(id);
    }

    public @Nullable ChatChannel getInitialChannel() {
        return MainConfig.getInstance().getInitialChannel();
    }

}
