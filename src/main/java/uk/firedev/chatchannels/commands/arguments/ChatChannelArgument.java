package uk.firedev.chatchannels.commands.arguments;

import org.bukkit.entity.Player;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import uk.firedev.daisylib.command.ArgumentBuilder;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.CustomArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;

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
            ArgumentBuilder.getAsyncSuggestions(info ->
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
        );
    }

}
