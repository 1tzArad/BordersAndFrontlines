package xyz.ItzArad.bafLibs.models;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.UUID;

public class BAFWorld {
    @Getter private final String name;
    public BAFWorld(String name){
        this.name = name;
    }
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BAFWorld other)) {
            return false;
        }
        return this.name.equals(other.name);
    }
    public int hashCode() {
        return Objects.requireNonNull(Bukkit.getWorld(name)).getUID().hashCode();
    }
    public String toString(){
        return "BAFWorld("+ getName() + ")";
    }
}
