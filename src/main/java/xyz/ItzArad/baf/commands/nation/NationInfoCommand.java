package xyz.ItzArad.baf.commands.nation;

import net.kyori.adventure.text.Component;
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
import java.util.stream.Collectors;

public class NationInfoCommand implements NationCommand, TabCompletable {
    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.empty();
    }

    @Override
    public boolean requiresNation() {
        return false;
    }
    @Override
    public boolean publicCommand() {
        return true;
    }
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "see nation information";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Info";
    }

    @Override
    public String getUsage() {
        return "/nation info [nation]";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        Nation nation;
        if(args.length < 1){
            nation = player.getNation();
            if(nation == null){
                player.sendColorMessage("<red>you are not in any nation!");
                return true;
            }
        }else{
            nation = NationManager.getNation(args[0]);
            if(nation == null){
                player.sendColorMessage("<red>This nation doesn't exists!");
                return true;
            }
        }
        List<String> alliesList = nation.getAllies().stream()
                .map(NationManager::getNation)
                .filter(Objects::nonNull)
                .map(Nation::getName)
                .toList();

        String alliesText = alliesList.isEmpty()
                ? "<red>This nation is not ally with any nations!"
                : String.join(", ", alliesList);

        Component name = Colors.color("<gold>Name: <yellow>" + nation.getName() + "\n");
        Component leader = Colors.color("<gold>Leader: <yellow>" + nation.getLeader().getName() + "\n");
        Component claimedChunks = Colors.color("<gold>ClaimedChunks Size: <yellow>" + nation.getClaimedChunks().size() + "\n");
        Component playersSize = Colors.color("<gold>Players Size: <yellow>" + nation.getNationPlayers().size() + "\n");
        Component players = Colors.color("<gold>Players: <yellow>" + NationManager.getNationPlayer(nation).stream()
                .map(BAFPlayer::getName)
                .collect(Collectors.joining("<gold>, <yellow>")) + "\n");
        Component vault = Colors.color("<gold>Vault Balance: <yellow>" + nation.getVault_balance() + "\n");
        Component allies = Colors.color("<gold>Allies: <yellow>" + alliesText);

        Component message = name.append(leader.append(claimedChunks.append(playersSize.append(players.append(vault.append(allies))))));
        player.sendMessage(message);
        return true;
    }

    @Override
    public List<String> onTabComplete(BAFPlayer player, String[] args) {
            if(args.length == 1) return NationManager.getNationsName();
            return List.of();
    }
}
