package xyz.ItzArad.baf.commands.nation;

import xyz.ItzArad.baf.common.commands.NationCommand;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.Optional;

public class NationChatToggleCommand implements NationCommand {
    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.of(Permissions.NATION_CHAT);
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
        return "chat";
    }

    @Override
    public String getDescription() {
        return "toggles nation chat";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Chat";
    }

    @Override
    public String getUsage() {
        return "/nation chat";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        NationManager.toggleNationChat(player);
        return true;
    }
}
