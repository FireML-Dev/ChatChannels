package uk.firedev.chatchannels.api;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.chatchannels.data.PlayerData;
import uk.firedev.daisylib.command.CooldownHelper;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

import java.time.Duration;
import java.util.List;

public abstract class ChatChannel extends ConfigBase {

    private final CooldownHelper pingCooldown = CooldownHelper.create();

    public ChatChannel(@NotNull String fileName) {
        super(fileName, fileName, ChatChannels.getInstance());
        init();
    }

    public boolean isEnabled() {
        return getConfig().getBoolean("enabled", true);
    }

    public abstract @NotNull String name();

    public @NotNull ComponentSingleMessage display() {
        return ComponentMessage.componentMessage(getConfig().getString("display", name()));
    }

    public boolean enablePing() {
        return getConfig().getBoolean("ping.enable", true);
    }

    public @Nullable Sound getPingSound() {
        return getSound("ping.sound");
    }

    public int getPingCooldown() {
        return getConfig().getInt("ping.cooldown");
    }

    public @Nullable String accessPermission() {
        return getConfig().getString("access-permission");
    }

    public abstract @NotNull ComponentSingleMessage defaultFormat();

    public @NotNull ComponentSingleMessage format() {
        ComponentMessage message = ComponentMessage.componentMessage(getMessageLoader(), "format");
        return message == null ? defaultFormat() : message.toSingleMessage();
    }

    public long radius() {
        return getConfig().getLong("radius", -1);
    }

    public void handleEvent(@NotNull AsyncChatEvent event) {
        Player sender = event.getPlayer();
        if (!hasAccess(sender)) {
            new PlayerData(sender).resetActiveChannel();
            event.setCancelled(false);
            return;
        }
        ComponentSingleMessage message = format().parsePlaceholderAPI(sender)
            .replace("{name}", sender.name());
        List<Entity> entitiesWithinRadius = getEntitiesWithinRadius(sender);
        if (radius() > 0 && entitiesWithinRadius.isEmpty()) {
            MessageConfig.getInstance().getNoNearbyPlayersMessage().send(sender);
            return;
        }
        message.replace("{message}", event.message()).send(Bukkit.getConsoleSender());
        Bukkit.getOnlinePlayers().forEach(player -> {
            // Handle radius
            if (!entitiesWithinRadius.isEmpty() && !entitiesWithinRadius.contains(player)) {
                return;
            }
            // Check if the message should be sent
            if (!shouldSendToTarget(sender, player)) {
                return;
            }
            // Checks for mentions
            Component messageContent = retrieveMessage(player, event.message());
            message.replace("{message}", messageContent).send(player);
        });
    }

    private Component retrieveMessage(@NotNull Player player, @NotNull Component component) {
        if (!enablePing() || pingCooldown.hasCooldown(player.getUniqueId())) {
            return component;
        }
        ComponentSingleMessage message = ComponentMessage.componentMessage(component);
        String pingFormat = "@" + player.getName();
        if (message.containsString(pingFormat)) {
            message = message.replace(pingFormat, "<red>@" + player.getName());
            Sound pingSound = getPingSound();
            if (pingSound != null) {
                player.playSound(pingSound);
            }
            pingCooldown.applyCooldown(player.getUniqueId(), Duration.ofSeconds(getPingCooldown()));
            return message.get();
        }
        return component;
    }

    public abstract boolean shouldSendToTarget(@NotNull Player player, @NotNull Player target);

    public boolean hasAccess(@NotNull Player player) {
        String access = accessPermission();
        return access == null || player.hasPermission(access);
    }

    public List<Entity> getEntitiesWithinRadius(@NotNull Player player) {
        long radius = radius();
        if (radius < 0) {
            return List.of();
        }
        return player.getNearbyEntities(radius, radius, radius);
    }

}
