package xyz.ItzArad.baf.commands;

import lombok.Getter;
import xyz.ItzArad.baf.common.commands.NationCommand;
import xyz.ItzArad.baf.common.commands.TabCompletable;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
// TODO: Complete Vault Commands And Create Commands: Deposit, Withdraw, Balance and ...
public class VaultCommands implements NationCommand, TabCompletable {
    @Getter private final Map<String, NationCommand> vaultCommands = new HashMap<>();
    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.of(
                Permissions.VAULT_COMMANDS
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
        return "vault";
    }

    @Override
    public String getDescription() {
        return "vault commands";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Vault";
    }

    @Override
    public String getUsage() {
        return "/nation vault <command>";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        if(args.length > 1){
            player.sendColorMessage("Vault Commands:");
            if(getVaultCommands().isEmpty()){
                player.sendColorMessage("<yellow>There is no vault commands that registered!");
                return true;
            }else {
                for (NationCommand cmd : getVaultCommands().values()) {
                    player.sendColorMessage("<yellow>" + cmd.getName() + "<gold>: <yellow>" + cmd.getDescription() + " <gold>- <yellow>Usage<gold>: " + cmd.getUsage());
                }
            }
            return true;
        }
        String cmdName = args[0];
        NationCommand cmd = getCommand(cmdName);
        if(cmd == null){
            player.sendColorMessage("<red>Command not found!");
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(BAFPlayer player, String[] args) {
        return List.of();
    }

    private void registerCommands(NationCommand cmd){
        getVaultCommands().put(cmd.getName().toLowerCase(), cmd);
    }

    @Nullable
    private NationCommand getCommand(String name){
        return getVaultCommands().get(name);
    }

}
