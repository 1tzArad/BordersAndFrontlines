package xyz.ItzArad.baf.managers;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import xyz.ItzArad.baf.models.Ideologies;
import xyz.ItzArad.baf.models.sessions.NationCreationSession;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class NationCreationSessionManager {
    /*
        key: leader
        value: the session
     */
    @Getter private final Map<BAFPlayer, NationCreationSession> sessionsMap = new HashMap<>();
    /*
        key: player (not leader only)
        value: their session;
     */
    @Getter private final Map<BAFPlayer, NationCreationSession> sessionPlayerMap = new HashMap<>();

    public NationCreationSession newSession(BAFPlayer leader, String name, Ideologies ideology, Chunk chunk, String color){
        NationCreationSession session = new NationCreationSession(name, leader, ideology, chunk, color, NationCreationSession.State.WAITING_FOR_PLAYERS);
        getSessionsMap().put(leader, session);
        getSessionPlayerMap().put(leader, session);
        return session;
    }

    public boolean hasSession(BAFPlayer player){
        return getSessionsMap().containsKey(player);
    }

    public NationCreationSession getSession(BAFPlayer player){
        return getSessionsMap().get(player);
    }

    public boolean isInSession(BAFPlayer player){
        return getSessionPlayerMap().containsKey(player);
    }

    public void sendSessionInvite(BAFPlayer player, NationCreationSession session){

    }

    public void leavePlayerFromSession(BAFPlayer player){
        NationCreationSession session = getSession(player);
        getSessionPlayerMap().remove(player);
        session.getAcceptedPlayers().remove(player);

        // Leader Notice
        session.getLeader().sendColorActionBar(player.getName() + " has left your nation");
    }

    public void resetAcceptedPlayers(NationCreationSession session){
        for (BAFPlayer player : session.getAcceptedPlayers()){
            if(!getSessionPlayerMap().containsKey(player)) return;
            getSessionPlayerMap().remove(player);
        }
    }

    /*
        Session Invite Dialog Actions
     */

    public void acceptSessionInvite(BAFPlayer player, NationCreationSession session){
        if(session.isPlayerAccepted(player)){
            player.sendColorMessage("<red>You've already joined to this nation!");
            return;
        }else if(player.isInNation()){
            player.sendColorMessage("<red>You Are Already in a nation!");
            return;
        } else if (!player.isOnline()) {
            Colors.sendConsoleMessage("WTF ? iin player e " + player.getName() + " invite e session nation " + session.getName() + " ro accept karde vali online nist WTF");
            return;
        } else if (isInSession(player)) {
            player.sendColorMessage("<red>You've already accepted an invite from another session!");
            return;
        }
        getSessionPlayerMap().put(player, session);
        session.addPlayer(player);
    }

    public void rejectSessionInvite(BAFPlayer player, NationCreationSession session){
        session.getLeader().sendColorActionBar("<yellow>" + player.getName() + " <white>has <red>rejected</green> your session invite!");
    }
}
