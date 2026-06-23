package xyz.ItzArad.baf.models;

import lombok.Getter;
import org.bukkit.Material;
import xyz.ItzArad.baf.common.IdeologyRequirement;

import java.util.List;
import java.util.Set;

@Getter
public enum Ideologies {
    TEST("test","test description", List.of(), List.of(
            new Ranks("Leader", 1000, Set.of(
                    Permissions.VAULT_DEPOSIT,
                    Permissions.VAULT_WITHDRAW,
                    Permissions.CAN_CLAIM,
                    Permissions.CAN_UNCLAIM
            )),
            new Ranks("Member", 1, Set.of())
    ), Material.ACACIA_BOAT);

    public final String name;

    public final String description;
    public final List<IdeologyRequirement> requirements;
    public final List<Ranks> ranks;
    private final Material icon;
    Ideologies(String name, String description, List<IdeologyRequirement> requirements,List<Ranks> ranks, Material icon){
        this.name = name;
        this.description = description;
        this.ranks = ranks;
        this.requirements = requirements;
        this.icon = icon;
    }

    public Ranks getDefaultRank(){
        return this.ranks.getLast();
    }

    public Ranks getLeaderRank(){
        return this.ranks.getFirst();
    }

}
