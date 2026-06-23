package xyz.ItzArad.baf.models.sessions;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import xyz.ItzArad.baf.managers.NationCreationSessionManager;
import xyz.ItzArad.baf.models.Ideologies;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.models.BAFChunk;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class NationCreationSession {

    @Getter private final BAFPlayer leader;
    @Getter
    private final String name;
    private final Ideologies ideology;
    private final Chunk chunk;
    @Getter private State state;
    @Getter private int requiredAccepts  = 2;
    @Getter private final Set<BAFPlayer> acceptedPlayers = new HashSet<>();
    private final String color;

    public NationCreationSession(String name, BAFPlayer leader, Ideologies ideology, Chunk chunk, String color, State state){
        this.leader = leader;
        this.name = name;
        this.ideology = ideology;
        this.chunk = chunk;
        this.state = state;
        this.color = color;
    }

    public void addPlayer(BAFPlayer player){
        if(player.isInNation()) return;
        if(acceptedPlayers.contains(player)) return;
        acceptedPlayers.add(player);
        leaderNoticeOnPlayerAccept(player);
        if(acceptedPlayers.size() >= requiredAccepts) makeReady();
    }

    private void leaderNoticeOnPlayerAccept(BAFPlayer player){
        leader.sendColorActionBar("<yellow>"+ player.getName() +" <white>has <green>accepted</green> your session invite!");
    }

    public NationCreationSession setRequiresAccepts(int n){
        this.requiredAccepts = n;
        return this;
    }

    public boolean isReady(){
        return state.equals(State.READY);
    }

    public void makeReady(){
        this.state = State.READY;
        if(leader.isOnline()){
            String acceptedPlayers = getAcceptedPlayers().stream()
                    .map(BAFPlayer::getName)
                    .collect(Collectors.joining(", "));


            Component title = Colors.color("<green><b>Your nation is ready!</b></green>\n");
            Component executableText = Colors.executeCommandOnClick(Colors.color("<yellow><b>HERE</b></yellow>"), "nation confirm", "Click To Confirm it");
            Component description = Colors.color("<gray>Confirm by clicking </gray>")
                    .append(executableText)
                    .append(Colors.color("<gray> or using </gray><aqua>/nation confirm</aqua>\n"));
            Component players = Colors.color("<dark_gray>Members: </dark_gray>")
                    .append(Colors.color("<white>"+ acceptedPlayers +"</white>"));
            leader.sendMessage(
                    title.append(description).append(players)
            );
        }
    }

    public void makeCompleted(){
        if(!isReady()) {
            leader.sendColorMessage("<red>Your Nation Is Not Ready To Complete!");
            return;
        };
        if(!leader.isOnline()) {
            Colors.sendConsoleMessage("WTF IIN CHERA OFFLINE E :SOB:");
            return;
        };
        if(acceptedPlayers.size() < requiredAccepts) {
            leader.sendColorMessage("WTF ?"); // iin text e bayad change beshe, lol
            return;
        };

        NationCreationSessionManager.getSessionsMap().remove(leader);
        NationCreationSessionManager.resetAcceptedPlayers(this);
        new Nation(this.name, this.leader, this.ideology, BAFChunk.of(this.chunk), this.color).addPlayers(acceptedPlayers);
    }

    public boolean isInSession(BAFPlayer player){
        return getAcceptedPlayers().contains(player);
    }

    public void makeCanceled(){
        leader.sendColorActionBar("<red>Creation session canceled successfully!</red>");
        if(!acceptedPlayers.isEmpty()) {
            acceptedPlayers.forEach(player -> {
                player.sendColorMessage("<yellow>The nation creation session for</yellow> <aqua>" + this.name + "</aqua> <yellow>has been</yellow> <red>canceled</red><yellow>.</yellow>");
            });
        }
        NationCreationSessionManager.resetAcceptedPlayers(this);
        NationCreationSessionManager.getSessionsMap().remove(leader);
    }

    public enum State{
        WAITING_FOR_PLAYERS,
        READY
    }

    public boolean isPlayerAccepted(BAFPlayer player){
        return getAcceptedPlayers().contains(player);
    }

}
