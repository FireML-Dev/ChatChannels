package uk.firedev.chatchannels.data;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;

public record PlayerData(@NonNull Player player) {

    private static final NamespacedKey CHANNEL_KEY = new NamespacedKey(ChatChannels.getInstance(), "channel");

    @Override
    public @NonNull Player player() {
        return this.player;
    }

    /**
     * Fetches the player's active chat channel from their {@link PersistentDataContainer}.
     *
     * @return The player's active chat channel, or global channel if null or invalid.
     */
    public @Nullable ChatChannel getActiveChannel() {
        String channelStr = this.player.getPersistentDataContainer().get(CHANNEL_KEY, PersistentDataType.STRING);
        if (channelStr == null) {
            return ChatChannelRegistry.getInstance().getInitialChannel();
        }
        ChatChannel channel = ChatChannelRegistry.getInstance().getChatChannel(channelStr);
        if (channel == null) {
            MessageConfig.getInstance().getNoLongerExistsMessage().send(this.player);
            resetActiveChannel();
            return ChatChannelRegistry.getInstance().getInitialChannel();
        }
        return channel;
    }

    public void setActiveChannel(@NonNull ChatChannel channel) {
        this.player.getPersistentDataContainer().set(CHANNEL_KEY, PersistentDataType.STRING, channel.name());
        MessageConfig.getInstance().getJoinChannelMessage(channel).send(this.player);
        player.updateCommands();
    }

    public void resetActiveChannel() {
        ChatChannel initial = ChatChannelRegistry.getInstance().getInitialChannel();
        if (initial == null) {
            this.player.getPersistentDataContainer().remove(CHANNEL_KEY);
            MessageConfig.getInstance().getNotInChannelMessage().send(this.player);
            return;
        }
        setActiveChannel(initial);
    }

}
