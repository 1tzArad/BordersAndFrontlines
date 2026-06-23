package xyz.ItzArad.baf.Placeholders;

import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.ItzArad.baf.BorderAndFrontlines;
import xyz.ItzArad.baf.Placeholders.nation.NationBalancePlaceholder;
import xyz.ItzArad.baf.Placeholders.nation.NationColorPlaceholder;
import xyz.ItzArad.baf.Placeholders.nation.NationNamePlaceholder;
import xyz.ItzArad.baf.Placeholders.nation.NationRankPlaceholder;
import xyz.ItzArad.baf.common.Placeholder;
import xyz.ItzArad.bafLibs.Colors;

import java.util.HashMap;
import java.util.Map;

public class NationPlaceholders extends PlaceholderExpansion {
    @Getter
    private final Map<String, Placeholder> placeholderMap = new HashMap<>();

    public NationPlaceholders(){
        register(new NationNamePlaceholder());
        register(new NationColorPlaceholder());
        register(new NationRankPlaceholder());
        register(new NationBalancePlaceholder());

        Colors.sendConsoleMessage("<aqua>[<blue>NationPlaceholders<aqua>] <blue>Loaded " + getPlaceholderMap().size() + " Placeholder(s) successfully!");
    }

    @Override
    public @NotNull String getIdentifier() {
        return "nation";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", BorderAndFrontlines.getInstance().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return BorderAndFrontlines.getInstance().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if(!getPlaceholderMap().containsKey(params)) return null;
        Placeholder placeholder = getPlaceholderMap().get(params);
        return placeholder.onRequest(player);
    }

    private void register(Placeholder placeholder){
        getPlaceholderMap().put(placeholder.getName(), placeholder);
    }
}
