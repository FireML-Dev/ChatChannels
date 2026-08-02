package uk.firedev.chatchannels.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ChatChannelArgument implements CustomArgumentType.Converted<ChatChannel, String> {

    private static final DynamicCommandExceptionType INVALID_CHANNEL = new DynamicCommandExceptionType(name ->
        MessageComponentSerializer.message().serialize(Component.text("Invalid Channel: " + name))
    );

    public List<String> getSuggestions(@NonNull CommandContext<?> ctx) {
        if (!(ctx.getSource() instanceof CommandSourceStack stack)) {
            return List.of();
        }
        if (!(stack.getSender() instanceof Player player)) {
            return List.of();
        }
        return ChatChannelRegistry.getInstance().getRegistry().values().stream()
            .filter(channel -> channel.hasAccess(player))
            .map(ChatChannel::name)
            .toList();
    }

    @NonNull
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NonNull CommandContext<S> context, @NonNull SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        getSuggestions(context).stream()
            .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(remaining))
            .forEach(builder::suggest);
        return builder.buildFuture();
    }

    /**
     * Converts the value from the native type to the custom argument type.
     *
     * @param nativeType native argument provided value
     * @return converted value
     * @throws CommandSyntaxException if an exception occurs while parsing
     * @see #convert(Object, Object)
     */
    @Override
    public @NonNull ChatChannel convert(@NonNull String nativeType) throws CommandSyntaxException {
        ChatChannel channel = ChatChannelRegistry.getInstance().getChatChannel(nativeType);
        if (channel == null) {
            throw INVALID_CHANNEL.create(nativeType);
        }
        return channel;
    }

    /**
     * Gets the native type that this argument uses,
     * the type that is sent to the client.
     *
     * @return native argument type
     */
    @Override
    public @NonNull ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

}
