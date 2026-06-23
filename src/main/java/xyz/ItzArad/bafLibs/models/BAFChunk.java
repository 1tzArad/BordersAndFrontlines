package xyz.ItzArad.bafLibs.models;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.baf.models.City;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.bafLibs.managers.BAFWorldManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record BAFChunk(int x, int z, BAFWorld world) implements ConfigurationSerializable {

    public static BAFChunk of(Chunk chunk){
        return new BAFChunk(chunk.getX(), chunk.getZ(), BAFWorldManager.of(chunk.getWorld()));
    }
    public static BAFChunk of(int x, int z, World world){
        return new BAFChunk(x, z, BAFWorldManager.of(world));
    }
    public static BAFChunk of(int x, int z){
        return new BAFChunk(x, z, BAFWorldManager.getDefaultWorld());
    }


    public enum Direction {
        EAST,
        WEST,
        NORTH,
        SOUTH
    }

    public World getBukkitWorld(){
        return Bukkit.getWorld(world.getName());
    }

    public Chunk getChunk(){
        return getBukkitWorld().getChunkAt(x, z);
    }

    public BAFChunk getRelative(int dx, int dz) {
        return new BAFChunk(x + dx, z + dz, world);
    }

    public boolean isClaimedNeighbor(){
        for (Direction direction : Direction.values()){
            BAFChunk chunk = this.getRelative(direction);
            if(chunk.isClaimed()){
                return true;
            }
        }
        return false;
    }
    public boolean isClaimedNeighbor(Nation nation){
        for (Direction direction : Direction.values()){
            BAFChunk chunk = this.getRelative(direction);
            if(chunk.isClaimed() && chunk.getNation() == nation){
                return true;
            }
        }
        return false;
    }
    public BAFChunk getRelative(Direction dir){
        if(dir==Direction.EAST){
            return getRelative(1, 0);
        }else if(dir==Direction.WEST){
            return getRelative(-1, 0);
        }else if(dir==Direction.SOUTH){
            return getRelative(0, 1);
        }else{
            return getRelative(0, -1);
        }
    }

    public boolean isClaimed(){
        return NationManager.getNationChunks().containsKey(this);
    }

    @Nullable
    public Nation getNation(){
        return NationManager.getNationChunks().get(this);
    }

    public boolean isCity(){ return NationManager.isChunkClaimedByACity(this); }

    @Nullable
    public City getCity(){
        if(!isCity()) return null;
        return NationManager.getCityByChunk(this);
    }

    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("world", Objects.requireNonNull(Bukkit.getWorld(this.world().getName())).getUID().toString());
        data.put("x", ""+this.x);
        data.put("z", ""+this.z);
        return data;
    }

    public static BAFChunk deserialize(Map<String, Object> map) {
        BAFWorld world = BAFWorldManager.of(UUID.fromString((String) map.get("world")));
        int x = Integer.parseInt((String)map.get("x"));
        int z = Integer.parseInt((String)map.get("z"));
        return new BAFChunk(x, z, world);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BAFChunk(int x1, int z1, BAFWorld world1))) {
            return false;
        }
        return this.x == x1 && this.z == z1 && this.world.equals(world1);
    }

    @Override
    public @NotNull String toString() {
        return "BAFChunk(x: "+ x +", z:"+ z +", world:"+ world +")";
    }

    public int getHighestYInChunk() {
        Chunk chunk = getChunk();
        int highestY = chunk.getWorld().getMinHeight();

        int startX = chunk.getX() << 4;
        int startZ = chunk.getZ() << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = startX + x;
                int worldZ = startZ + z;

                int y = chunk.getWorld().getHighestBlockYAt(worldX, worldZ);

                if (y > highestY) {
                    highestY = y;
                }
            }
        }

        return highestY;
    }
}
