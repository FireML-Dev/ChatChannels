package uk.firedev.chatchannels.configs;

import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import uk.firedev.daisylib.config.BasicConfig;

public class MainConfig extends BasicConfig {

    private static final MainConfig instance = new MainConfig();

    public MainConfig() {
        super("config.yml", "config.yml", ChatChannels.get());
    }

    public static @NonNull MainConfig getInstance() {
        return instance;
    }

    public @Nullable ChatChannel getInitialChannel() {
        return ChatChannelRegistry.getInstance().getChatChannel(getConfig().getString("initial-channel"));
    }
    
}
