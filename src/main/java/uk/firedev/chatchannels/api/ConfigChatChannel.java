package uk.firedev.chatchannels.api;

import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.addons.requirement.Requirement;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;
import uk.firedev.daisylib.util.CooldownHelper;

import java.io.File;
import java.util.List;

/**
 * Used for fetching chat channels from the config files.
 */
public class ConfigChatChannel extends ConfigBase implements ChatChannel {

    protected final CooldownHelper pingCooldown = CooldownHelper.cooldownHelper();

    private final @NonNull String id;
    private final List<String> commandAliases;
    private final @NonNull Requirement accessRequirement;

    public ConfigChatChannel(@NonNull File file, @NonNull Plugin plugin) throws ChannelLoadException {
        super(file, null, plugin);
        init();
        this.id = checkId();
        this.commandAliases = getConfig().getStringList("commands");
        this.accessRequirement = new Requirement(getConfig().getConfigurationSection("requirements"), plugin);
    }

    public ConfigChatChannel(@NonNull String fileName, @NonNull String resourceName, @NonNull Plugin plugin) throws ChannelLoadException {
        super(fileName, resourceName, plugin);
        init();
        this.id = checkId();
        this.commandAliases = getConfig().getStringList("commands");
        this.accessRequirement = new Requirement(getConfig().getConfigurationSection("requirements"), plugin);
    }

    private @NonNull String checkId() throws ChannelLoadException {
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
    public @NonNull String name() {
        return id;
    }

    @Override
    public final @NonNull Plugin plugin() {
        return getPlugin();
    }

    @Override
    public @NonNull ComponentSingleMessage display() {
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
    public @NonNull CooldownHelper pingCooldownHandler() {
        return pingCooldown;
    }

    @Override
    public @NonNull Requirement accessRequirement() {
        return accessRequirement;
    }

    public @NonNull ComponentSingleMessage defaultFormat() {
        return ComponentMessage.componentMessage("<gray>[" + name() + "]</gray> <white>{name} ➻ {message}</white>");
    }

    @Override
    public @NonNull ComponentSingleMessage format() {
        ComponentMessage message = ComponentMessage.componentMessage(getMessageLoader(), "format");
        return message == null ? defaultFormat() : message.toSingleMessage();
    }

    @Override
    public boolean shouldSendToTarget(@NonNull Player player, @NonNull Player target) {
        return hasAccess(target);
    }

    @Nullable
    @Override
    public Replacer replacer(@NonNull Player player) {
        return null;
    }

    @Override
    public long radius() {
        return getConfig().getLong("radius", -1);
    }

    @Override
    public @NonNull List<String> aliases() {
        return commandAliases;
    }

    @Override
    public boolean persistent() {
        return false;
    }

}
