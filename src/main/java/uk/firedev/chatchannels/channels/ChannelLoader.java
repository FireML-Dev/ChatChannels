package uk.firedev.chatchannels.channels;

import org.jetbrains.annotations.NotNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChannelLoadException;
import uk.firedev.chatchannels.api.ConfigChatChannel;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.utils.FileUtils;

import java.io.File;
import java.util.List;

public class ChannelLoader {

    private final ChatChannelRegistry registry;

    public ChannelLoader(ChatChannelRegistry registry) {
        this.registry = registry;
    }

    public void loadChannels() {
        File directory = new File(ChatChannels.getInstance().getDataFolder(), "channels");
        // Always do this first as it checks if the directory exists.
        loadDefaultFiles(directory);
        regenExampleFile(directory);
        List<File> files = FileUtils.getFilesInDirectory(directory, true, true);
        files.forEach(file -> {
            ConfigChatChannel channel;
            try {
                channel = new ConfigChatChannel(file, ChatChannels.getInstance());
            } catch (ChannelLoadException exception) {
                Loggers.warn(ChatChannels.getInstance().getComponentLogger(), "Failed to load channel " + file.getName(), exception);
                return;
            }
            registry.register(channel);
        });
    }

    private void loadDefaultFiles(@NotNull File directory) {
        if (directory.exists()) {
            return;
        }
        FileUtils.loadFile(directory, "global.yml", "channels/global.yml", ChatChannels.getInstance());
        FileUtils.loadFile(directory, "local.yml", "channels/local.yml", ChatChannels.getInstance());
        Loggers.info(ChatChannels.getInstance().getComponentLogger(), "Loaded default channel configs.");
    }

    private void regenExampleFile(@NotNull File directory) {
        FileUtils.loadFile(directory, "_example.yml", "channels/_example.yml", ChatChannels.getInstance());
    }

}
