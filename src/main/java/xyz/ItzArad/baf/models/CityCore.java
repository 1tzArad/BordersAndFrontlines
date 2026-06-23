package xyz.ItzArad.baf.models;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.bafLibs.Colors;

import java.util.Objects;
import java.util.UUID;

@Getter
public class CityCore {
    // model data
    UUID uuid;
    transient City city;
    Status status;


    int x;
    int y;
    int z;
    String worldName;

    @Getter UUID blockDisplayUUID;
    @Getter UUID textDisplayUUID;


    public CityCore(City city){
        this.uuid = UUID.randomUUID();
        this.city = city;
        this.status = Status.NORMAL;
        this.summon();

        NationManager.getCoreMap().put(city, this);
    }

    private void summon(){

        Location location = getCity().getMayor().getPlayer().getLocation();

        Block block = this.city.getChunk().getBukkitWorld().getBlockAt(location);
        String world = this.city.getChunk().getBukkitWorld().getName();

        block.setType(getMaterial());

        Campfire campfire = (Campfire) block.getBlockData();
        campfire.setLit(false);
        block.setBlockData(campfire);

        UUID blockDisplayUUID = city.getChunk().getBukkitWorld().spawn(block.getLocation().clone(), BlockDisplay.class, entity -> {
           entity.setBlock(getMaterial().createBlockData());
           entity.setGlowing(true);
           entity.setGlowColorOverride(Color.AQUA);
        }).getUniqueId();
        UUID textDisplayUUID = city.getChunk().getBukkitWorld().spawn(block.getLocation().clone().add(0.5, 1.4, 0.5), TextDisplay.class, entity -> {
            String mayorName = city.getMayor() != null
                    ? city.getMayor().getName()
                    : "None";
            entity.text(Colors.color(
                    "<" + city.getColor() + "><b>" + city.getName() + "</b></" + city.getColor() + ">\n" +
                            "<gray>Mayor: <white>" + mayorName
            ));
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setLineWidth(200);
            entity.setGlowing(true);
            entity.setGlowColorOverride(Color.AQUA);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        }).getUniqueId();

        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.worldName = world;
        this.textDisplayUUID = textDisplayUUID;
        this.blockDisplayUUID = blockDisplayUUID;
    }

    public void delete(){
        Objects.requireNonNull(Bukkit.getEntity(blockDisplayUUID)).remove();
        Objects.requireNonNull(Bukkit.getEntity(textDisplayUUID)).remove();
        this.city.getChunk().getBukkitWorld().getBlockAt(getX(), getY(), getZ()).setType(Material.AIR);
    }

    public static Material getMaterial(){
        return Material.CAMPFIRE;
    }

    public enum Status{
        NORMAL,
        CAPTURE
    }

}
