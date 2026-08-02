package uk.firedev.chatchannels;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.commands.ChatCommand;
import uk.firedev.chatchannels.commands.MainCommand;
import uk.firedev.chatchannels.configs.MainConfig;
import uk.firedev.chatchannels.configs.MessageConfig;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import uk.firedev.daisylib.logging.Logging;

public final class ChatChannels extends JavaPlugin {

    private static ChatChannels INSTANCE;
    private static Logging LOGGING;

    private boolean loading = true;
    private boolean allowServerReload = false;

    public ChatChannels() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException(getClass().getName() + " has already been assigned!");
        }
        INSTANCE = this;
        LOGGING = Logging.logging(this);
    }

    public static @NonNull ChatChannels get() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException(ChatChannels.class.getSimpleName() + " has not been assigned!");
        }
        return INSTANCE;
    }

    public static @NonNull Logging getLogging() {
        if (LOGGING == null) {
            throw new UnsupportedOperationException(ChatChannels.class.getSimpleName() + " has not been assigned!");
        }
        return LOGGING;
    }

    @Override
    public void onLoad() {
        this.loading = true;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new StartupListener(), this);
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
        reloadServerForCommands();
        this.loading = false;
    }

    public void reloadServerForCommands() {
        if (allowServerReload && !loading) {
            getLogging().info("Reloading the server to register missing commands.");
            Bukkit.reloadData();
        }
    }

    // Listens for ServerLoadEvent to determine whether we can use Bukkit#reloadData.
    class StartupListener implements Listener {

        @EventHandler
        public void onLoad(ServerLoadEvent event) {
            if (event.getType() == ServerLoadEvent.LoadType.STARTUP) {
                allowServerReload = true;
                Bukkit.getScheduler().runTaskLater(
                    ChatChannels.INSTANCE,
                    () -> {
                        reloadServerForCommands();
                        HandlerList.unregisterAll(this);
                    },
                    5L
                );
            }
        }

    }

}
