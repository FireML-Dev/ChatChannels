package uk.firedev.chatchannels.api;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.chatchannels.data.PlayerData;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import uk.firedev.daisylib.addons.requirement.RequirementChecker;
import uk.firedev.daisylib.addons.requirement.RequirementData;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.messages.message.ComponentListMessage;
import uk.firedev.daisylib.messages.message.ComponentMessage;
import uk.firedev.daisylib.messages.message.ComponentSingleMessage;
import uk.firedev.daisylib.messages.replacer.Replacer;
import uk.firedev.daisylib.registry.RegistryItem;
import uk.firedev.daisylib.utils.CooldownHelper;

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
    @NonNull RequirementChecker accessRequirement();

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
        Component name = processName(sender);
        ComponentSingleMessage message = format().parsePlaceholderAPI(sender)
            .replace("{name}", name)
            .replace(replacer(sender));
        new Messaging(this).sendMessage(sender, component, message);
    }

    default @NonNull Component processName(@NonNull Player player) {
        Component name = player.name();
        ComponentListMessage hover = nameHover(player);
        if (hover != null) {
            name = name.hoverEvent(HoverEvent.showText(hover.toSingleMessage().get()));
        }
        String click = nameClick(player);
        if (click != null) {
            name = name.clickEvent(ClickEvent.suggestCommand(click));
        }
        return name;
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
        return accessRequirement().check(
            new RequirementData(player)
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

    @Nullable ComponentListMessage nameHover(@NonNull Player player);

    @Nullable String nameClick(@NonNull Player player);

    boolean persistent();

}
