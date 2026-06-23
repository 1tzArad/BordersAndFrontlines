package xyz.ItzArad.baf.models;

import lombok.Getter;
import xyz.ItzArad.baf.abstracts.InviteAbstract;

import java.util.UUID;

public class AllyInvite extends InviteAbstract {
    private final String inviteCode;
    private final UUID target;
    private final UUID sender;
    @Getter private final UUID senderPlayer;

    public AllyInvite(UUID target, UUID sender, UUID senderPlayer, long expiresAt){
        super(expiresAt);
        this.target = target;
        this.sender = sender;
        this.senderPlayer = senderPlayer;
        this.inviteCode = generateCode();
    }

    @Override
    public String getCode() {
        return this.inviteCode;
    }

    @Override
    public UUID getInviter() {
        return this.sender;
    }

    @Override
    public UUID getOwner() {
        return this.target;
    }


    @Override
    public String getPrefix() {
        return "ally";
    }
}
