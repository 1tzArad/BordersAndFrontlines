package xyz.ItzArad.baf.common.commands;

import xyz.ItzArad.baf.models.Permissions;

import java.util.Optional;

public interface NationCommand extends SubCommand{
    Optional<Permissions> requiredPermission();

    boolean requiresNation();

    boolean publicCommand();
}
