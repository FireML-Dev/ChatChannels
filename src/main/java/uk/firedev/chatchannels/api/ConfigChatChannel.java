package uk.firedev.chatchannels.api;

import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.addons.requirement.Requirement;
import uk.firedev.daisylib.command.CooldownHelper;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Used for fetching chat channels from the channels config folder.
 */
@ApiStatus.Internal
public class ConfigChatChannel extends ConfigBase implements ChatChannel {

    protected final CooldownHelper pingCooldown = CooldownHelper.create();

    private final @NotNull String id;
    private final List<String> commandAliases;
    private @NotNull Requirement accessRequirement = new Requirement(plugin());

    public ConfigChatChannel(@NotNull File file, @NotNull Plugin plugin) throws ChannelLoadException {
        super(file, null, plugin);
        // init performs a reload.
        init();
        this.id = checkId();
        this.commandAliases = getConfig().getStringList("commands");
    }

    private @NotNull String checkId() throws ChannelLoadException {
        String id = getConfig().getString("id");
        if (id == null) {
            throw new ChannelLoadException("Missing id.");
        }
        return id;
    }

    @Override
    public boolean isEnabled() {
        return getConfig().getBoolean("enabled", true);
    }

    @Override
    public @NotNull String name() {
        return id;
    }

    @Override
    public final @NotNull Plugin plugin() {
        return getPlugin();
    }

    @Override
    public @NotNull ComponentSingleMessage display() {
        return ComponentMessage.componentMessage(getConfig().getString("display", name()));
    }

    @Override
    public boolean enablePing() {
        return getConfig().getBoolean("ping.enable", true);
    }

    @Override
    public @Nullable Sound pingSound() {
        return getSound("ping.sound");
    }

    @Override
    public int pingCooldown() {
        return getConfig().getInt("ping.cooldown");
    }

    @Override
    public @NotNull CooldownHelper pingCooldownHandler() {
        return pingCooldown;
    }

    @Override
    public @NotNull Requirement accessRequirement() {
        return accessRequirement;
    }

    public @NotNull ComponentSingleMessage defaultFormat() {
        return ComponentMessage.componentMessage("<gray>[" + name() + "]</gray> <white>{name} ➻ {message}</white>");
    }

    @Override
    public @NotNull ComponentSingleMessage format() {
        ComponentMessage message = ComponentMessage.componentMessage(getMessageLoader(), "format");
        return message == null ? defaultFormat() : message.toSingleMessage();
    }

    @Override
    public boolean shouldSendToTarget(@NotNull Player player, @NotNull Player target) {
        return hasAccess(target);
    }

    @Nullable
    @Override
    public Replacer replacer(@NotNull Player player) {
        return null;
    }

    @Override
    public long radius() {
        return getConfig().getLong("radius", -1);
    }

    @Override
    public @NotNull List<String> aliases() {
        return commandAliases;
    }

    @Override
    public boolean persistent() {
        return false;
    }

}
