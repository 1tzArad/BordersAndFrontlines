package xyz.ItzArad.baf.listeners;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.City;
import xyz.ItzArad.baf.models.CityCore;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.models.BAFChunk;
import xyz.ItzArad.bafLibs.models.BAFPlayer;


public class CityCoreListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if(!isCore(block, BAFPlayer.of(player))) return;
        BAFPlayer.of(player).sendColorMessage("<red>You cannot break a city core!");
        event.setCancelled(true);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        BAFChunk chunk = BAFChunk.of(player.getChunk());
        if(!event.getAction().isRightClick()) return;

        if(!isCore(event.getClickedBlock(), BAFPlayer.of(player))) return;
        event.setCancelled(true);

        City city = NationManager.getCityByChunk(chunk);
        Nation nation = NationManager.getNationByCity(city);
        if(nation == null) return;

        Gui.gui().title(Colors.color("<white>七七七七七七七七\uE566")).rows(3).create().open(player);
    }

    private boolean isCore(Block block, BAFPlayer player){
        if(block == null) return false;
        if(!block.getType().equals(CityCore.getMaterial())) return false;
        BAFChunk chunk = BAFChunk.of(player.getChunk().getChunk());
        City city = NationManager.getCityByChunk(chunk);
        if(city == null) return false;
        CityCore cityCore = city.getCore();
        if(!(block.getX() == cityCore.getX() && block.getY() == cityCore.getY() && block.getZ() == cityCore.getZ() && block.getWorld().getName().equals(cityCore.getWorldName()))) return false;
        return true;
    }
}
