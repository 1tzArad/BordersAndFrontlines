package xyz.ItzArad.baf.models;

import lombok.Getter;
import xyz.ItzArad.baf.abstracts.InviteAbstract;

import java.util.UUID;

@Getter
public class NationInvite extends InviteAbstract {
    private final String inviteCode;
    private final UUID owner;
    private final UUID inviter;
    private final UUID nation;

    public NationInvite(UUID owner, UUID inviter, UUID nation, long expiresAt){
        super(expiresAt);
        this.owner = owner;
        this.inviter = inviter;
        this.nation = nation;
        this.inviteCode = generateCode();
    }

    @Override
    public String getCode() {
        return inviteCode;
    }

    @Override
    public UUID getInviter() {
        return this.inviter;
    }

    @Override
    public UUID getOwner() {
        return this.owner;
    }

    @Override
    public String getPrefix() {
        return "nation_invite";
    }
}
