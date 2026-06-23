package xyz.ItzArad.baf.Placeholders.nation;

import org.bukkit.OfflinePlayer;
import xyz.ItzArad.baf.common.Placeholder;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.Objects;

public class NationColorPlaceholder implements Placeholder {
    @Override
    public String getName() {
        return "color";
    }

    @Override
    public String onRequest(OfflinePlayer player) {
        BAFPlayer player1 = BAFPlayer.of(player);
        if(player1.getNation() == null){
            return "";
        }else{
            return player1.getNation().getColor();
        }
    }
}
