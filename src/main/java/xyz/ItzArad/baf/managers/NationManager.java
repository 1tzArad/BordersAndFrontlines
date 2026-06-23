package xyz.ItzArad.baf.managers;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import xyz.ItzArad.baf.BorderAndFrontlines;
import xyz.ItzArad.baf.abstracts.InviteAbstract;
import xyz.ItzArad.baf.dialogs.NationCreationSessionInviteDialog;
import xyz.ItzArad.baf.models.*;
import xyz.ItzArad.baf.models.sessions.NationCreationSession;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.Config;
import xyz.ItzArad.bafLibs.JsonStorage;
import xyz.ItzArad.bafLibs.managers.BAFChunkManager;
import xyz.ItzArad.bafLibs.models.BAFChunk;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

@UtilityClass
public class NationManager {
    @Getter private final Map<BAFChunk, Nation> nationChunks = new HashMap<>();
    @Getter private final Map<String, Nation> nationNameMap = new HashMap<>();
    @Getter private final Map<BAFPlayer, Nation> playerNationMap = new HashMap<>();
    @Getter private final ConcurrentLinkedQueue<Nation> nationsCache = new ConcurrentLinkedQueue<>();

    @Getter private final Map<String, NationInvite> nationInvitesCache = new HashMap<>();
    @Getter private final Map<String, AllyInvite> allyInvitesCache = new HashMap<>();
    @Getter private final Map<String, InviteAbstract> abstractInviteCache = new HashMap<>();
    @Getter private final Set<BAFPlayer> nationChatToggleCache = new HashSet<>();
    @Getter private final Set<BAFPlayer> autoClaimPlayersSet = new HashSet<>();

    @Getter private final Map<BAFChunk, City> cityChunks = new HashMap<>();
    @Getter private final Map<City, Nation> cityMap = new HashMap<>();
    @Getter private final Map<City, CityCore> coreMap = new HashMap<>();
    @Getter private final Map<BAFPlayer, City> playerCityMap = new HashMap<>();


    private Path storageDir;
    private JsonStorage storage;


    public void init(){
        int loadedNations = 0;
        Colors.sendConsoleMessage("Started Loading Nations From Files...");
        try {
            storageDir = BorderAndFrontlines.getInstance().getDataPath().resolve("nations");
            storage = new JsonStorage(storageDir);
            List<UUID> uuids = storage.listAll();
            Colors.sendConsoleMessage("Loading <yellow>" + uuids.size() + " <white>nation(s)...");
            for (UUID uuid : uuids){
                try{
                    Nation nation = storage.load(uuid, Nation.class);
                    if(nation != null) {
                        getNationsCache().add(nation);
                        getNationNameMap().put(nation.getName(), nation);
                        for (UUID uuid1 : nation.getNationPlayers().keySet()){
                            getPlayerNationMap().put(new BAFPlayer(uuid1), nation);
                        }
                        for (BAFChunk chunk : nation.getClaimedChunks()){
                            getNationChunks().put(chunk, nation);
                        }
                        for (City city : nation.getCitiesMap().values()){
                            getCityMap().put(city, nation);
                            getCoreMap().put(city, city.getCore());
                            getCityChunks().put(city.getChunk(), city);
                        }
                        loadedNations++;
                        Colors.sendConsoleMessage("<yellow>" + nation.getName() + " <white>has loaded successfully!");
                    };
                }catch (IOException | RuntimeException e){
                    BorderAndFrontlines.getInstance().getLogger().log(Level.SEVERE, "Failed to load nation " + uuid, e);
                }
            }
        }catch (IOException e){
            BorderAndFrontlines.getInstance().getLogger().log(Level.SEVERE, "Failed to initialize NationManager", e);
        }
        Colors.sendConsoleMessage("<yellow>" + loadedNations + "<white> nation(s) has loaded successfully!");
    }

    public void saveNation(Nation n) {
        getNationsCache().add(n);
        getNationNameMap().put(n.getName(), n);
        saveNationAsync(n);
    }

    public CompletableFuture<Void> saveNationAsync(Nation n) {
        return storage.saveAsync(n.getUUID(), n)
                .exceptionally(ex -> {
                    BorderAndFrontlines.getInstance().getLogger().log(Level.SEVERE, "Failed to save nation " + n.getUUID(), ex);
                    return null;
                });
    }

    public void deleteNationSync(Nation n){
        try {
            storage.delete(n.getUUID());
        } catch (IOException e){
            BorderAndFrontlines.getInstance().getLogger().log(Level.SEVERE, "<red>Failed to delete nation sync " + n.getUUID(), e);
        }
    }

    public CompletableFuture<Boolean> deleteNationAsync(Nation n){
        return storage.deleteAsync(n.getUUID())
                .exceptionally(ex -> {
                    BorderAndFrontlines.getInstance().getLogger().log(Level.SEVERE, "Failed to delete nation " + n.getUUID(), ex);
                    return null;
                });
    }

    public void saveNationSync(Nation n) {
        try {
            storage.save(n.getUUID(), n);
        } catch (IOException e) {
            BorderAndFrontlines.getInstance().getLogger().log(Level.SEVERE, "Failed to save nation sync " + n.getUUID(), e);
        }
    }

    public CompletableFuture<Void> saveAllAsync() {
        CompletableFuture<?>[] futures = nationsCache.stream()
                .map(NationManager::saveNationAsync)
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures);
    }

    public void saveAllSync() {
        nationsCache.forEach(NationManager::saveNationSync);
    }

    public void create(String name, BAFPlayer leader, Ideologies ideology, String color){
        List<BAFPlayer> playersInChunk = BAFChunkManager.CountPlayersInChunk(leader.getPlayer().getChunk()).stream()
                .filter(player -> !player.isInNation())
                .toList();
        if(isChunkClaimed(leader.getChunk())){
            leader.sendColorMessage("<red>This chunk has already claimed by another nation!");
            return;
        }
        if(isNameExists(name)){
            leader.sendColorMessage("<red>This nation name is already exists!");
            return;
        }
        if(NationCreationSessionManager.hasSession(leader)){
            leader.sendColorMessage("<red>You already have a creation session for a nation called '" + NationCreationSessionManager.getSession(leader).getName() + "'!");
            return;
        }
        if(!leader.hasBalance(getCreationCost())){
            leader.sendColorMessage("<red>You don't have enough money to create a nation! at least you must have $"+ getCreationCost() +"!");
            return;
        }
        if(leader.isInNation()){
            leader.sendColorMessage("<red>To create a nation, you must not be in a nation!");
            return;
        }
        if(getMinNaming() > name.length() || name.length() > getMaxNaming()){
            leader.sendColorMessage("<red>Nation name length must be between "+ getMinNaming() +" and "+ getMaxNaming() +" characters");
            return;
        }
        if (playersInChunk.size() < (getMinPlayers() - 1)){
            leader.sendColorMessage("<red>There must be at least 2 more players in your chunk that who are not in any nation to invite!");
            return;
        }
        leader.withdraw(getCreationCost());
        leader.sendColorMessage("<green><green>Invites sent!</green> <yellow>Please wait for the players to accept, to finalize your nation creation.</yellow>");
        NationCreationSession session = NationCreationSessionManager.newSession(leader, name, ideology, leader.getPlayer().getChunk(), color);
        for (BAFPlayer player : playersInChunk){
            if(player.uuid() == leader.uuid()) continue;
            NationCreationSessionInviteDialog.open(player, session);
        }
    }

    public boolean isNameExists(String name){
        return getNationNameMap().containsKey(name.toLowerCase());
    }


    public boolean isChunkClaimed(BAFChunk chunk){
        return nationChunks.containsKey(chunk);
    }


    @Nullable
    public City getCityByChunk(BAFChunk chunk){
        return getCityChunks().get(chunk);
    }

    public boolean isChunkClaimedByACity(BAFChunk chunk){
        return  getCityChunks().containsKey(chunk);
    }

    public Nation getNationByCity(City city){
        return getCityMap().get(city);
    }

    public String createInvite(BAFPlayer target, BAFPlayer inviter, Nation nation){
        long expireAfter = 5 * 60 * 100;
        long expireAt = System.currentTimeMillis() + expireAfter;

        NationInvite data = new NationInvite(
                target.getOfflinePlayer().getUniqueId(),
                inviter.uuid(),
                nation.getUUID(),
                expireAt
        );

        String inviteCode = data.getCode();

        getNationInvitesCache().put(inviteCode, data);
        getAbstractInviteCache().put(inviteCode, data);
        return inviteCode;
    }

    public BAFPlayer getInviter(String inviteCode){
        return new BAFPlayer(getNationInvitesCache().get(inviteCode).getInviter());
    }

    public void sendInvite(BAFPlayer player, BAFPlayer inviter, Nation nation){
        String inviteCode = createInvite(player, inviter, nation);
        Component title = Colors.color("<b><green>You've Invited To</green> " + "<"+ nation.getColor() +">"+ nation.getName() + "</b>\n");
        Component acceptText = Colors.executeCommandOnClick(Colors.color("<green><b>Accept</b></green>"), "nation accept " + inviteCode, "Click To Accept the invite!");
        Component rejectText = Colors.executeCommandOnClick(Colors.color("<red><b>Reject</b></red>"), "nation reject " + inviteCode, "Click To Reject the invite!");
        Component gap = Colors.color(" <dark_gray>| <reset>");
        Component description = Colors.color("<gray>You've Invited To</gray> " + "<"+ nation.getColor() +">"+ nation.getName() + " <gray>By <white>" + getInviter(inviteCode).getName() + "<gray>!\n")
                .append(acceptText.append(gap.append(rejectText)));
        player.sendMessage(title.append(description));
        playInvitedSound(player);
        player.sendColorActionBar("<green>You have a nation invite request from " + "<"+ nation.getColor() +">"+ nation.getName() + "!");
    }

    public void playInvitedSound(BAFPlayer player){
        player.getPlayer().playSound(Sound.sound().type(org.bukkit.Sound.BLOCK_BEACON_ACTIVATE).volume(0.3f).pitch(0.9f).build(), Sound.Emitter.self());
        player.getPlayer().playSound(Sound.sound().type(org.bukkit.Sound.BLOCK_BEACON_DEACTIVATE).volume(0.3f).pitch(0.9f).build(), Sound.Emitter.self());
        player.getPlayer().playSound(Sound.sound().type(org.bukkit.Sound.BLOCK_BEACON_AMBIENT).volume(0.3f).pitch(1.5f).build(), Sound.Emitter.self());
        player.getPlayer().playSound(Sound.sound().type(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP).volume(0.3f).pitch(0.9f).build(), Sound.Emitter.self());
    }

    public boolean validateInviteCode(String inviteCode){
        return getAbstractInviteCache().containsKey(inviteCode);
    }

    public InviteAbstract getInvite(String inviteCode){
        return getAbstractInviteCache().get(inviteCode);
    }
    public NationInvite getNationInvite(String inviteCode){
        return getNationInvitesCache().get(inviteCode);
    }
    public AllyInvite getAllianceInvite(String inviteCode){
        return getAllyInvitesCache().get(inviteCode);
    }
    /*
        Utility methods!
     */

    public Nation getNation(UUID uuid){
        for (Nation nation : getNationsCache()){
            if(nation.getUUID().equals(uuid)) return nation;
            continue;
        }
        return null;
    }

    public Nation getNation(String name){
        return getNationNameMap().get(name);
    }

    public List<String> getNationsName(){
        return getNationNameMap().keySet().stream()
                .map(String::toUpperCase)
                .toList();
    }

    public Set<BAFPlayer> getNationPlayer(Nation nation){
        Set<BAFPlayer> players = new HashSet<>();
        for (UUID playeruuid : nation.getNationPlayers().keySet()){
            players.add(new BAFPlayer(playeruuid));
        }
        return players;
    }

    public boolean isValidWorld(String worldName){
        return getWorldsList().contains(worldName);
    }

    public boolean areAlliance(Nation a, Nation b){
        return a.isAllyWith(b);
    }
    public void makeAlliance(Nation a, Nation b){
        a.makeAlly(b);
        b.makeAlly(a);
    }
    public void breakAlliance(Nation a, Nation b){
        a.breakAlliance(b);
        b.breakAlliance(a);
    }
    /*
        Nation Chat
     */

    public boolean isNationChatToggle(BAFPlayer player){
        return nationChatToggleCache.contains(player);
    }

    public void toggleNationChat(BAFPlayer player) {
        if (!isNationChatToggle(player)) {
            nationChatToggleCache.add(player);
            player.sendColorActionBar("<green>Nation chat enabled.");
        } else {
            nationChatToggleCache.remove(player);
            player.sendColorActionBar("<red>Nation chat disabled.");
        }
    }

    public String createAllianceRequest(Nation target, Nation sender, BAFPlayer senderPlayer){
        long expiresIn = 5 * 60 * 1000;
        long expiresAt = System.currentTimeMillis() + expiresIn;

        AllyInvite data = new AllyInvite(target.getUUID(), sender.getUUID(), senderPlayer.uuid(), expiresAt);

        getAllyInvitesCache().put(data.getCode(), data);
        getAbstractInviteCache().put(data.getCode(), data);
        return data.getCode();
    }

    public void sendAlliance(Nation target, Nation sender, BAFPlayer senderPlayer){
        String inviteCode = createAllianceRequest(target, sender, senderPlayer);
        senderPlayer.sendColorActionBar("<green>You've successfully sent alliance request to <" + target.getColor() + ">" + target.getName() + "<green>!");
        sender.broadcast("<green>" + senderPlayer.getName() + " has sent an alliance request to <" + target.getColor() + ">" + target.getName() + "<green>!");
        target.broadcast("<" + sender.getColor() + ">" + sender.getName() + " <gray>has sent an alliance request to this nation!\n<dark_gray>" + target.getLeader().getName() + " <gray>must accept or deny it!");
        BAFPlayer targetLeader = target.getLeader();
        
        Component acceptText = Colors.executeCommandOnClick(Colors.color("<green><b>Accept</b></green>"), "nation accept " + inviteCode, "Click To Accept the invite!");
        Component rejectText = Colors.executeCommandOnClick(Colors.color("<red><b>Reject</b></red>"), "nation reject " + inviteCode, "Click To Reject the invite!");
        Component gap = Colors.color(" <dark_gray>| <reset>");
        Component msg = Colors.color("<b><" + sender.getColor() + ">" + sender.getName() + "<green> has sent a alliance request!</b>\n").append(acceptText.append(gap.append(rejectText)));

        targetLeader.sendMessage(msg);
        targetLeader.sendColorActionBar("<yellow>You're nation have an alliance request!");
    }
    /*
        Auto Claiming
     */

    public boolean isPlayerAutoClaimingToggle(BAFPlayer player){
        return getAutoClaimPlayersSet().contains(player);
    }

    public void toggleAutoClaim(BAFPlayer player){
        if(!isPlayerAutoClaimingToggle(player)) getAutoClaimPlayersSet().add(player);
        else getAutoClaimPlayersSet().remove(player);
    }

    public boolean claimChecks(BAFChunk chunk, BAFPlayer player){
        Nation nation = player.getNation();
        if(!player.isInNation() || nation == null){
            player.sendColorMessage("<red>You are not in any nation to claim!");
            getAutoClaimPlayersSet().remove(player);
            return false;
        } else if(!isValidWorld(chunk.getBukkitWorld().getName())){
            player.sendColorMessage("<red>Nations are disabled in this world!");
            return false;
        } else if(!chunk.isClaimedNeighbor(nation)){
            player.sendColorMessage("<red>Bayad claim e kiri kenar ye claim dige bashe");
            return false;
        } else if (nation.getCitiesMap().isEmpty()) { // it's always true, but I think it's not a bad idea to check it.
            player.sendColorMessage("<red>To claim a chunk you must at least have 1 city in your nation!");
            return false;
        }else if (!player.hasPermission(Permissions.CAN_CLAIM)) {
            player.sendColorMessage("<red>You don't have enough nation permission to claim!");
            return false;
        }else if (!player.hasBalance(getClaimCost())) {
            player.sendColorActionBar("<red>To claim a chunk you must have <dark_red>" + NationManager.getClaimCost() + "<red>$");
            return false;
        }else if (chunk.isClaimed()) {
            player.sendColorActionBar("<red>This chunk is already claimed!");
            return false;
        }
        return true;
    }

    /*
        Configuration
     */

    public int getMinNaming(){
        return Config.getInt("naming.min");
    }

    public int getMaxNaming(){
        return Config.getInt("naming.max");
    }

    public double getCreationCost(){
        return Config.getDouble("nation.creation-cost");
    }

    public int getMinPlayers(){
        return Config.getInt("nation.minPlayers");
    }

    public String getNationChatFormat(BAFPlayer player, Component message){return Config.getString("nation.nation-chat-format").replace("{rank}", Objects.requireNonNull(player.getNation()).getRank(player).getName()).replace("{player}", player.getName()).replace("{message}", Colors.serialize(message));}

    public List<String> getWorldsList(){return Config.getConfig().getStringList("worlds");}

    public double getClaimCost(){
        return Config.getDouble("nation.claim-cost");
    }

    public double getUnclaimRefund(){
        return Config.getDouble("nation.unclaim-refund");
    }
}
