package xyz.ItzArad.baf;

import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.The1tzArad.FrontlineEconomy.api.EconomyAPI;
import xyz.ItzArad.baf.commands.NationCommands;
import xyz.ItzArad.baf.listeners.AutoClaimListener;
import xyz.ItzArad.baf.listeners.CityCoreListener;
import xyz.ItzArad.baf.listeners.NationChatListener;
import xyz.ItzArad.baf.managers.MapManager;
import xyz.ItzArad.baf.managers.NationManager;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.Config;

import java.util.Map;

public class BorderAndFrontlines extends JavaPlugin {
    @Getter
    private static BorderAndFrontlines instance;
    @Getter
    private static EconomyAPI economy = null;

    @Override
    public void onEnable(){
        instance = this;

        // init Economy
        initEconomy();

        // init Config
        Config.init(this);

        // Nations Manager Init
        NationManager.init();

        // register commands
        registerCommands(Map.of(
                "nation", new NationCommands()
        ));

        // Hook BlueMap
        BlueMapHooker.init();
        MapManager.init();

        // Hook PlaceholderApi
        new PlaceholderApiHooker().hook();

        // register listeners
        registerListener(Map.of(
                "CityCoreListeners", CityCoreListener.class,
                "NationChatListener", NationChatListener.class,
                "AutoClaimListener", AutoClaimListener.class
        ));

        // enable log
        Colors.sendConsoleMessage("<green>Plugin Enabled Successfully!");
    }

    @Override
    public void onDisable(){
        // save all nations from cache
        NationManager.saveAllAsync();

        // disable log
        Colors.sendConsoleMessage("<red>Plugin Disabled Successfully!");
    }

    private void registerCommands(Map<String, CommandExecutor> commandExecutorMap) {
        commandExecutorMap.forEach((s, commandExecutor) -> {
            final PluginCommand pluginCommand = getCommand(s.toLowerCase());
            if (pluginCommand == null) {
                Colors.sendConsoleMessage("<red>Failed to register command <yellow>" + s);
                return;
            }
            pluginCommand.setExecutor(commandExecutor);
            Colors.sendConsoleMessage("<yellow>" + s + " <white>command loaded successfully!");
        });
    }

    private void initEconomy(){
        RegisteredServiceProvider<EconomyAPI> rsp =
                getServer().getServicesManager().getRegistration(EconomyAPI.class);

        if (rsp == null) {
            getLogger().severe("FrontlineEconomy not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        economy = rsp.getProvider();
    }

    private void registerListener(Map<String, Class<? extends Listener>> listeners) {
        for (Map.Entry<String, Class<? extends Listener>> entry : listeners.entrySet()) {
            try {
                Listener listener = entry.getValue().getDeclaredConstructor().newInstance();
                this.getServer().getPluginManager().registerEvents(listener, this);
                Colors.sendConsoleMessage("<yellow>" + entry.getKey() + " <white>listener loaded successfully!");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
