package xyz.ItzArad.baf.commands.nation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.ItzArad.baf.common.commands.NationCommand;
import xyz.ItzArad.baf.common.commands.TabCompletable;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.List;
import java.util.Optional;

public class NationInviteCommand implements NationCommand, TabCompletable {
    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.of(Permissions.PLAYER_INVITE);
    }

    @Override
    public boolean requiresNation() {
        return true;
    }

    @Override
    public String getName() {
        return "invite";
    }
    @Override
    public boolean publicCommand() {
        return false;
    }
    @Override
    public String getDescription() {
        return "invite another player into nation";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Invite";
    }

    @Override
    public String getUsage() {
        return "/nation invite <player>";
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
        String targetName = args[0];
        Player bukkitTargetPlayer = Bukkit.getPlayer(targetName);
        if(bukkitTargetPlayer == null){
            player.sendColorMessage("<red>There is no player with this name!");
            return true;
        }
        BAFPlayer target = BAFPlayer.of(bukkitTargetPlayer);
        if(target.equals(player)){
            player.sendColorMessage("<red>You cannot invite yourself!");
            return true;
        }
        if(target.getNation() != null){
            player.sendColorMessage("<red>The Player is already in a nation!");
            return true;
        }
        NationManager.sendInvite(target, player, player.getNation());
        player.sendColorMessage("<green>" + targetName + " <gray>has invited to the nation successfully!");
        return true;
    }

    @Override
    public List<String> onTabComplete(BAFPlayer player, String[] args) {
        if(args.length == 1){
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .sorted()
                    .toList();
        }
        return List.of();
    }
}
