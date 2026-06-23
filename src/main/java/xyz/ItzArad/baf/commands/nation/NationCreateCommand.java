package xyz.ItzArad.baf.commands.nation;

import xyz.ItzArad.baf.common.commands.NationCreationSessionCommand;
import xyz.ItzArad.baf.common.commands.TabCompletable;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.Ideologies;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NationCreateCommand implements NationCreationSessionCommand, TabCompletable {
    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.empty();
    }

    @Override
    public boolean requiresNation() {
        return false;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "create a nation";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Create";
    }

    @Override
    public String getUsage() {
        return "/nation create <name> <ideology> [#colorCode]";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
    @Override
    public boolean publicCommand() {
        return false;
    }
    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        if(args.length < 2){
            player.sendMessage("<blue>", getUsage());
            return true;
        }
        String name = args[0];
        String ideologyArg = args[1];
        String color;
        if (args.length >= 3) {
            color = args[2];
        } else {
            color = Colors.generateRandomHexCodeColor();
        }
        if(!(NationManager.isValidWorld(player.getChunk().getBukkitWorld().getName()))){
            player.sendColorMessage("<red>Nations are disabled in this world!");
            return true;
        }
        if(!(isValidIdeology(ideologyArg))){
            player.sendColorMessage("<red>This ideology does not exist!");
            return true;
        }
        NationManager.create(name, player, Ideologies.valueOf(ideologyArg.toUpperCase()), color);
        return true;
    }

    @Override
    public List<String> onTabComplete(BAFPlayer player, String[] args) {
        switch (args.length){
            case 1 -> {
                return List.of("<name>");
            }
            case 2 -> {
                return Arrays.stream(Ideologies.values())
                        .map(i -> i.name.substring(0, 1).toUpperCase() + i.name.substring(1).toLowerCase())
                        .toList();
            }
            case 3 -> {
                return List.of("[#hex_code]");
            }
        }
        return List.of();
    }

    private boolean isValidIdeology(String ideologyArg) {
        return Arrays.stream(Ideologies.values())
                .anyMatch(i -> i.name.equalsIgnoreCase(ideologyArg));
    }

    @Override
    public boolean requiresSession() {
        return false;
    }

    @Override
    public boolean isSessionLeaderOnly() {
        return false;
    }
}
