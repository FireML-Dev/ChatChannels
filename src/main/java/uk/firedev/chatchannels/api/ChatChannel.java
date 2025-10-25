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
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.chatchannels.data.PlayerData;
import uk.firedev.daisylib.addons.requirement.Requirement;
import uk.firedev.daisylib.addons.requirement.RequirementData;
import uk.firedev.daisylib.command.CooldownHelper;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;

import java.util.Objects;

public interface ChatChannel {

    boolean isEnabled();

    @NotNull String name();

    @NotNull Plugin plugin();

    @NotNull ComponentSingleMessage display();

    boolean enablePing();

    @Nullable Sound pingSound();

    int pingCooldown();

    @NotNull Requirement accessRequirement();

    @NotNull ComponentSingleMessage format();

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

    boolean shouldSendToTarget(@NotNull Player player, @NotNull Player target);

    @Nullable Replacer replacer(@NotNull Player player);

    @NotNull CooldownHelper pingCooldownHandler();

    void reload();

    default boolean hasAccess(@NotNull Player player) {
        if (!isEnabled()) {
            return false;
        }
        return accessRequirement().meetsRequirements(
            new RequirementData().withPlayer(player)
        );
    }

    default void registerAlias(@NotNull String alias) {
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

}
