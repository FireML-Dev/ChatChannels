package uk.firedev.chatchannels;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.commands.ChatCommand;
import uk.firedev.chatchannels.commands.MainCommand;
import uk.firedev.chatchannels.configs.MainConfig;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;

public final class ChatChannels extends JavaPlugin {

    private static ChatChannels INSTANCE;

    private boolean loading = true;

    public ChatChannels() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException(getClass().getName() + " has already been assigned!");
        }
        INSTANCE = this;
    }

    public static @NonNull ChatChannels getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException(ChatChannels.class.getSimpleName() + " has not been assigned!");
        }
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        this.loading = true;
    }

    @Override
    public void onEnable() {
        ChatChannelRegistry.getInstance().init(this);
        // Do this after, as we need the channel registry to be full.
        registerCommands();
        this.loading = false;
    }

    @Override
    public void onDisable() {}

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            Commands registrar = commands.registrar();

            registrar.register(MainCommand.get());
            registrar.register(ChatCommand.get());
            ChatChannelRegistry.getInstance().getRegistry()
                .values()
                .forEach(channel ->
                    channel.registerAliases(registrar)
                );
        });
    }

    public void reload() {
        this.loading = true;
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        ChatChannelRegistry.getInstance().reload();
        Bukkit.reloadData();
        this.loading = false;
    }

    public boolean isLoading() {
        return this.loading;
    }

}
