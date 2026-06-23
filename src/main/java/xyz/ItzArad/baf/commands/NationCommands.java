package xyz.ItzArad.baf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.ItzArad.baf.commands.nation.*;
import xyz.ItzArad.baf.commands.nation.creation_session.LeaveNationCreationCommand;
import xyz.ItzArad.baf.commands.nation.creation_session.NationCancelCreationCommand;
import xyz.ItzArad.baf.commands.nation.creation_session.NationConfirmCommand;
import xyz.ItzArad.baf.common.commands.NationCommand;
import xyz.ItzArad.baf.common.commands.NationCreationSessionCommand;
import xyz.ItzArad.baf.common.commands.SubCommand;
import xyz.ItzArad.baf.common.commands.TabCompletable;
import xyz.ItzArad.baf.managers.NationCreationSessionManager;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.*;

public class NationCommands implements CommandExecutor, TabCompleter {

    private final Map<String, NationCommand> nationCommands = new HashMap<>();

    public NationCommands() {
        registerCommand(new NationCreateCommand());
        registerCommand(new NationConfirmCommand());
        registerCommand(new NationCancelCreationCommand());
        registerCommand(new LeaveNationCreationCommand());
        registerCommand(new NationInfoCommand());
        registerCommand(new CityCommands());
        registerCommand(new NationChatToggleCommand());
        registerCommand(new NationClaimCommand());
        registerCommand(new NationInviteCommand());
        registerCommand(new NationAcceptCommand());
        registerCommand(new NationRejectCommand());
        registerCommand(new NationLeaveCommand());
        registerCommand(new NationDisbandCommand());
        registerCommand(new AllianceBreakCommand());
        registerCommand(new NationAllyCommand());
    }

    private void registerCommand(NationCommand cmd) {
        nationCommands.put(cmd.getName().toLowerCase(), cmd);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (args.length == 0) {
            nationCommands.values().forEach(c ->
                    sender.sendMessage(c.getName() + ": " + c.getDescription()));
            return true;
        }

        NationCommand subCommand = nationCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage("Command Not Found!");
            return true;
        }

        BAFPlayer player = null;
        if (sender instanceof Player p) {
            player = BAFPlayer.of(p);
        }

        if (subCommand.isPlayerOnly() && player == null) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if(subCommand.requiresNation() && !player.isInNation()){
            player.sendColorMessage("<red>To Use This Command You must be a nation!");
            return true;
        }

        if (player != null && !player.hasPermission(subCommand.getPermission())) {
            player.sendColorMessage("<red>You don't have permission!");
            return true;
        }

        if (player != null && subCommand instanceof NationCreationSessionCommand sessionCmd) {
            if (sessionCmd.requiresSession() &&
                    !NationCreationSessionManager.isInSession(player)) {
                player.sendColorMessage("<red>You are not in a nation creation session!");
                return true;
            }

            if (sessionCmd.isSessionLeaderOnly() &&
                    !NationCreationSessionManager.hasSession(player)) {
                player.sendColorMessage("<red>You must be the session leader!");
                return true;
            }
        }

        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        return subCommand.execute(player, newArgs);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        if (!(sender instanceof Player p)) return List.of();

        BAFPlayer player = BAFPlayer.of(p);

        if (args.length == 1) {
            return nationCommands.values().stream()
                    .filter(cmd -> cmd.getName().startsWith(args[0].toLowerCase()))
                    .filter(cmd -> player.hasPermission(cmd.getPermission()))
                    .filter(cmd -> {
                        if (cmd instanceof NationCreationSessionCommand sessionCmd) {
                            if ((sessionCmd.requiresSession() &&
                                    !NationCreationSessionManager.isInSession(player)) || (sessionCmd.isSessionLeaderOnly() &&
                                    !NationCreationSessionManager.hasSession(player)))
                                return false;
                        }
                        if((cmd.requiresNation() && !cmd.publicCommand()
                        && !player.isInNation()) || (!cmd.requiresNation() && !cmd.publicCommand()
                                && player.isInNation())){
                            return false;
                        }
                        return true;
                    })
                    .map(SubCommand::getName)
                    .sorted()
                    .toList();
        }

        NationCommand subCommand = nationCommands.get(args[0].toLowerCase());
        if (subCommand instanceof TabCompletable tabCompletable) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return tabCompletable.onTabComplete(player, subArgs);
        }

        return List.of();
    }
}
