package uk.firedev.chatchannels.configs;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;

public class MessageConfig extends ConfigBase {

    private static final MessageConfig instance = new MessageConfig();

    public MessageConfig() {
        super("messages.yml", "messages.yml", ChatChannels.getInstance());
    }

    public static @NonNull MessageConfig getInstance() {
        return instance;
    }

    // Prefix

    public @NonNull ComponentSingleMessage getPrefix() {
        return getComponentMessage("prefix", "<gray>[ChatChannels]</gray> ").toSingleMessage();
    }

    public @NonNull Replacer getPrefixReplacer() {
        return Replacer.replacer().addReplacement("{prefix}", getPrefix());
    }

    // Messages

    public @NonNull ComponentMessage getReloadedMessage() {
        return getComponentMessage("reloaded", "{prefix}<aqua>Successfully reloaded the plugin.")
            .replace(getPrefixReplacer());
    }

    public @NonNull ComponentMessage getJoinChannelMessage(@NonNull ChatChannel channel) {
        return getComponentMessage("join-channel", "{prefix}<aqua>You are now speaking in <gold>{channel}</gold> Chat")
            .replace(getPrefixReplacer())
            .replace("{channel}", channel.display());
    }

    public @NonNull ComponentMessage getNoNearbyPlayersMessage() {
        return getComponentMessage("no-nearby-players", "{prefix}<red>There are no players within range.")
            .replace(getPrefixReplacer());
    }

    public @NonNull ComponentMessage getNoAccessMessage() {
        return getComponentMessage("no-access", "{prefix}<red>You cannot access that channel!")
            .replace(getPrefixReplacer());
    }

    public @NonNull ComponentMessage getNoLongerExistsMessage() {
        return getComponentMessage("no-longer-exists", "{prefix}<red>The channel you were in no longer exists.")
            .replace(getPrefixReplacer());
    }

    public @NonNull ComponentMessage getNotInChannelMessage() {
        return getComponentMessage("not-in-channel", "{prefix}<red>You are no longer in a chat channel.")
            .replace(getPrefixReplacer());
    }

}
