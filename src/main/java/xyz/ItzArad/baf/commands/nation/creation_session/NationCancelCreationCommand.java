package xyz.ItzArad.baf.commands.nation.creation_session;

/*
    this command cancels the creation session!
 */

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.ItzArad.baf.common.commands.NationCreationSessionCommand;
import xyz.ItzArad.baf.managers.NationCreationSessionManager;
import xyz.ItzArad.baf.models.Permissions;
import xyz.ItzArad.baf.models.sessions.NationCreationSession;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.Optional;

public class NationCancelCreationCommand implements NationCreationSessionCommand {
    @Override
    public Optional<Permissions> requiredPermission() {
        return Optional.empty();
    }
    @Override
    public boolean publicCommand() {
        return false;
    }
    @Override
    public boolean requiresNation() {
        return false;
    }

    @Override
    public String getName() {
        return "cancel";
    }

    @Override
    public String getDescription() {
        return "cancels the nation creation session!";
    }

    @Override
    public String getPermission() {
        return "BAF.Nation.CreationSession.Cancel";
    }

    @Override
    public String getUsage() {
        return "/nation cancel";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean execute(BAFPlayer player, String[] args) {
        NationCreationSession session = NationCreationSessionManager.getSession(player);
        // vaghean nemidonam chia ro check bedam, wtf
        Component msg = Colors.color(
                "<yellow><b>Are you sure?</b></yellow>\n" +
                        "<gray>Cancelling this session will stop the nation creation process.</gray>\n\n"
        ).append(
                Colors.executeCodeOnClick(
                        Colors.color("<red><b>Cancel Creation</b></red>"),
                        aud -> {
                            if (!(aud instanceof Player)) return;
                            session.makeCanceled();
                        },
                        "<red>T-T</red>"
                )
        ).append(
                Colors.color("  ")
        ).append(
                Colors.executeCodeOnClick(
                        Colors.color("<green><b>Continue</b></green>"),
                        aud -> {
                            if (!(aud instanceof Player p)) return;
                            player.sendColorMessage("<blue>Hich etefaghi nayoftad o emaliyat e creation edame peyda mikone !");
                        },
                        "<green>^o^</green>"
                )
        );

        player.sendMessage(msg);

        return true;
    }

    @Override
    public boolean requiresSession() {
        return true;
    }

    @Override
    public boolean isSessionLeaderOnly() {
        return true;
    }
}
