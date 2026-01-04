package uk.firedev.chatchannels.configs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import uk.firedev.daisylib.config.ConfigBase;

public class MainConfig extends ConfigBase {

    private static final MainConfig instance = new MainConfig();

    public MainConfig() {
        super("config.yml", "config.yml", ChatChannels.getInstance());
    }

    public static @NonNull MainConfig getInstance() {
        return instance;
    }

    public @Nullable ChatChannel getInitialChannel() {
        return ChatChannelRegistry.getInstance().getChatChannel(getConfig().getString("initial-channel"));
    }
    
}
