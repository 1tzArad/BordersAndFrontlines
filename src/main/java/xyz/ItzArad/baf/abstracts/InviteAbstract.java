package xyz.ItzArad.baf.abstracts;

import xyz.ItzArad.baf.common.Invite;

import java.util.UUID;

public abstract class InviteAbstract implements Invite {
    private final long expiresAt;
    private boolean isUsed = false;

    public InviteAbstract(long expiresAt){
        this.expiresAt = expiresAt;
    }

    @Override
    public void makeUsed(){
        this.isUsed = true;
    }
    @Override
    public boolean isUsed(){
        return this.isUsed;
    }
    @Override
    public long getExpiresAt() {
        return this.expiresAt;
    }

    public boolean isExpired(){
        return System.currentTimeMillis() > this.expiresAt;
    }


    public String generateCode(){
        return getPrefix() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
