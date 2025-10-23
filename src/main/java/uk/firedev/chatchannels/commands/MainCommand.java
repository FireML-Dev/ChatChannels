package uk.firedev.chatchannels.commands;

import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.configs.MessageConfig;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public class MainCommand {

    public static CommandTree getCommand() {
        return new CommandTree("chatchannels")
            .withPermission("chatchannels.command")
            .then(reload());
    }

    private static Argument<String> reload() {
        return new LiteralArgument("reload")
            .executes(info -> {
                ChatChannels.getInstance().reload();
                MessageConfig.getInstance().getReloadedMessage().send(info.sender());
            });
    }

}
