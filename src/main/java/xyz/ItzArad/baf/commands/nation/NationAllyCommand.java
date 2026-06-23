package xyz.ItzArad.baf.commands.nation;

import xyz.ItzArad.baf.common.commands.NationCommand;
import xyz.ItzArad.baf.common.commands.TabCompletable;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class NationAllyCommand implements NationCommand, TabCompletable {
    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.of(Permissions.MAKE_ALLY);
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
        return "ally";
    }

    @Override
    public String getDescription() {
        return "make an ally with a nation";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Ally";
    }

    @Override
    public String getUsage() {
        return "/nation ally <nation>";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        if(args.length < 1){
            player.sendColorMessage("<blue>" + getUsage());
            return true;
        }
        String targetNationName = args[0];
        Nation targetNation = NationManager.getNation(targetNationName);
        Nation nation = player.getNation();
        if(nation == null){
            player.sendColorMessage("<red>You are not in any nations!");
            return true;
        }
        if(targetNation == null){
            player.sendColorMessage("<red>This nation doesn't exists!");
            return true;
        }
        if(!nation.isLeader(player)){
            player.sendColorMessage("<red>To use this command you must be the leader of the nation!");
            return true;
        }
        if(!targetNation.getLeader().isOnline()){
            player.sendColorMessage("<red>Target Nations' leader is offline!");
            return true;
        }
        if(NationManager.areAlliance(nation, targetNation)){
            player.sendColorMessage("<red>You are already ally with this nation!");
            return true;
        }
        NationManager.sendAlliance(targetNation, nation, player);
        return true;
    }

    @Override
    public List<String> onTabComplete(BAFPlayer player, String[] args) {
        if(args.length == 1) return NationManager.getNationsName().stream()
                .filter(name -> !name.equals(Objects.requireNonNull(player.getNation()).getName()))
                .collect(Collectors.toList());
        return List.of();
    }
}
