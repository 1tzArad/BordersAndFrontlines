package xyz.ItzArad.baf.common;

import java.util.UUID;

public interface Invite {

    String getCode();
    UUID getInviter();
    UUID getOwner();
    long getExpiresAt();
    boolean isUsed();
    void makeUsed();
    String getPrefix();
}
