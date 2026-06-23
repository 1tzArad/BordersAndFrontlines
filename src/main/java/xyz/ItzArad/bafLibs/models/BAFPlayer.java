package xyz.ItzArad.bafLibs.models;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.ItzArad.baf.BorderAndFrontlines;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.*;
import xyz.ItzArad.bafLibs.Colors;
import java.util.Objects;
import java.util.UUID;

public record BAFPlayer(UUID uuid) {
    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(uuid);
    }
    public String getName(){
        return getOfflinePlayer().getName();
    }
    public boolean isOnline(){
        return getOfflinePlayer().isOnline();
    }

    public Player getPlayer(){
        return getOfflinePlayer().getPlayer();
    }

    @Nullable
    public Nation getNation(){
        return NationManager.getPlayerNationMap().get(this);
    }

    @Nullable
    public City getCity(){
        if(getNation() == null) return null;
        return NationManager.getPlayerCityMap().get(this);
    }

    public boolean isInNation(){
        return getNation() != null;
    }
    @Nullable
    public Ranks getRank(){
        if(getNation() == null) return null;
        return getNation().getRank(this);
    }

    @Nullable
    public CityRank getCityRank(){
        if(getCity() == null) return null;
        return getCity().getPlayers().get(uuid);
    }
    public BAFChunk getChunk(){
        return BAFChunk.of(getPlayer().getChunk());
    }

    public void sendActionBar(Component msg){
        getPlayer().sendActionBar(msg);
    }

    public void sendColorActionBar(String msg){
        sendActionBar(Colors.color(msg));
    }

    public boolean hasPermission(String p){
        return getPlayer().hasPermission(p);
    }
    public boolean hasPermission(Permissions p) { return Objects.requireNonNull(getRank()).hasPermission(p); }

    public void sendMessage(String... strings){
        getPlayer().sendMessage(strings);
    }

    public void sendMessage(Component message){
        getPlayer().sendMessage(message);
    }
    public void sendColorMessage(String message){
        getPlayer().sendMessage(Colors.color(message));
    }
    public void deposit(long amount){
        BorderAndFrontlines.getEconomy().deposit(this.uuid, amount);
    }
    public void withdraw(long amount){
        BorderAndFrontlines.getEconomy().withdraw(this.uuid, amount);
    }
    public boolean hasBalance(long amount){
        return BorderAndFrontlines.getEconomy().has(this.uuid, amount);
    }
    public void deposit(double amount){
        BorderAndFrontlines.getEconomy().deposit(this.uuid, (long) amount);
    }
    public void withdraw(double amount){
        BorderAndFrontlines.getEconomy().withdraw(this.uuid, (long) amount);
    }
    public boolean hasBalance(double amount){
        return BorderAndFrontlines.getEconomy().has(this.uuid, (long) amount);
    }


    public static BAFPlayer of(OfflinePlayer player){
        return new BAFPlayer(player.getUniqueId());
    }
    public static BAFPlayer of(Player player){
        return new BAFPlayer(player.getUniqueId());
    }

}
