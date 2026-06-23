package xyz.ItzArad.baf.Placeholders.nation;

import org.bukkit.OfflinePlayer;
import xyz.ItzArad.baf.common.Placeholder;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

public class NationRankPlaceholder implements Placeholder {
    @Override
    public String getName() {
        return "rank";
    }

    @Override
    public String onRequest(OfflinePlayer player) {
        BAFPlayer player1 = BAFPlayer.of(player);
        if(player1.getRank() == null){
            return "";
        }else{
            return player1.getRank().getName();
        }
    }
}
