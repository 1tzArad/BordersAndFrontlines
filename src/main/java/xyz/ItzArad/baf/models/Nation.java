package xyz.ItzArad.baf.models;

import lombok.Getter;
import xyz.ItzArad.baf.managers.MapManager;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.bafLibs.models.BAFChunk;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.*;

@Getter
public class Nation {
    // Main Settings
    final UUID UUID;
    String name;
    String displayName;
    BAFPlayer leader;
    Ideologies ideology;
    String color;

    final Set<BAFChunk> claimedChunks = new HashSet<>();
    final Map<UUID, Ranks> nationPlayers = new HashMap<>();
    final Set<UUID> allies = new HashSet<>();

    final Map<String, City> citiesMap = new HashMap<>();
    // Tax Settings
    double tax_rate = 0.15;
    int taxIntervalDays = 20;
    long lastTaxTime;

    // Vault
    double vault_balance = 1000.0;

    public Nation(String name, BAFPlayer leader, Ideologies ideology, BAFChunk chunk, String color){
        this.UUID = java.util.UUID.randomUUID();
        this.name = name;
        this.ideology = ideology;
        this.displayName = name;
        this.leader = leader;
        addPlayer(leader, ideology.getLeaderRank());
        this.color = color;
        this.leaderCreatedNotice();
        createCity(name + "'s Capital City", leader, color);
        NationManager.saveNation(this);
        claim(chunk);
    }

    public void createCity(String name, BAFPlayer player, String color){
        City city = new City(name, player, color, this);
        this.citiesMap.put(name, city);
    }

    public boolean isCityExists(String name){
        return citiesMap.containsKey(name);
    }

    private void leaderCreatedNotice(){
        this.leader.sendColorActionBar("<green>" + name + " </green><white>has created successfully!");
    }

    public void addPlayer(BAFPlayer player){
        if(player.isInNation()) return;
        addPlayer(player, this.ideology.getDefaultRank());
    }

    public void addPlayer(BAFPlayer player, Ranks rank){
        if(player.isInNation()) return;
        this.getNationPlayers().put(player.uuid(), rank);
        NationManager.getPlayerNationMap().put(player, this);
    }

    public void addPlayers(Map<BAFPlayer, Ranks> players){
        for (Map.Entry<BAFPlayer, Ranks> player : players.entrySet()) {
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

    public void removePlayer(BAFPlayer player){
        if(!player.isInNation()) return;
        this.getNationPlayers().remove(player.uuid());
        NationManager.getPlayerNationMap().remove(player);
        NationManager.getPlayerCityMap().remove(player);
        this.getNationPlayers().remove(player.uuid());
    }

    public synchronized void claim(BAFChunk chunk){
        if(chunk.isClaimed()) return;
        getClaimedChunks().add(chunk);
        MapManager.addNationChunk(this, chunk);
        NationManager.getNationChunks().put(chunk, this);
    }

    public synchronized boolean unclaim(BAFChunk chunk){
        if(!(NationManager.isChunkClaimed(chunk))) return false;
        getClaimedChunks().remove(chunk);
        MapManager.removeNationChunk(this, chunk);
        NationManager.getNationChunks().remove(chunk);
        return true;
    }

    public void broadcast(String message){
        for (BAFPlayer player : NationManager.getNationPlayer(this)){
            if(!player.isOnline()) continue;
            player.sendColorMessage("<gray>[<"+ getColor() +">" + getName() + "<gray>]<reset> " + message);
        }
    }

    public Ranks getRank(BAFPlayer player){
        return getNationPlayers().get(player.uuid());
    }

    public void vaultDeposit(double amount){
        vault_balance += amount;
    }
    public void vaultWithdraw(double amount){
        vault_balance -= amount;
    }
    public boolean hasBalance(double amount){
        return vault_balance >= amount;
    }

    public boolean isLeader(BAFPlayer player){
        return getLeader().equals(player);
    }
    public void disband(){
        NationManager.deleteNationSync(this);
        NationManager.getNationsCache().remove(this);
        NationManager.getNationNameMap().remove(this.getName(), this);
        for (BAFChunk chunk : getClaimedChunks()){
            NationManager.getNationChunks().remove(chunk, this);
            NationManager.getCityChunks().remove(chunk);
            MapManager.removeNationChunk(this, chunk);
        }
        for (BAFPlayer player : NationManager.getNationPlayer(this)){
            removePlayer(player);
        }
        for (City city : getCitiesMap().values()){
            NationManager.getCityMap().remove(city, this);
            NationManager.getCoreMap().remove(city, city.getCore());
            city.getCore().delete();
            MapManager.removeCity(city);
        }
    }

    public void makeAlly(Nation nation){
        getAllies().add(nation.getUUID());
    }

    public boolean isAllyWith(Nation a){
        return getAllies().contains(a.getUUID());
    }

    public void breakAlliance(Nation a){
        getAllies().remove(a.getUUID());
    }
}
