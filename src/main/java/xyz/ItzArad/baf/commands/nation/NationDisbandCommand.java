package xyz.ItzArad.baf.commands.nation;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.ItzArad.baf.common.commands.NationCommand;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.Optional;

public class NationDisbandCommand implements NationCommand {
    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.of(Permissions.NATION_SETTINGS_HIGH);
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
        return "disband";
    }

    @Override
    public String getDescription() {
        return "disband the nation";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Disband";
    }

    @Override
    public String getUsage() {
        return "/nation disband";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        Nation nation = player.getNation();
        if(nation == null) return true;
        if(!nation.isLeader(player)){
            player.sendColorMessage("<red>Only nations' leader can execute this command!");
            return true;
        }
        Component message = Colors.color("<yellow><b>Are you sure?</b></yellow>\n" +
                        "<gray>Do you really want to disband the nation ?</gray>\n\n")
                .append(
                        Colors.executeCodeOnClick(
                                Colors.color("<red><b>Yea</b></red>"),
                                aud -> {
                                    if (!(aud instanceof Player)) return;
                                    if(!player.isInNation()){
                                        player.sendColorMessage("<red>You are not in any nations!");
                                        return;
                                    }
                                    nation.broadcast("<gray>Nation has <red>Disbanded <gray>By the <red>Leader<gray>!");
                                    player.sendColorMessage("<red>You've disbanded the nation successfully!");
                                    nation.disband();
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
