package xyz.ItzArad.baf;

import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import xyz.ItzArad.baf.Placeholders.NationPlaceholders;
import xyz.ItzArad.bafLibs.Colors;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderApiHooker{
    @Getter
    private final Map<String, PlaceholderExpansion> placeholdersMap = new HashMap<>();

    public PlaceholderApiHooker(){
        register(new NationPlaceholders());
    }

    public void hook(){
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            Colors.sendConsoleMessage("<dark_green>PlaceholderApi <green>has hooked successfully!");
            for(PlaceholderExpansion e : getPlaceholdersMap().values()){
                e.register();
                Colors.sendConsoleMessage("<blue>Loaded " + e.getName() + " Placeholder Successfully!");
            }
        }else{
            Colors.sendConsoleMessage("<red>Could not find PlaceholderAPI!");
            Bukkit.getPluginManager().disablePlugin(BorderAndFrontlines.getInstance());
        }
    }


    private void register(PlaceholderExpansion e){
        getPlaceholdersMap().put(e.getName(), e);
    }
}
