package xyz.ItzArad.baf.common.commands;

import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.List;

public interface TabCompletable {
    List<String> onTabComplete(BAFPlayer player, String[] args);
}
