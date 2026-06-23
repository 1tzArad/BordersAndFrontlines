package xyz.ItzArad.baf.commands.nation.creation_session;

import xyz.ItzArad.baf.common.commands.NationCreationSessionCommand;
import xyz.ItzArad.baf.managers.NationCreationSessionManager;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.Optional;

public class LeaveNationCreationCommand implements NationCreationSessionCommand {
    @Override
    public boolean requiresSession() {
        return true;
    }
    @Override
    public boolean publicCommand() {
        return false;
    }
    @Override
    public boolean isSessionLeaderOnly() {
        return false;
    }

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
        return "sleave";
    }

    @Override
    public String getDescription() {
        return "leave the creation session!";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.CreationSession.Leave";
    }

    @Override
    public String getUsage() {
        return "/nation sleave";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        // nemidonam chia ro bayad check bedam, lol
        if(NationCreationSessionManager.isInSession(player)){
            player.sendColorMessage("<red>You are not in any session!");
            return true;
        }
        NationCreationSessionManager.leavePlayerFromSession(player);
        return true;
    }
}
