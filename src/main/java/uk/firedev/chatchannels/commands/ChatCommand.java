package uk.firedev.chatchannels.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.commands.arguments.ChatChannelArgument;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.chatchannels.data.PlayerData;
import uk.firedev.daisylib.command.CommandUtils;

public class ChatCommand {

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("chat")
            .then(
                Commands.argument("channel", new ChatChannelArgument())
                    .executes(ctx -> {
                        Player player = CommandUtils.requirePlayer(ctx);
                        ChatChannel channel = ctx.getArgument("channel", ChatChannel.class);
                        if (!channel.isEnabled() || !channel.hasAccess(player)) {
                            MessageConfig.getInstance().getNoAccessMessage().send(player);
                            return 1;
                        }
                        new PlayerData(player).setActiveChannel(channel);
                        return 1;
                    })
                    .then(
                        Commands.argument("message", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                Player player = CommandUtils.requirePlayer(ctx);
                                ChatChannel channel = ctx.getArgument("channel", ChatChannel.class);
                                if (!channel.isEnabled() || !channel.hasAccess(player)) {
                                    MessageConfig.getInstance().getNoAccessMessage().send(player);
                                    return 1;
                                }
                                String message = ctx.getArgument("message", String.class);
                                channel.sendMessage(player, Component.text(message));
                                return 1;
                            })
                    )
            )
            .build();
    }

}
