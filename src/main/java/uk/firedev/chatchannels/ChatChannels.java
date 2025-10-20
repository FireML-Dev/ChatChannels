package uk.firedev.chatchannels;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.chatchannels.commands.ChatCommand;
import uk.firedev.chatchannels.commands.MainCommand;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;

public final class ChatChannels extends JavaPlugin {

    private static ChatChannels INSTANCE;

    public ChatChannels() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException(getClass().getName() + " has already been assigned!");
        }
        INSTANCE = this;
    }

    public static @NotNull ChatChannels getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException(ChatChannels.class.getSimpleName() + " has not been assigned!");
        }
        return INSTANCE;
    }

    @Override
    public void onLoad() {}

    @Override
    public void onEnable() {
        MessageConfig.getInstance().init();
        MainCommand.getCommand().register(this);
        ChatChannelRegistry.getInstance().init(this);
        ChatCommand.getCommand().register(this);
    }

    @Override
    public void onDisable() {}

    public void reload() {
        MessageConfig.getInstance().reload();
        ChatChannelRegistry.getInstance().reload();
        Bukkit.getOnlinePlayers().forEach(CommandAPI::updateRequirements);
    }

}
