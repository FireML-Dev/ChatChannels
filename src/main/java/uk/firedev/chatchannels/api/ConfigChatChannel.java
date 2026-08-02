package uk.firedev.chatchannels.api;

import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.addons.requirement.RequirementChecker;
import uk.firedev.daisylib.config.BasicConfig;
import uk.firedev.daisylib.config.serializer.SoundSerializer;
import uk.firedev.daisylib.messages.message.ComponentMessage;
import uk.firedev.daisylib.messages.message.ComponentSingleMessage;
import uk.firedev.daisylib.messages.replacer.Replacer;
import uk.firedev.daisylib.utils.CooldownHelper;

import java.io.File;
import java.util.List;

/**
 * Used for fetching chat channels from the config files.
 */
public class ConfigChatChannel extends BasicConfig implements ChatChannel {

    protected final CooldownHelper pingCooldown = CooldownHelper.cooldownHelper();

    private final Plugin plugin;
    private final @NonNull String id;
    private final List<String> commandAliases;
    private final @NonNull RequirementChecker accessRequirement;
    private final boolean persistent;

    public ConfigChatChannel(@NonNull File file, @NonNull Plugin plugin, boolean persistent) throws ChannelLoadException {
        super(file, plugin);
        this.plugin = plugin;
        this.id = checkId();
        this.commandAliases = getConfig().getStringList("commands");
        this.accessRequirement = new RequirementChecker(getConfig().getConfigurationSection("requirements"));
        this.persistent = persistent;
    }

    public ConfigChatChannel(@NonNull String fileName, @NonNull String resourceName, @NonNull Plugin plugin, boolean persistent) throws ChannelLoadException {
        super(fileName, resourceName, plugin);
        this.plugin = plugin;
        this.id = checkId();
        this.commandAliases = getConfig().getStringList("commands");
        this.accessRequirement = new RequirementChecker(getConfig().getConfigurationSection("requirements"));
        this.persistent = persistent;
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
        return this.plugin;
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
        String sound = getConfig().getString("ping.sound");
        return SoundSerializer.get().deserialize(sound);
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
    public @NonNull RequirementChecker accessRequirement() {
        return accessRequirement;
    }

    public @NonNull ComponentSingleMessage defaultFormat() {
        return ComponentMessage.componentMessage("<gray>[" + name() + "]</gray> <white>{name} ➻ {message}</white>");
    }

    @Override
    public @NonNull ComponentSingleMessage format() {
        return getComponentMessage("format", defaultFormat()).toSingleMessage();
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
    public final boolean persistent() {
        return this.persistent;
    }

    @Override
    public boolean copyDefaults() {
        return true;
    }

}
