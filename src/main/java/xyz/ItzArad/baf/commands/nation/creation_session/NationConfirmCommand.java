package xyz.ItzArad.baf.commands.nation.creation_session;

import xyz.ItzArad.baf.common.commands.NationCreationSessionCommand;
import xyz.ItzArad.baf.common.commands.TabCompletable;
import xyz.ItzArad.baf.managers.NationCreationSessionManager;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.baf.models.sessions.NationCreationSession;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.List;
import java.util.Optional;

public class NationConfirmCommand implements NationCreationSessionCommand, TabCompletable {
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
        return false;
    }

    @Override
    public String getName() {
        return "confirm";
    }

    @Override
    public String getDescription() {
        return "confirm your nation creation";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.CreationSession.Confirm";
    }

    @Override
    public String getUsage() {
        return "/nation confirm";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        NationCreationSession session = NationCreationSessionManager.getSession(player);
        if(!session.isReady()){
            player.sendColorMessage("<red>Your creation session is not ready yet!");
            return true;
        }
        session.makeCompleted();
        return true;
    }

    @Override
    public List<String> onTabComplete(BAFPlayer player, String[] args) {
        return List.of();
    }

    @Override
    public boolean requiresSession() {
        return true;
    }

    @Override
    public boolean isSessionLeaderOnly() {
        return true;
    }
}
