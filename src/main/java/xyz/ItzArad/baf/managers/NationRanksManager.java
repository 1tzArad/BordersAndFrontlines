package xyz.ItzArad.baf.managers;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import xyz.ItzArad.baf.models.Ranks;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class NationRanksManager {
    @Getter
    public final Map<String, Ranks> ranksMap = new HashMap<>();

    public Ranks getRank(String name){
        return ranksMap.get(name);
    }

}
