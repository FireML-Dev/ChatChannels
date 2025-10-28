package uk.firedev.chatchannels.api;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.chatchannels.data.PlayerData;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.addons.requirement.Requirement;
import uk.firedev.daisylib.addons.requirement.RequirementData;
import uk.firedev.daisylib.command.CooldownHelper;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;
import uk.firedev.daisylib.registry.RegistryItem;

import java.util.List;
import java.util.Objects;

public interface ChatChannel extends RegistryItem {

    /**
     * @return Whether this channel should be enabled or not.
     */
    boolean isEnabled();

    /**
     * @return The name of this channel.
     */
    @NotNull String name();

    default @NotNull String getKey() {
        return name();
    }

    /**
     * @return The plugin that owns this channel.
     */
    @NotNull Plugin plugin();

    /**
     * @return How this channel should look to players.
     */
    @NotNull ComponentSingleMessage display();

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
    @NotNull Requirement accessRequirement();

    /**
     * @return The chat format for this channel.
     */
    @NotNull ComponentSingleMessage format();

    /**
     * @return The radius for this channel's messages. Set to 0 or below to disable.
     */
    long radius();

    default void handleEvent(@NotNull AsyncChatEvent event) {
        Player sender = event.getPlayer();
        if (!hasAccess(sender)) {
            MessageConfig.getInstance().getNoAccessMessage().send(sender);
            new PlayerData(sender).resetActiveChannel();
            return;
        }
        sendMessage(sender, event.message());
    }

    default void sendMessage(@NotNull Player sender, @NotNull Component component) {
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
    boolean shouldSendToTarget(@NotNull Player player, @NotNull Player target);

    /**
     * @param player The relevant player.
     * @return Text replacements for this channel.
     */
    @Nullable Replacer replacer(@NotNull Player player);

    /**
     * @return The {@link CooldownHelper} instance to handle ping cooldowns.
     */
    @NotNull CooldownHelper pingCooldownHandler();

    default boolean hasAccess(@NotNull Player player) {
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
    @NotNull List<String> aliases();

    default void registerAliases() {
        List<String> aliases = aliases();
        if (aliases.isEmpty()) {
            return;
        }
        Loggers.info(ChatChannels.getInstance().getComponentLogger(), "Registering aliases. The server may reload a few times.");
        aliases.forEach(this::registerAlias);
    }

    private void registerAlias(@NotNull String alias) {
        new CommandTree(alias)
            .withRequirement(sender -> {
                if (!(sender instanceof Player player)) {
                    return false;
                }
                return hasAccess(player);
            })
            .then(
                new GreedyStringArgument("message")
                    .executesPlayer(info -> {
                        String message = Objects.requireNonNull(info.args().getUnchecked("message"));
                        sendMessage(
                            info.sender(),
                            ComponentMessage.componentMessage(message).get()
                        );
                    })
            )
            .executesPlayer(info -> {
                new PlayerData(info.sender()).setActiveChannel(this);
            })
            .register(plugin().namespace());
    }

    default boolean persistent() {
        return true;
    }

}
