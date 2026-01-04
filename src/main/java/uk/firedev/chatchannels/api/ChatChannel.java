package uk.firedev.chatchannels.api;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.chatchannels.data.PlayerData;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import uk.firedev.daisylib.addons.requirement.Requirement;
import uk.firedev.daisylib.addons.requirement.RequirementData;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;
import uk.firedev.daisylib.registry.RegistryItem;
import uk.firedev.daisylib.util.CooldownHelper;
import uk.firedev.daisylib.util.Loggers;

import java.util.List;

public interface ChatChannel extends RegistryItem {

    /**
     * @return Whether this channel should be enabled or not.
     */
    boolean isEnabled();

    /**
     * @return The name of this channel.
     */
    @NonNull String name();

    default @NonNull String getKey() {
        return name();
    }

    /**
     * @return The plugin that owns this channel.
     */
    @NonNull Plugin plugin();

    /**
     * @return How this channel should look to players.
     */
    @NonNull ComponentSingleMessage display();

    /**
     * @return If pings should be enabled.
     */
    boolean enablePing();

    /**
     * @return The sound a ping should make.
     */
    @Nullable Sound pingSound();

    /**
     * @return The cooldown time between pings.
     */
    int pingCooldown();

    /**
     * @return The requirements to access this channel.
     */
    @NonNull Requirement accessRequirement();

    /**
     * @return The chat format for this channel.
     */
    @NonNull ComponentSingleMessage format();

    /**
     * @return The radius for this channel's messages. Set to 0 or below to disable.
     */
    long radius();

    default void handleEvent(@NonNull AsyncChatEvent event) {
        Player sender = event.getPlayer();
        if (!hasAccess(sender)) {
            MessageConfig.getInstance().getNoAccessMessage().send(sender);
            new PlayerData(sender).resetActiveChannel();
            return;
        }
        sendMessage(sender, event.message());
    }

    default void sendMessage(@NonNull Player sender, @NonNull Component component) {
        ComponentSingleMessage message = format().parsePlaceholderAPI(sender)
            .replace("{name}", sender.name())
            .replace(replacer(sender));
        new Messaging(this).sendMessage(sender, component, message);
    }

    /**
     * @param player The player who sent the message.
     * @param target The player who is receiving the message.
     * @return If the target player should receive the message.
     */
    boolean shouldSendToTarget(@NonNull Player player, @NonNull Player target);

    /**
     * @param player The relevant player.
     * @return Text replacements for this channel.
     */
    @Nullable Replacer replacer(@NonNull Player player);

    /**
     * @return The {@link CooldownHelper} instance to handle ping cooldowns.
     */
    @NonNull CooldownHelper pingCooldownHandler();

    default boolean hasAccess(@NonNull Player player) {
        if (!isEnabled()) {
            return false;
        }
        return accessRequirement().meetsRequirements(
            new RequirementData().withPlayer(player)
        );
    }

    /**
     * @return The command shortcuts to access this channel.
     */
    @NonNull List<String> aliases();

    default boolean canUseCommand() {
        return ChatChannelRegistry.getInstance().getChatChannel(name()) != null;
    }

    default void registerAliases(@NonNull Commands registrar) {
        if (!canUseCommand()) {
            return;
        }
        List<String> aliases = aliases();
        if (aliases.isEmpty()) {
            return;
        }
        Loggers.info(ChatChannels.getInstance().getComponentLogger(), "Registering aliases. The server may reload a few times.");
        aliases.forEach(alias -> registerAlias(alias, registrar));
    }

    private void registerAlias(@NonNull String alias, @NonNull Commands registrar) {
        LiteralCommandNode<CommandSourceStack> command = Commands.literal(alias)
            .requires(source -> {
                if (!canUseCommand()) {
                    return false;
                }
                if (!(source.getSender() instanceof Player player)) {
                    return false;
                }
                return hasAccess(player);
            })
            .then(
                Commands.argument("message", StringArgumentType.greedyString())
                    .executes(ctx -> {
                        Player player = CommandUtils.requirePlayer(ctx);
                        String message = ctx.getArgument("message", String.class);
                        sendMessage(
                            player,
                            ComponentMessage.componentMessage(message).get()
                        );
                        return 1;
                    })
            )
            .executes(ctx -> {
                Player player = CommandUtils.requirePlayer(ctx);
                new PlayerData(player).setActiveChannel(this);
                return 1;
            })
            .build();
        registrar.register(command);
    }

    default boolean persistent() {
        return true;
    }

}
