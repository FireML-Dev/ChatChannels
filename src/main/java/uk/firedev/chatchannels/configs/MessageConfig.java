package uk.firedev.chatchannels.configs;

import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.daisylib.config.BasicConfig;
import uk.firedev.daisylib.messages.message.ComponentMessage;
import uk.firedev.daisylib.messages.message.ComponentSingleMessage;

public class MessageConfig extends BasicConfig {

    private static final MessageConfig instance = new MessageConfig();

    public MessageConfig() {
        super("messages.yml", "messages.yml", ChatChannels.get());
    }

    public static @NonNull MessageConfig getInstance() {
        return instance;
    }

    // Prefix

    public @NonNull ComponentSingleMessage getPrefix() {
        return super.getComponentMessage("prefix", "<gray>[ChatChannels]</gray> ").toSingleMessage();
    }

    // Messages

    public @NonNull ComponentMessage<?, ?> getReloadedMessage() {
        return getComponentMessage("reloaded", "{prefix}<aqua>Successfully reloaded the plugin.");
    }

    public @NonNull ComponentMessage<?, ?> getJoinChannelMessage(@NonNull ChatChannel channel) {
        return getComponentMessage("join-channel", "{prefix}<aqua>You are now speaking in <gold>{channel}</gold> Chat")
            .replace("{channel}", channel.display());
    }

    public @NonNull ComponentMessage<?, ?> getNoNearbyPlayersMessage() {
        return getComponentMessage("no-nearby-players", "{prefix}<red>There are no players within range.");
    }

    public @NonNull ComponentMessage<?, ?> getNoAccessMessage() {
        return getComponentMessage("no-access", "{prefix}<red>You cannot access that channel!");
    }

    public @NonNull ComponentMessage<?, ?> getNoLongerExistsMessage() {
        return getComponentMessage("no-longer-exists", "{prefix}<red>The channel you were in no longer exists.");
    }

    public @NonNull ComponentMessage<?, ?> getNotInChannelMessage() {
        return getComponentMessage("not-in-channel", "{prefix}<red>You are no longer in a chat channel.");
    }

    @Override
    public ComponentMessage<?, ?> getComponentMessage(@NonNull String path, @NonNull Object def) {
        return super.getComponentMessage(path, def).replace("{prefix}", getPrefix());
    }

}
