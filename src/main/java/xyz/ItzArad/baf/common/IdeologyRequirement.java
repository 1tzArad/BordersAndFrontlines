package xyz.ItzArad.baf.common;

import xyz.ItzArad.baf.context.IdeologySelectionContext;

public interface IdeologyRequirement {

    boolean check(IdeologySelectionContext ctx);

    String getFailMessage();

}
