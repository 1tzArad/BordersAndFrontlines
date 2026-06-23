package xyz.ItzArad.baf.listeners;

import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.models.BAFChunk;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.time.Duration;
import java.util.Objects;

public class AutoClaimListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        Player p = event.getPlayer();
        BAFPlayer player = BAFPlayer.of(p);
        Nation nation = player.getNation();
        if (nation == null) return;
        if (!NationManager.isPlayerAutoClaimingToggle(player)) return;
        if (from.getChunk().equals(to.getChunk())) return;
        BAFChunk chunk = BAFChunk.of(to.getChunk());
        boolean checksResult = NationManager.claimChecks(chunk, player);
        if(!checksResult) return;
        nation.claim(chunk);
        player.withdraw(NationManager.getClaimCost());
        player.sendColorActionBar("<green>Chunk <yellow>" + chunk.getChunk().getX() + "<gold>,<yellow>" + chunk.getChunk().getZ() + "<green> claimed successfully! <dark_red>(<red>-<dark_red>" + NationManager.getClaimCost() + "<red>$<dark_red>)");
    }

    @EventHandler
    public void NationClaimEnter(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        BAFPlayer player = BAFPlayer.of(event.getPlayer());
        if (from.getChunk() == to.getChunk()) return;
        if (!BAFChunk.of(from.getChunk()).isClaimed() &&
                BAFChunk.of(to.getChunk()).isClaimed()) {
            BAFChunk toChunk = BAFChunk.of(to.getChunk());
            Title title = Title.title(
                    Colors.color("<" + Objects.requireNonNull(toChunk.getNation()).getColor() + ">" + "You've Entered <bold>" + toChunk.getNation().getName()),
                    Colors.color("<gray>Leader<dark_gray>: <gray>" + toChunk.getNation().getLeader().getName()),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(1))
            );
            player.getPlayer().showTitle(title);
        }
        ;
        if (BAFChunk.of(from.getChunk()).isClaimed() &&
                !BAFChunk.of(to.getChunk()).isClaimed()) {
            BAFChunk fromChunk = BAFChunk.of(from.getChunk());
            Title title = Title.title(
                    Colors.color("<green>Wildness"),
                    Colors.color("<white>You've left " + Objects.requireNonNull(fromChunk.getNation()).getName() + "'s territory!"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(1))
            );
            player.getPlayer().showTitle(title);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BAFPlayer player = BAFPlayer.of(event.getPlayer());
        if (!NationManager.isPlayerAutoClaimingToggle(player)) return;
        NationManager.getAutoClaimPlayersSet().remove(player);
    }


}
