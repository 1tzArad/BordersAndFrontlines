package xyz.ItzArad.baf.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.Set;

public class NationChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event){
        if(event.isCancelled()) return;
        Player player = event.getPlayer();
        BAFPlayer BAFPlayer = BAFPlayer.of(player);
        Nation nation = BAFPlayer.getNation();
        if(nation == null) return;
        if(!NationManager.isNationChatToggle(BAFPlayer)) return;
        event.setCancelled(true);
        Component msg = event.message();
        String formatted = NationManager.getNationChatFormat(BAFPlayer, msg);
        Set<Audience> viewers = event.viewers();
        for (Audience viewer : viewers){
            if(!(viewer instanceof Player player1)) continue;
            BAFPlayer BAFPlayer1 = BAFPlayer.of(player1);
            if(!BAFPlayer1.isInNation()) continue;
            if(BAFPlayer1.getNation() != nation) continue;
            BAFPlayer1.sendColorMessage(formatted);
        }
    }
}
