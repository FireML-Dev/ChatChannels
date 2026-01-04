package uk.firedev.chatchannels.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.configs.MessageConfig;

public class MainCommand {

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("chatchannels")
            .requires(source -> source.getSender().hasPermission("chatchannels.command"))
            .then(reload())
            .build();
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reload() {
        return Commands.literal("reload")
            .executes(ctx -> {
                ChatChannels.getInstance().reload();
                MessageConfig.getInstance().getReloadedMessage().send(ctx.getSource().getSender());
                return 1;
            });
    }

}
