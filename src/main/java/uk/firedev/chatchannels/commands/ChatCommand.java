package uk.firedev.chatchannels.commands;

import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.commands.arguments.ChatChannelArgument;
import uk.firedev.chatchannels.data.PlayerData;
import uk.firedev.daisylib.libs.commandapi.CommandTree;

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
                        new PlayerData(info.sender()).setActiveChannel(channel);
                    })
            );
    }

}
