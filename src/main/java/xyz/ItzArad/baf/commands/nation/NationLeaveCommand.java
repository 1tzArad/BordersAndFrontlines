package xyz.ItzArad.baf.commands.nation;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.ItzArad.baf.common.commands.NationCommand;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.Optional;

public class NationLeaveCommand implements NationCommand {
    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.empty();
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
        return "leave";
    }

    @Override
    public String getDescription() {
        return "leave the nation that you are in";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Leave";
    }

    @Override
    public String getUsage() {
        return "/nation leave";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        Nation nation = player.getNation();
        if(nation == null || !player.isInNation()){
            player.sendColorMessage("<red>You are not in any nations!");
            return true;
        }
        if(nation.getLeader().equals(player)){
            player.sendColorMessage("<red>You cannot leave the nation while you're the leader! disband the nation or promote someone else to leader!");
            return true;
        }
        Component message = Colors.color("<yellow><b>Are you sure?</b></yellow>\n" +
                "<gray>Do you really want to leave the nation ?</gray>\n\n")
                .append(
                Colors.executeCodeOnClick(
                        Colors.color("<red><b>Yea</b></red>"),
                        aud -> {
                            if (!(aud instanceof Player)) return;
                            if(!player.isInNation()){
                                player.sendColorMessage("<red>You are not in any nations!");
                                return;
                            }
                            nation.removePlayer(player);
                            nation.broadcast("<red>" + player.getName() + " <gray>has left the nation!");
                            player.sendColorMessage("<red>You've left the nation successfully!");
                        },
                        "<red>T-T</red>"
                )
        ).append(
                Colors.color("  ")
        ).append(
                Colors.executeCodeOnClick(
                        Colors.color("<green><b>No</b></green>"),
                        aud -> {
                            if (!(aud instanceof Player)) return;
                            if(!player.isInNation()){
                                player.sendColorMessage("<red>You are not in any nations!");
                                return;
                            }
                            player.sendColorMessage("<red>Hichi Nashod!");
                        },
                        "<green>^o^</green>"
                )
        );
        player.sendMessage(message);
        return true;
    }
}
