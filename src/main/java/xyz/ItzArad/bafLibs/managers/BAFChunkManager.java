package xyz.ItzArad.bafLibs.managers;

import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.*;

@UtilityClass
public class BAFChunkManager {
    public Collection<BAFPlayer> CountPlayersInChunk(Chunk chunk){
        return Arrays.stream(chunk.getEntities())
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .map(BAFPlayer::of)
                .toList();
    }
}
