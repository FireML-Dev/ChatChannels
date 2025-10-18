package uk.firedev.chatchannels.api;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public record Messaging(ChatChannel channel) {

    public Messaging(@NotNull ChatChannel channel) {
        this.channel = channel;
    }

    public void sendMessage(@NotNull Player sender, @NotNull Component sentMessage, @NotNull ComponentSingleMessage message) {
        Bukkit.getScheduler().runTask(ChatChannels.getInstance(), () -> {
            Optional<List<Player>> targetPlayers = handleRadius(sender);
            if (targetPlayers.isEmpty()) {
                return;
            }
            message.replace("{message}", sentMessage).send(sender, Bukkit.getConsoleSender());
            targetPlayers.get().forEach(player -> {
                // Don't send to sender
                if (player.equals(sender)) {
                    return;
                }
                // Check if the message should be sent
                if (!channel.shouldSendToTarget(sender, player)) {
                    return;
                }
                // Checks for mentions/pings
                Component messageContent = processPing(player, sentMessage);
                message.replace("{message}", messageContent).send(player);
            });
        });
    }

    public Optional<List<Player>> handleRadius(@NotNull Player sender) {
        long radius = channel.radius();
        // If the radius is 0 or less, we can just pass all online players
        if (radius < 0) {
            return Optional.of(List.copyOf(Bukkit.getOnlinePlayers()));
        }
        List<Player> players = sender.getNearbyEntities(radius, radius, radius).stream()
            .filter(entity -> entity instanceof Player)
            .map(entity -> (Player) entity)
            .toList();
        if (players.isEmpty()) {
            MessageConfig.getInstance().getNoNearbyPlayersMessage().send(sender);
            return Optional.empty();
        }
        return Optional.of(players);
    }

    private Component processPing(@NotNull Player player, @NotNull Component component) {
        if (!channel.enablePing() || channel.pingCooldown.hasCooldown(player.getUniqueId())) {
            return component;
        }
        ComponentSingleMessage message = ComponentMessage.componentMessage(component);
        String pingFormat = "@" + player.getName();
        if (message.containsString(pingFormat)) {
            message = message.replace(pingFormat, "<red>@" + player.getName());
            Sound pingSound = channel.pingSound();
            if (pingSound != null) {
                player.playSound(pingSound);
            }
            channel.pingCooldown.applyCooldown(player.getUniqueId(), Duration.ofSeconds(channel.pingCooldown()));
            return message.get();
        }
        return component;
    }

}
