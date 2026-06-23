package xyz.ItzArad.baf.managers;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import de.bluecolored.bluemap.api.markers.ShapeMarker;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import xyz.ItzArad.baf.models.City;
import xyz.ItzArad.baf.models.Nation;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.models.BAFChunk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public final class MapManager{

    private static BlueMapAPI api;

    @Getter
    private final Map<String, MarkerSet> NationClaimedChunksMarkerSetMap = new HashMap<>();
    @Getter
    private final Map<String, MarkerSet> CitiesMarkerSetMap = new HashMap<>();

    public static void init() {
        BlueMapAPI.onEnable(blueMapAPI -> {
            api = blueMapAPI;
            Colors.sendConsoleMessage("<blue>BlueMap hooked successfully");
            rebuildAll();
        });
    }

    public static void rebuildAll() {
        if (api == null) return;

        for (BlueMapMap map : api.getMaps()) {
            MarkerSet nationsSet = MarkerSet.builder()
                    .label("Nations")
                    .toggleable(true)
                    .defaultHidden(false)
                    .build();

            for (Nation nation : NationManager.getNationsCache()) {
                for (BAFChunk chunk : nation.getClaimedChunks()) {
                    addNationChunkInternal(nationsSet, nation, chunk);
                }
            }

            map.getMarkerSets().put("nations", nationsSet);
            getNationClaimedChunksMarkerSetMap().put(map.getId(), nationsSet);

            MarkerSet cities = MarkerSet.builder()
                    .label("Cities")
                    .toggleable(true)
                    .defaultHidden(false)
                    .build();

            for(City city : NationManager.getCityMap().keySet()){
                addCityInternal(cities, city, city.getChunk());
            }

            map.getMarkerSets().put("cities", cities);
            getCitiesMarkerSetMap().put(map.getId(), cities);
        }
    }

    public void addNationChunk(Nation nation, BAFChunk chunk) {
        if (api == null) return;

        for (MarkerSet set : getNationClaimedChunksMarkerSetMap().values()) {
            addNationChunkInternal(set, nation, chunk);
        }
    }

    public void removeNationChunk(Nation nation, BAFChunk chunk) {
        if (api == null) return;

        String id = markerId(nation.getUUID(), chunk);
        for (MarkerSet set : getNationClaimedChunksMarkerSetMap().values()) {
            set.getMarkers().remove(id);
        }
    }

    public void addCity(City city, BAFChunk chunk) {
        if (api == null) return;

        for (MarkerSet set : getCitiesMarkerSetMap().values()) {
            addCityInternal(set, city, chunk);
        }
    }

    public void removeCity(City city) {
        if (api == null) return;

        BAFChunk chunk = city.getChunk();
        String id = markerId(city.getUuid(), chunk);

        for (MarkerSet set : getCitiesMarkerSetMap().values()) {
            set.getMarkers().remove(id);
        }
    }



    private void addNationChunkInternal(MarkerSet set, Nation nation, BAFChunk chunk) {
        String id = markerId(nation.getUUID(), chunk);

        Shape shape = chunkShape(chunk.getChunk().getX(), chunk.getChunk().getZ());
        Color color = nationColor(nation);

        ShapeMarker marker = ShapeMarker.builder()
                .label(nation.getName())
                .shape(shape, 100)
                .fillColor(color)
                .lineColor(color)
                .lineWidth(2)
                .build();

        set.getMarkers().put(id, marker);
    }

    private void addCityInternal(MarkerSet set, City city, BAFChunk chunk){
        String id = markerId(city.getUuid(), chunk);

        Chunk bchunk = city.getChunk().getChunk();
        int chunkX = bchunk.getX();
        int chunkZ = bchunk.getZ();

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        double centerX = startX + 8;
        double centerZ = startZ + 8;

        POIMarker marker = POIMarker.builder()
                .label(city.getName())
                .position(new Vector3d(centerX, chunk.getBukkitWorld().getHighestBlockYAt((int)centerX, (int)centerZ), centerZ))
                .build();
        set.put(id, marker);
    }

    private static Shape chunkShape(int chunkX, int chunkZ) {
        double x = chunkX << 4;
        double z = chunkZ << 4;

        return Shape.builder()
                .addPoint(new Vector2d(x, z))
                .addPoint(new Vector2d(x + 16, z))
                .addPoint(new Vector2d(x + 16, z + 16))
                .addPoint(new Vector2d(x, z + 16))
                .build();
    }

    private static Color nationColor(Nation nation) {
        String c = nation.getColor();
        return new Color(c);
    }

    private static String markerId(UUID uuid, BAFChunk chunk) {
        return uuid + "_" + chunk.getChunk().getX() + "_" + chunk.getChunk().getZ();
    }
}
