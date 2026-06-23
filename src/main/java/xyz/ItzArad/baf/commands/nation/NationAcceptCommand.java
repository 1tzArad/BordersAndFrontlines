package xyz.ItzArad.baf.commands.nation;

import xyz.ItzArad.baf.abstracts.InviteAbstract;
import xyz.ItzArad.baf.common.commands.NationCommand;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.AllyInvite;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.baf.models.NationInvite;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.Optional;

public class NationAcceptCommand implements NationCommand {
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
        return false;
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String getDescription() {
        return "a command to accept invites!";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Accept";
    }

    @Override
    public String getUsage() {
        return "/nation accept <invite_code>";
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
        String inviteCode = args[0];
        InviteAbstract inviteAbstract = NationManager.getInvite(inviteCode);
        boolean checksResult = checks(player, inviteCode);
        if(!checksResult) return true;
        if(inviteAbstract instanceof NationInvite){
            nationInvite(player, inviteCode);
        } else if (inviteAbstract instanceof AllyInvite) {
            allianceInvite(player, inviteCode);
        }
        return true;
    }

    private boolean checks(BAFPlayer player, String inviteCode){
        InviteAbstract inviteAbstract = NationManager.getInvite(inviteCode);
        if(!NationManager.validateInviteCode(inviteCode)){
            player.sendColorMessage("<red>Invalid Invite Code!");
            return false;
        } else if (player.isInNation() && (inviteAbstract instanceof NationInvite)) {
            player.sendColorMessage("<ed>You are already in a nation!");
            return false;
        } else if (NationManager.getInvite(inviteCode).isExpired()) {
            player.sendColorMessage("<red>The invite has expired!");
            return false;
        } else if (NationManager.getInvite(inviteCode).isUsed()) {
            player.sendColorMessage("<red>This invite has used before!");
            return false;
        }
        return true;
    }

    private void nationInvite(BAFPlayer player, String inviteCode){
        NationInvite invite = NationManager.getNationInvite(inviteCode);
        Nation nation = NationManager.getNation(invite.getNation());

        invite.makeUsed();

        player.sendColorActionBar("<green>You've successfully joined to " + nation.getName() + "!");

        nation.addPlayer(player);
        nation.broadcast("<green>" + player.getName() + " <gray>has joined to the nation!");
    }

    private void allianceInvite(BAFPlayer player, String inviteCode){
        AllyInvite invite = NationManager.getAllianceInvite(inviteCode);
        Nation inviter = NationManager.getNation(invite.getInviter());
        Nation target = NationManager.getNation(invite.getOwner());

        invite.makeUsed();
        inviter.broadcast("<green>Alliance request to <" + target.getColor() + ">" + target.getName() + " <green>has been accepted!");
        target.broadcast("<green>Alliance request from <" + inviter.getColor() + ">" + inviter.getName() + " <green>has been accepted by <dark_green>" + player.getName() + "<green>!");
        NationManager.makeAlliance(inviter, target);
        player.sendColorActionBar("<green>Request has accepted successfully!");
    }
}
