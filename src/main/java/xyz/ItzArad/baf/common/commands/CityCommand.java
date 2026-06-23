package xyz.ItzArad.baf.common.commands;

import xyz.ItzArad.baf.models.City;
import xyz.ItzArad.baf.models.CityRank;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.Optional;

public interface CityCommand extends NationCommand{

    boolean isMayorOnly();

    Optional<CityRank> requiredCityRank(BAFPlayer player);

    boolean shouldBeInCityChunk();

    City getCity(City city);
}
