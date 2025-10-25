package uk.firedev.chatchannels.api;

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
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;

import java.util.List;

public abstract class ConfigChatChannel extends ConfigBase implements ChatChannel {

    protected final CooldownHelper pingCooldown = CooldownHelper.create();

    private final List<String> commandAliases;
    private @NotNull Requirement accessRequirement;

    public ConfigChatChannel(@NotNull String fileName, @NotNull Plugin plugin) {
        super(fileName, fileName, plugin);
        // init performs a reload.
        init();
        this.commandAliases = getConfig().getStringList("commands");
        registerAliases();
    }

    @Override
    public boolean isEnabled() {
        return getConfig().getBoolean("enabled", true);
    }

    @Override
    public void reload() {
        super.reload();
        // Cache the accessRequirement so we don't need to rebuild it every time we need it.
        this.accessRequirement = new Requirement(getConfig().getConfigurationSection("requirements"), plugin());
    }

    @Override
    public abstract @NotNull String name();

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

    public abstract @NotNull ComponentSingleMessage defaultFormat();

    @Override
    public @NotNull ComponentSingleMessage format() {
        ComponentMessage message = ComponentMessage.componentMessage(getMessageLoader(), "format");
        return message == null ? defaultFormat() : message.toSingleMessage();
    }

    @Override
    public long radius() {
        return getConfig().getLong("radius", -1);
    }

    private void registerAliases() {
        this.commandAliases.forEach(this::registerAlias);
    }

}
