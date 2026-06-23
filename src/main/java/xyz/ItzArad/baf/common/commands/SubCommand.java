package xyz.ItzArad.baf.common.commands;

import xyz.ItzArad.bafLibs.models.BAFPlayer;

public interface SubCommand {
    String getName();
    String getDescription();
    String getPermission();
    String getUsage();
    boolean isPlayerOnly();
    boolean execute(BAFPlayer player, String[] args);
}
