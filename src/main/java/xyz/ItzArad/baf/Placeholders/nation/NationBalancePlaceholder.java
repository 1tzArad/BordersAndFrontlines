package xyz.ItzArad.baf.Placeholders.nation;

import org.bukkit.OfflinePlayer;
import xyz.ItzArad.baf.common.Placeholder;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

public class NationBalancePlaceholder implements Placeholder {
    @Override
    public String getName() {
        return "vault_balance";
    }

    @Override
    public String onRequest(OfflinePlayer player) {
        BAFPlayer player1 = BAFPlayer.of(player);
        if(player1.isInNation() && player1.getNation() != null){
            return Double.toString(player1.getNation().getVault_balance());
        }
        return "";
    }
}
