package uk.firedev.chatchannels.configs;

import org.jetbrains.annotations.NotNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;

import java.nio.channels.Channel;

public class MessageConfig extends ConfigBase {

    private static final MessageConfig instance = new MessageConfig();

    public MessageConfig() {
        super("messages.yml", "messages.yml", ChatChannels.getInstance());
    }

    public static @NotNull MessageConfig getInstance() {
        return instance;
    }

    // Prefix

    public @NotNull ComponentSingleMessage getPrefix() {
        return getComponentMessage("prefix", "<gray>[ChatChannels]</gray> ").toSingleMessage();
    }

    public @NotNull Replacer getPrefixReplacer() {
        return Replacer.replacer().addReplacement("{prefix}", getPrefix());
    }

    // Messages

    public @NotNull ComponentMessage getReloadedMessage() {
        return getComponentMessage("reloaded", "{prefix}<aqua>Successfully reloaded the plugin.")
            .replace(getPrefixReplacer());
    }

    public @NotNull ComponentMessage getJoinChannelMessage(@NotNull ChatChannel channel) {
        return getComponentMessage("join-channel", "{prefix}<aqua>You are now speaking in <gold>{channel}</gold> Chat")
            .replace(getPrefixReplacer())
            .replace("{channel}", channel.display());
    }

    public @NotNull ComponentMessage getNoNearbyPlayersMessage() {
        return getComponentMessage("no-nearby-players", "{prefix}<red>There are no players within range.")
            .replace(getPrefixReplacer());
    }

}
