package xyz.ItzArad.bafLibs.managers;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;
import xyz.ItzArad.bafLibs.models.BAFWorld;

import java.util.Objects;
import java.util.UUID;

@UtilityClass
public class BAFWorldManager {

    public BAFWorld of(UUID uuid){
        return new BAFWorld(Objects.requireNonNull(Bukkit.getWorld(uuid)).getName());
    }
    public BAFWorld of(World world){
        return new BAFWorld(world.getName());
    }
    public BAFWorld of(String name){
        return new BAFWorld(name);
    }
    public BAFWorld getDefaultWorld(){
        return new BAFWorld(Bukkit.getWorlds().getFirst().getName());
    }

}
