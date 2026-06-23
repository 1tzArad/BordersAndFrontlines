package xyz.ItzArad.baf.models;

import lombok.Getter;
import xyz.ItzArad.baf.managers.MapManager;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.bafLibs.models.BAFChunk;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.*;

@Getter
public class City {

    UUID uuid;
    String name;
    BAFPlayer mayor;
    final BAFChunk chunk;
    String color;
    transient final Nation nation;
    final Map<UUID, CityRank> players = new HashMap<>();

    int health;
    int level;

    final CityCore core;

    public City(String name, BAFPlayer player, String color, Nation nation){
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.color = color;
        this.mayor = player;
        addPlayer(mayor, CityRank.Mayor);
        this.level = 1;
        this.chunk = player.getChunk();
        this.health = this.Level2Health(this.level);
        this.core = new CityCore(this);
        this.nation = nation;
        NationManager.getCityChunks().put(chunk, this);
        NationManager.getCityMap().put(this, nation);
        MapManager.addCity(this, this.chunk);
    }

    public void addPlayer(BAFPlayer player){
        if(player.isInNation()) return;
        addPlayer(player, CityRank.Resident);
    }

    public void addPlayer(BAFPlayer player, CityRank rank){
        if(player.isInNation()) return;
        this.players.put(player.uuid(), rank);
        NationManager.getPlayerCityMap().put(player, this);
    }

    public void addPlayers(Map<BAFPlayer, CityRank> players){
        for (Map.Entry<BAFPlayer, CityRank> player : players.entrySet()) {
            addPlayer(player.getKey(), player.getValue());
        }
    }

    public void addPlayers(Set<BAFPlayer> players){
        for (BAFPlayer player : players){
            addPlayer(player);
        }
    }

    public void addPlayers(List<BAFPlayer> players){
        for (BAFPlayer player : players){
            addPlayer(player);
        }
    }

    private int Level2Health(int level){
        return level * 20;
    }
}
