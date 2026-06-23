package xyz.ItzArad.baf;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import xyz.ItzArad.bafLibs.Colors;

@UtilityClass
public class BlueMapHooker {

    @Getter private boolean enabled = false;

    public void init(){
        if(Bukkit.getPluginManager().getPlugin("BlueMap") == null){
            Colors.sendConsoleMessage("<red>Failed to hook BlueMap!");
            enabled = false;
            return;
        }
        Colors.sendConsoleMessage("<green>Found BlueMap! attempting to hook...");
        enabled = true;
    }

}
