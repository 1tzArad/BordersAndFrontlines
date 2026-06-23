package xyz.ItzArad.baf.common.commands;

public interface NationCreationSessionCommand extends NationCommand{

    boolean requiresSession();

    boolean isSessionLeaderOnly();
}
