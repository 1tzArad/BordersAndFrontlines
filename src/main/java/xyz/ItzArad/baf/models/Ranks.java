package xyz.ItzArad.baf.models;

import lombok.Getter;
import xyz.ItzArad.baf.managers.NationRanksManager;

import java.util.Set;

public record Ranks(@Getter String name,@Getter int weight,@Getter Set<Permissions> permissions) {

    public Ranks(String name, int weight, Set<Permissions> permissions){
        this.name = name;
        this.weight = weight;
        this.permissions = permissions;
        NationRanksManager.getRanksMap().put(name, this);
    }

    public boolean hasPermission(Permissions permission){
        return permissions.contains(permission);
    }

    public boolean isHigher(Ranks r){
        return r.getWeight() < this.getWeight();
    }
}
