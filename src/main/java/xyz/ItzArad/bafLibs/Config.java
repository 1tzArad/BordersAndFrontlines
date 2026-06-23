package xyz.ItzArad.bafLibs;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@UtilityClass
public class Config {

    @Getter
    public FileConfiguration config;
    public JavaPlugin plugin;

    public void init(JavaPlugin plugin) {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
    }

    public FileConfiguration getFile(File file){
        return YamlConfiguration.loadConfiguration(file);
    }

    public static Object get(String path) {
        return config.get(path);
    }
    public static Object get(String path, Object def){
        return config.get(path, def);
    }
    public static String getString(String path){
        return config.getString(path);
    }
    public static String getString(String path, String def){
        return config.getString(path, def);
    }
    public static int getInt(String path){
        return config.getInt(path);
    }
    public static int getInt(String path, int def){
        return config.getInt(path, def);
    }
    public static double getDouble(String path){
        return config.getDouble(path);
    }
    public static double getDouble(String path, double def){
        return config.getDouble(path, def);
    }
    public static boolean getBoolean(String path){
        return getBoolean(path, false);
    }
    public static boolean getBoolean(String path, boolean def){
        return config.getBoolean(path, def);
    }
    public static boolean contains(String path){
        return config.contains(path);
    }
}
