package uk.firedev.chatchannels.channels;

import org.bukkit.util.FileUtil;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.ChatChannels;
import uk.firedev.chatchannels.api.ChannelLoadException;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.api.ConfigChatChannel;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import uk.firedev.daisylib.utils.CommonUtils;
import uk.firedev.daisylib.utils.FileUtils;

import java.io.File;
import java.util.List;

public class ChannelLoader {

    private final ChatChannelRegistry registry;

    public ChannelLoader(ChatChannelRegistry registry) {
        this.registry = registry;
    }

    public void loadChannels() {
        File directory = new File(ChatChannels.get().getDataFolder(), "channels");
        // Always do this first as it checks if the directory exists.
        loadDefaultFiles(directory);
        regenExampleFile(directory);
        List<File> files = FileUtils.getYamlFilesInDirectory(directory, true, true);
        files.forEach(file -> {
            ConfigChatChannel channel;
            try {
                channel = new ConfigChatChannel(file, ChatChannels.get(), false);
            } catch (ChannelLoadException exception) {
                ChatChannels.getLogging().warn("Failed to load channel " + file.getName(), exception);
                return;
            }
            registry.register(channel);
        });
    }

    private void loadDefaultFiles(@NonNull File directory) {
        if (directory.exists()) {
            return;
        }
        FileUtils.loadFile(directory, "global.yml", "channels/global.yml", ChatChannels.get());
        FileUtils.loadFile(directory, "local.yml", "channels/local.yml", ChatChannels.get());
        ChatChannels.getLogging().info("Loaded default channel configs.");
    }

    private void regenExampleFile(@NonNull File directory) {
        File file = new File(ChatChannels.get().getDataFolder(), "_example.yml");
        if (file.exists()) {
            file.delete();
        }
        FileUtils.loadFile(directory, "_example.yml", "channels/_example.yml", ChatChannels.get());
    }

}
