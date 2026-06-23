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

public class NationRejectCommand implements NationCommand {
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
        return "reject";
    }

    @Override
    public String getDescription() {
        return "reject a invite!";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.Reject";
    }
    @Override
    public boolean publicCommand() {
        return false;
    }
    @Override
    public String getUsage() {
        return "/nation reject <inviteCode>";
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
            player.sendColorMessage("<red>You are already in a nation!");
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
        BAFPlayer inviter = new BAFPlayer(invite.getInviter());
        Nation nation = NationManager.getNation(invite.getNation());

        invite.makeUsed();
        inviter.sendColorActionBar("<red>" + player.getName() + " has rejected your invite request to " + nation.getName() + "!");
        player.sendColorMessage("<gray>You successfully <red>rejected <gray>the invite request!");
    }

    private void allianceInvite(BAFPlayer player, String inviteCode){
        AllyInvite invite = NationManager.getAllianceInvite(inviteCode);
        Nation inviter = NationManager.getNation(invite.getInviter());
        Nation target = NationManager.getNation(invite.getOwner());

        invite.makeUsed();
        inviter.broadcast("<red>Alliance request to <" + target.getColor() + ">" + target.getName() + " <red>has been rejected!");
        target.broadcast("<red>Alliance request from <" + inviter.getColor() + ">" + inviter.getName() + " <red>has been rejected by <dark_red>" + player.getName() + "<red>!");
        NationManager.breakAlliance(inviter, target);
        player.sendColorActionBar("<red>Request has rejected successfully!");
    }
}
