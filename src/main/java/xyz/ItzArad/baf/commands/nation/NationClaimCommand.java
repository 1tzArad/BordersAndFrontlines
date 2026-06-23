package xyz.ItzArad.baf.commands.nation;

import xyz.ItzArad.baf.common.commands.NationCommand;
import xyz.ItzArad.baf.common.commands.TabCompletable;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.List;
import java.util.Optional;

public class NationClaimCommand implements NationCommand, TabCompletable {
    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.of(Permissions.CAN_CLAIM);
    }

    @Override
    public boolean requiresNation() {
        return true;
    }
    @Override
    public boolean publicCommand() {
        return false;
    }
    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return "claim chunks";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Claim";
    }

    @Override
    public String getUsage() {
        return "/nation claim claim/toggle_auto";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        if(args.length < 1){
            player.sendColorMessage("<blue>Usage<aqua>: <blue>" + getUsage());
            return true;
        }
        switch (args[0]){
            case "this_chunk" -> {
                Nation nation = player.getNation();
                assert nation != null;
                boolean checksResult = NationManager.claimChecks(player.getChunk(), player);
                if(!checksResult) return true;
                nation.claim(player.getChunk());
                player.withdraw(NationManager.getClaimCost());
                player.sendColorActionBar("<green>Chunk claimed successfully!");
                return true;
            }
            case "toggle_auto" -> {
                NationManager.toggleAutoClaim(player);
                if(NationManager.isPlayerAutoClaimingToggle(player)) player.sendColorActionBar("<green>Auto Claiming has enabled!");
                else player.sendColorActionBar("<red>Auto Claiming has disabled!");
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(BAFPlayer player, String[] args) {
        if(args.length == 1){
            return List.of(
                    "this_chunk",
                    "toggle_auto"
            );
        }
        return List.of();
    }
}
