package uk.firedev.chatchannels.registry;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import uk.firedev.chatchannels.data.PlayerData;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        if (ChatChannelRegistry.getInstance().isEmpty()) {
            return;
        }
        PlayerData data = new PlayerData(event.getPlayer());
        event.setCancelled(true);
        data.getActiveChannel().handleEvent(event);
    }

}
