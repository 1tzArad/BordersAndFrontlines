package xyz.ItzArad.baf.context;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class IdeologySelectionContext {
    private final BAFPlayer leader;
    private final Collection<BAFPlayer> players;
    private final BAFPlayer chunk;
}