package uk.firedev.chatchannels.api;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.data.PlayerData;
import uk.firedev.daisylib.command.CooldownHelper;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

public abstract class ChatChannel extends ConfigBase {

    protected final CooldownHelper pingCooldown = CooldownHelper.create();

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

    public @Nullable Sound pingSound() {
        return getSound("ping.sound");
    }

    public int pingCooldown() {
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
        new Messaging(this).sendMessage(sender, event, message);
    }

    public abstract boolean shouldSendToTarget(@NotNull Player player, @NotNull Player target);

    public boolean hasAccess(@NotNull Player player) {
        String access = accessPermission();
        return access == null || player.hasPermission(access);
    }

}
