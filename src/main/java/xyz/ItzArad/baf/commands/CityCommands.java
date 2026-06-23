package xyz.ItzArad.baf.commands;

import lombok.Getter;
import xyz.ItzArad.baf.common.commands.CityCommand;
import xyz.ItzArad.baf.common.commands.NationCommand;
import xyz.ItzArad.baf.common.commands.TabCompletable;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.City;
import xyz.ItzArad.baf.models.CityRank;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import javax.annotation.Nullable;
import java.util.*;

public class CityCommands implements NationCommand, TabCompletable {
    @Getter
    private final Map<String, CityCommand> cityCommandMap = new HashMap<>();


    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.empty();
    }
    @Override
    public boolean publicCommand() {
        return false;
    }
    @Override
    public boolean requiresNation() {
        return true;
    }

    @Override
    public String getName() {
        return "city";
    }

    @Override
    public String getDescription() {
        return "commands that are connected to cities!";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.City";
    }

    @Override
    public String getUsage() {
        return "/nation city [command]";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        if(args.length > 1){
            player.sendColorMessage("City Commands:");
            if(getCityCommandMap().isEmpty()){
                player.sendColorMessage("<yellow>There is no city commands that registered!");
                return true;
            }else {
                for (CityCommand cmd : getCityCommandMap().values()) {
                    player.sendColorMessage("<yellow>" + cmd.getName() + "<gold>: <yellow>" + cmd.getDescription() + " <gold>- <yellow>Usage<gold>: " + cmd.getUsage());
                }
            }
            return true;
        }
        String cmdName = args[0];
        CityCommand cityCommand = getCommand(cmdName);
        if(cityCommand == null){
            player.sendColorMessage("<red>Command not found!");
            return true;
        }
        if(cityCommand.shouldBeInCityChunk() && !NationManager.getCityChunks().containsKey(player.getChunk())){
            player.sendColorMessage("<red>To use this command you must be in a city chunk!");
            return true;
        }
        if(cityCommand.isMayorOnly() && !(NationManager.getCityChunks().get(player.getChunk()).getMayor().equals(player))){
            player.sendColorMessage("<red>To use this command you must be the mayor of the city!");
            return true;
        }

        if(!(NationManager.getCityChunks().get(player.getChunk()).getPlayers().containsKey(player.uuid()))){
            player.sendColorMessage("<red>To use city commands you must be in the city that you live in!");
            return true;
        }
        Optional<CityRank> requiredCityRank = cityCommand.requiredCityRank(player);
        if(requiredCityRank.isPresent() && !player.getCityRank().equals(requiredCityRank)){
            player.sendColorMessage("<red>To use this command you must have " + requiredCityRank + " city rank or be a mayor!");
            return true;
        }
        City playerCity = player.getCity();
        if(playerCity == null){
            player.sendColorMessage("<red>To use this command you must be in a city!");
            return true;
        }
        cityCommand.getCity(playerCity);
        cityCommand.execute(player, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public List<String> onTabComplete(BAFPlayer player, String[] args) {
        if(args.length == 1){
            return getCityCommandMap().values().stream()
                    .filter(cmd -> cmd.getName().startsWith(args[0].toLowerCase()))
                    .filter(cmd -> player.hasPermission(cmd.getPermission()))
                    .filter(cmd -> {
                        if(cmd.isMayorOnly() && !(NationManager.getCityChunks().get(player.getChunk()).getMayor().equals(player))) return false;
                        if(cmd.shouldBeInCityChunk() && cmd.shouldBeInCityChunk() && !NationManager.getCityChunks().containsKey(player.getChunk())) return false;

                        return true;
                    })
                    .map(CityCommand::getName)
                    .sorted()
                    .toList();
        }
        return List.of();
    }

    private void registerCommands(CityCommand cmd){
        getCityCommandMap().put(cmd.getName().toLowerCase(), cmd);
    }

    @Nullable
    private CityCommand getCommand(String name){
        return getCityCommandMap().get(name);
    }

    // constructor
    public CityCommands(){

    }
}
