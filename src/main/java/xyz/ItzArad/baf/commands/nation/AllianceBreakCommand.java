package xyz.ItzArad.baf.commands.nation;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.ItzArad.baf.common.commands.NationCommand;
import xyz.ItzArad.baf.common.commands.TabCompletable;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class AllianceBreakCommand implements NationCommand, TabCompletable {
    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.of(
                Permissions.BREAK_ALLY
        );
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
        return "break_ally";
    }

    @Override
    public String getDescription() {
        return "Break alliance with a nation";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Break_Ally";
    }

    @Override
    public String getUsage() {
        return "/nation break_ally <nation>";
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

        AtomicBoolean isUsed = new AtomicBoolean(false);
        String nationName = args[0];
        Nation targetNation = NationManager.getNation(nationName);
        Nation nation = player.getNation();
        if(targetNation == null){
            player.sendColorMessage("<red>Invalid nation!");
            return true;
        }
        if(nation == null){
            player.sendColorMessage("<red>You're not in any nation!!");
            return true;
        }
        boolean checkResults = checks(nation, targetNation, player);
        if(!checkResults) return true;

        Component message = Colors.color("<yellow><b>Are you sure?</b></yellow>\n" +
                        "<gray>Do you really want to break your alliance with " + targetNation.getName() + " ?</gray>\n\n")
                .append(
                        Colors.executeCodeOnClick(
                                Colors.color("<red><b>Yea</b></red>"),
                                aud -> {
                                    if (!(aud instanceof Player)) return;
                                    if (!isUsed.compareAndSet(false, true)) return;
                                    if(!checks(nation, targetNation, player)) return;
                                    NationManager.breakAlliance(nation, targetNation);
                                    player.sendColorMessage("<red>You've successfully broken alliance with " + targetNation.getName());
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
                                    if (!isUsed.compareAndSet(false, true)) return;
                                    if(!checks(nation, targetNation, player)) return;
                                    player.sendColorMessage("<green>You've successfully canceled alliance cancellation!");
                                },
                                "<green>^o^</green>"
                        )
                );
        return true;
    }

    @Override
    public List<String> onTabComplete(BAFPlayer player, String[] args) {
        if(args.length == 1){
            Nation nation = player.getNation();
            if(nation == null){
                return List.of();
            }
            return nation.getAllies().stream()
                    .map(NationManager::getNation)
                    .filter(Objects::nonNull)
                    .map(Nation::getName)
                    .toList();
        }
        return List.of();
    }

    private boolean checks(Nation nation, Nation targetNation, BAFPlayer player){
        if(!player.isInNation()) {
            player.sendColorMessage("<red>You are not in any nation");
            return false;
        }
        if(!targetNation.isAllyWith(nation)){
            player.sendColorMessage("<red>You're not in alliance with " + targetNation.getName() + "!");
            return false;
        }
        return true;
    }
}
