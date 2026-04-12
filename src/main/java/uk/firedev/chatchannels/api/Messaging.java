package uk.firedev.chatchannels.api;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.events.ChatChannelsChatEvent;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

public record Messaging(@NonNull ChatChannel channel) {

    public void sendMessage(@NonNull Player sender, @NonNull Component sentMessage, @NonNull ComponentSingleMessage message) {
        Bukkit.getScheduler().runTask(ChatChannels.getInstance(), () -> {
            ChatChannelsChatEvent ccce = new ChatChannelsChatEvent(channel, sender, sentMessage);
            // Event cancelled
            if (!ccce.callEvent()) {
                return;
            }

            Component hand = sender.getInventory().getItemInMainHand().displayName();
            ComponentSingleMessage sent = ComponentMessage.componentMessage(ccce.message())
                .replace("[i]", hand)
                .replace("[item]", hand);

            handleRadius(sender).stream()
                .filter(player -> channel.shouldSendToTarget(sender, player))
                .forEach(player -> {
                    Component msg = message.replace("{message}", processPing(player, sent)).get();
                    player.sendMessage(msg);
                });
        });
    }

    private Collection<? extends Player> handleRadius(@NonNull Player sender) {
        long radius = channel.radius();
        // If the radius is 0 or less, we can just pass all online players
        if (radius <= 0) {
            return Bukkit.getOnlinePlayers();
        }
        List<Player> players = sender.getNearbyEntities(radius, radius, radius).stream()
            .filter(entity -> entity instanceof Player)
            .map(entity -> (Player) entity)
            .toList();
        if (players.isEmpty()) {
            MessageConfig.getInstance().getNoNearbyPlayersMessage().send(sender);
            return List.of();
        }
        return players;
    }

    private Component processPing(@NonNull Player player, @NonNull ComponentSingleMessage message) {
        if (!channel.enablePing() || channel.pingCooldownHandler().has(player.getUniqueId())) {
            return message.get();
        }
        String pingFormat = "@" + player.getName();
        if (message.containsString(pingFormat)) {
            message = message.replace(pingFormat, "<red>@" + player.getName());
            Sound pingSound = channel.pingSound();
            if (pingSound != null) {
                player.playSound(pingSound);
            }
            channel.pingCooldownHandler().apply(player.getUniqueId(), Duration.ofSeconds(channel.pingCooldown()));
        }
        return message.get();
    }

}
