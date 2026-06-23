package xyz.ItzArad.baf.common;

import org.bukkit.OfflinePlayer;

public interface Placeholder {
    String getName();
    String onRequest(OfflinePlayer player);
}
