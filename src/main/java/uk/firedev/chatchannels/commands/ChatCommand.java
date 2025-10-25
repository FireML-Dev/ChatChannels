package uk.firedev.chatchannels.commands;

import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.api.ConfigChatChannel;
import uk.firedev.chatchannels.commands.arguments.ChatChannelArgument;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.chatchannels.data.PlayerData;
import dev.jorel.commandapi.CommandTree;

import java.util.Objects;

public class ChatCommand {

    public static CommandTree getCommand() {
        return new CommandTree("chat")
            .withAliases("channel")
            .withShortDescription("Access chat channels")
            .withPermission("chatchannels.command")
            .then(
                ChatChannelArgument.get()
                    .executesPlayer(info -> {
                        ChatChannel channel = Objects.requireNonNull(info.args().getUnchecked("channel"));
                        if (!channel.isEnabled() || !channel.hasAccess(info.sender())) {
                            MessageConfig.getInstance().getNoAccessMessage().send(info.sender());
                            return;
                        }
                        new PlayerData(info.sender()).setActiveChannel(channel);
                    })
            );
    }

}
