package uk.firedev.chatchannels.commands.arguments;

import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import org.bukkit.entity.Player;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;

import java.util.concurrent.CompletableFuture;

public class ChatChannelArgument {

    public static Argument<ChatChannel> get() {
        return new CustomArgument<>(new StringArgument("channel"), info -> {
            ChatChannel channel = ChatChannelRegistry.getInstance().getChatChannel(info.input());
            if (channel == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                    new CustomArgument.MessageBuilder("Unknown channel: ").appendArgInput()
                );
            }
            return channel;
        }).includeSuggestions(
            ArgumentSuggestions.stringsAsync(info ->
                CompletableFuture.supplyAsync(() ->
                    ChatChannelRegistry.getInstance().getRegistry().values().stream()
                        .filter(channel -> {
                            if (!(info.sender() instanceof Player player)) {
                                return true;
                            }
                            return channel.hasAccess(player);
                        })
                        .map(ChatChannel::name)
                        .toArray(String[]::new)
                )
            )
        );
    }

}
