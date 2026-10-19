package uk.firedev.chatchannels.registry;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.ChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.data.PlayerData;

@SuppressWarnings("deprecation") // We use the synchronous ChatEvent as we need the player's PDC.
public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {
        if (ChatChannelRegistry.getInstance().isEmpty()) {
            return;
        }
        ChatChannel channel = new PlayerData(event.getPlayer()).getActiveChannel();
        if (channel == null) {
            return;
        }
        event.setCancelled(true);
        channel.handleEvent(event);
    }

}
