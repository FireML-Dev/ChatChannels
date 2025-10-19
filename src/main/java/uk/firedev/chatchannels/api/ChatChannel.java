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
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.GreedyStringArgument;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;

import java.util.List;
import java.util.Objects;

public abstract class ChatChannel extends ConfigBase {

    protected final CooldownHelper pingCooldown = CooldownHelper.create();

    private final List<String> commandAliases;
    private @NotNull Requirement accessRequirement;

    public ChatChannel(@NotNull String fileName, @NotNull Plugin plugin) {
        super(fileName, fileName, plugin);
        // init performs a reload.
        init();
        this.commandAliases = getConfig().getStringList("commands");
        registerAliases();
    }

    public boolean isEnabled() {
        return getConfig().getBoolean("enabled", true);
    }

    @Override
    public void reload() {
        super.reload();
        // Cache the accessRequirement so we don't need to rebuild it every time we need it.
        this.accessRequirement = new Requirement(getConfig().getConfigurationSection("requirements"), plugin());
    }

    public abstract @NotNull String name();

    public final @NotNull Plugin plugin() {
        return getPlugin();
    }

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

    public @NotNull Requirement accessRequirement() {
        return accessRequirement;
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
            MessageConfig.getInstance().getNoAccessMessage().send(sender);
            new PlayerData(sender).resetActiveChannel();
            return;
        }
        sendMessage(sender, event.message());
    }

    public void sendMessage(@NotNull Player sender, @NotNull Component component) {
        ComponentSingleMessage message = format().parsePlaceholderAPI(sender)
            .replace("{name}", sender.name())
            .replace(replacer(sender));
        new Messaging(this).sendMessage(sender, component, message);
    }

    public abstract boolean shouldSendToTarget(@NotNull Player player, @NotNull Player target);

    public abstract @Nullable Replacer replacer(@NotNull Player player);

    public boolean hasAccess(@NotNull Player player) {
        if (!isEnabled()) {
            return false;
        }
        return accessRequirement().meetsRequirements(
            new RequirementData().withPlayer(player)
        );
    }

    private void registerAliases() {
        this.commandAliases.forEach(this::registerAlias);
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

}
