/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances;

import java.io.BufferedReader;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.cyberiantiger.minecraft.instances.command.SenderType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.cyberiantiger.minecraft.Coord;
import org.cyberiantiger.minecraft.Cuboid;
import org.cyberiantiger.minecraft.Facing;
import org.cyberiantiger.minecraft.instances.command.Cmd;
import org.cyberiantiger.minecraft.instances.command.Command;
import org.cyberiantiger.minecraft.instances.command.CreatePortal;
import org.cyberiantiger.minecraft.instances.command.DeletePortal;
import org.cyberiantiger.minecraft.instances.command.Genocide;
import org.cyberiantiger.minecraft.instances.command.Home;
import org.cyberiantiger.minecraft.instances.command.InvocationException;
import org.cyberiantiger.minecraft.instances.command.Mob;
import org.cyberiantiger.minecraft.instances.command.ModifyPortal;
import org.cyberiantiger.minecraft.instances.command.Motd;
import org.cyberiantiger.minecraft.instances.command.NotAvailableException;
import org.cyberiantiger.minecraft.instances.command.PartyChat;
import org.cyberiantiger.minecraft.instances.command.PartyCreate;
import org.cyberiantiger.minecraft.instances.command.PartyDisband;
import org.cyberiantiger.minecraft.instances.command.PartyEmote;
import org.cyberiantiger.minecraft.instances.command.PartyInfo;
import org.cyberiantiger.minecraft.instances.command.PartyInvite;
import org.cyberiantiger.minecraft.instances.command.PartyJoin;
import org.cyberiantiger.minecraft.instances.command.PartyKick;
import org.cyberiantiger.minecraft.instances.command.PartyLeader;
import org.cyberiantiger.minecraft.instances.command.PartyLeave;
import org.cyberiantiger.minecraft.instances.command.PartyList;
import org.cyberiantiger.minecraft.instances.command.PartyUninvite;
import org.cyberiantiger.minecraft.instances.command.PortalList;
import org.cyberiantiger.minecraft.instances.command.Reload;
import org.cyberiantiger.minecraft.instances.command.Save;
import org.cyberiantiger.minecraft.instances.command.SelectionTool;
import org.cyberiantiger.minecraft.instances.command.SetDestination;
import org.cyberiantiger.minecraft.instances.command.SetEntrance;
import org.cyberiantiger.minecraft.instances.command.SetHome;
import org.cyberiantiger.minecraft.instances.command.SetSpawn;
import org.cyberiantiger.minecraft.instances.command.Spawn;
import org.cyberiantiger.minecraft.instances.command.Spawner;
import org.cyberiantiger.minecraft.unsafe.InstanceTools;
import org.cyberiantiger.minecraft.unsafe.CBShim;
import org.cyberiantiger.minecraft.instances.unsafe.depend.PacketHooks;
import org.cyberiantiger.minecraft.instances.unsafe.depend.Bank;
import org.cyberiantiger.minecraft.instances.unsafe.depend.WorldInheritance;
import org.cyberiantiger.minecraft.instances.unsafe.depend.CuboidSelection;
import org.cyberiantiger.minecraft.instances.unsafe.depend.InstancesCuboidSelectionFactory;
import org.cyberiantiger.minecraft.instances.unsafe.depend.MultiverseCoreWorldInheritanceFactory;
import org.cyberiantiger.minecraft.instances.unsafe.depend.MultiverseInventoriesWorldInheritanceFactory;
import org.cyberiantiger.minecraft.instances.unsafe.depend.PEXWorldInheritanceFactory;
import org.cyberiantiger.minecraft.instances.unsafe.depend.ProtocolLibPacketHooksFactory;
import org.cyberiantiger.minecraft.instances.unsafe.depend.VaultBankFactory;
import org.cyberiantiger.minecraft.instances.unsafe.depend.WorldEditCuboidSelectionFactory;
import org.cyberiantiger.minecraft.instances.unsafe.depend.WorldGuardWorldInheritanceFactory;
import org.cyberiantiger.minecraft.instances.util.DependencyFactory;
import org.cyberiantiger.minecraft.instances.util.DependencyUtil;
import org.cyberiantiger.minecraft.instances.util.StringUtil;
import org.cyberiantiger.minecraft.unsafe.NBTTools;
import org.cyberiantiger.minecraft.util.FileUtils;

/**
 *
 * @author antony
 */
public class Instances extends JavaPlugin implements Listener {

    public static final Charset CHARSET = Charset.forName("UTF-8");
    private String[] motd;
    private String partyNamePrefix;
    private String partyNameSuffix;
    private boolean leaderInvite;
    private boolean leaderKick;
    private boolean leaderDisband;
    private boolean editCommandInCreative = true;
    private String spawnName;
    private Map<String, Party> parties = new HashMap<String, Party>();
    private Map<Player, Party> partyMap = new HashMap<Player, Party>();
    private Map<String, PortalPair> portals = new HashMap<String, PortalPair>();
    private Map<String, List<Portal>> portalMap = new HashMap<String, List<Portal>>();
    private Map<Player, InstanceEntrancePortal> lastPortal = new HashMap<Player, InstanceEntrancePortal>();
    private Map<String, HomeLocation> homes = new HashMap<String, HomeLocation>();
    private Map<String, List<Instance>> instanceMap = new HashMap<String, List<Instance>>();
    private ItemStack selectionTool;
    private Map<Player, Session> sessions = new HashMap<Player, Session>();
    private final static Map<String, Command> commands = new HashMap<String, Command>();
    private Bank bank;
    private WorldInheritance worldInheritance;
    private CuboidSelection cuboidSelection;
    private PacketHooks packetHooks;
    private NBTTools nbtTools;
    private InstanceTools instanceTools;

    {
        commands.put("p", new PartyChat());
        commands.put("pme", new PartyEmote());
        commands.put("pcreate", new PartyCreate());
        commands.put("pdisband", new PartyDisband());
        commands.put("pinfo", new PartyInfo());
        commands.put("pinvite", new PartyInvite());
        commands.put("pjoin", new PartyJoin());
        commands.put("pkick", new PartyKick());
        commands.put("pleader", new PartyLeader());
        commands.put("pleave", new PartyLeave());
        commands.put("puninvite", new PartyUninvite());
        commands.put("plist", new PartyList());
        commands.put("setspawn", new SetSpawn());
        commands.put("spawn", new Spawn());
        commands.put("isave", new Save());
        commands.put("ireload", new Reload());
        commands.put("home", new Home());
        commands.put("sethome", new SetHome());
        commands.put("motd", new Motd());
        commands.put("iselectiontool", new SelectionTool());
        commands.put("icreateportal", new CreatePortal());
        commands.put("isetentrance", new SetEntrance());
        commands.put("isetdestination", new SetDestination());
        commands.put("iportallist", new PortalList());
        commands.put("ideleteportal", new DeletePortal());
        commands.put("imob", new Mob());
        commands.put("igenocide", new Genocide());
        commands.put("imodifyportal", new ModifyPortal());
        commands.put("ispawner", new Spawner());
        commands.put("icmd", new Cmd());
    }

    public File getInstanceWorldContainer() {
        return new File(getDataFolder(), "worlds");
    }

    public Bank getBank() {
        return bank;
    }

    public WorldInheritance getWorldInheritance() {
        return worldInheritance;
    }

    public CuboidSelection getCuboidSelection() {
        return cuboidSelection;
    }

    public Collection<PortalPair> getPortalPairs() {
        return portals.values();
    }

    public NBTTools getNBTTools() {
        return nbtTools;
    }

    @CheckForNull
    public InstanceTools getInstanceTools() {
        return instanceTools;
    }

    public PortalPair getPortalPair(String name) {
        return portals.get(name);
    }

    public void addPortalPair(PortalPair pair) {
        portals.put(pair.getName(), pair);
        addPortal(pair.getEnter());
        addPortal(pair.getDestination());
    }

    public void addPortal(Portal portal) {
        String world = portal.getCuboid().getWorld();
        List<Portal> l = portalMap.get(world);
        if (l == null) {
            l = new ArrayList<Portal>();
            portalMap.put(world, l);
        }
        l.add(portal);
    }

    public void removePortalPair(PortalPair pair) {
        pair = portals.remove(pair.getName());
        removePortal(pair.getEnter());
        removePortal(pair.getDestination());
    }

    public void removePortal(Portal portal) {
        String world = portal.getCuboid().getWorld();
        List<Portal> worldPortals = portalMap.get(world);
        if (worldPortals != null) {
            worldPortals.remove(portal);
        }
    }

    public String getPartyNamePrefix() {
        return partyNamePrefix;
    }

    public String getPartyNameSuffix() {
        return partyNameSuffix;
    }

    public boolean isLeaderInvite() {
        return leaderInvite;
    }

    public boolean isLeaderDisband() {
        return leaderDisband;
    }

    public boolean isLeaderKick() {
        return leaderKick;
    }

    public ItemStack getSelectionTool() {
        return selectionTool;
    }

    public void setSelectionTool(ItemStack selectionTool) {
        this.selectionTool = selectionTool;
    }

    public Session getSession(Player player) {
        Session session = sessions.get(player);
        if (session == null) {
            session = new Session();
            sessions.put(player, session);
        }
        return session;
    }

    public Selection getSelection(Player player) {
        return getSession(player).getCurrent();
    }

    public String[] getMotd() {
        return motd;
    }

    public World getSpawn() {
        return spawnName == null ? null : getServer().getWorld(spawnName);
    }

    public void setSpawn(World spawn) {
        this.spawnName = spawn.getName();
    }

    public void setHome(Player player, Location home) {
        homes.put(player.getName(), new HomeLocation(home));
    }

    public Location getHome(Player player) {
        HomeLocation home = homes.get(player.getName());
        return home == null ? null : home.getLocation(getServer());
    }

    public Collection<Party> getParties() {
        return parties.values();
    }

    public Party getParty(String name) {
        return parties.get(name);
    }

    public Party getParty(Player player) {
        return partyMap.get(player);
    }

    public Party partyCreate(String name, Player player) {
        Party party = new Party(name, player);
        parties.put(name, party);
        partyMap.put(player, party);
        return party;
    }

    public void partyDisband(Party party) {
        for (Player p : party.getMembers()) {
            // Will cause calls to checkInstances(party) on the next tick.
            // Instances should be empty anyway.
            removePlayerFromInstance(p);
            partyMap.remove(p);
        }
        // Clear party members so checkInstances() can delete any instances.
        party.getMembers().clear();
        party.setLeader(null);
        // Delete any party instances which are still hanging around.
        Iterator<Instance> i = party.getInstances().iterator();
        while (i.hasNext()) {
            Instance instance = i.next();
            deleteInstance(instance);
            i.remove();
        }
        parties.remove(party.getName());
    }

    public void partyAdd(Party party, Player player) {
        party.getInvites().remove(player);
        party.getMembers().add(player);
        partyMap.put(player, party);
    }

    public void partyRemove(Party party, Player player) {
        // May cause a call to checkInstances next tick via teleport event.
        removePlayerFromInstance(player);
        party.getMembers().remove(player);
        partyMap.remove(player);
        if (party.getMembers().isEmpty()) {
            partyDisband(party);
        }
    }

    public void partyInvite(Party party, Player player) {
        party.getInvites().add(player);
    }

    public void partyUninvite(Party party, Player player) {
        party.getInvites().remove(player);
    }

    public void setLastEnterPortal(Player player, InstanceEntrancePortal portal) {
        lastPortal.put(player, portal);
    }

    public World createInstance(Party party, Player player, PortalPair pair) {
        String sourceWorldName = pair.getDestination().getCuboid().getWorld();

        int i = pair.getDefaultParty() == null ? 1 : party.getName().equals(pair.getDefaultParty()) ? 0 : 1;

        while (getServer().getWorld(sourceWorldName + '-' + i) != null) {
            i++;
        }

        String instanceName = sourceWorldName + '-' + i;

        getWorldInheritance().preAddInheritance(sourceWorldName, instanceName);

        try {
            InstanceTools tools = getInstanceTools();
            if (tools == null) {
                throw new IllegalStateException("Unsupported server version: " + getServer().getBukkitVersion());
            }
            
            if (getServer().getWorld(sourceWorldName) != null) {
                getLogger().warning(sourceWorldName + " is loaded, instances of loaded worlds may cause issues.");
            }
            
            File sourcePath = new File(getServer().getWorldContainer(), sourceWorldName);
            
            File instanceWorldContainer = getInstanceWorldContainer();
            
            instanceWorldContainer.mkdirs();
            
            File instanceFolder = File.createTempFile(instanceName, ".world", instanceWorldContainer);
            instanceFolder.delete();
            instanceFolder.mkdir();
            World world = tools.createInstance(this, instanceName, pair.getEnvironment(), pair.getDifficulty(), sourcePath, instanceFolder);
            
            getWorldInheritance().postAddInheritance(sourceWorldName, instanceName);
            
            pair.getLastCreate().put(player.getName(), System.currentTimeMillis());
            
            Instance instance = new Instance(pair, sourceWorldName, world.getName());
            
            party.addInstance(instance);
            List<Instance> instances;
            if (!instanceMap.containsKey(pair.getName())) {
                instances = new ArrayList<Instance>();
                instanceMap.put(pair.getName(), instances);
            } else {
                instances = instanceMap.get(pair.getName());
            }
            instances.add(instance);
            
            getLogger().info("Created instance: " + instance);
            
            return world;
        } catch (RuntimeException e) {
            getLogger().log(Level.WARNING, "Error creating instance", e);
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Error creating instance", e);
        }
        getWorldInheritance().postRemoveInheritance(sourceWorldName, instanceName);
        return null;
    }

    public List<Instance> getInstances(PortalPair portal) {
        List<Instance> ret = instanceMap.get(portal.getName());
        return ret == null ? Collections.<Instance>emptyList() : ret;
    }
    
    public boolean isInstance(World world) {
        for (Party p : parties.values()) {
            for (Instance i : p.getInstances()) {
                if (world.getName().equals(i.getInstance())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();
        getLogger().info("Purging old instance saves files");
        try {
            FileUtils.deleteRecursively(getInstanceWorldContainer());
        } catch (IOException ex) {
            Logger.getLogger(Instances.class.getName()).log(Level.WARNING, "Error purging old instance saves", ex);
        }
        getLogger().info("Loading configuration");
        load();
        getLogger().info("Registering event handlers");
        getServer().getPluginManager().registerEvents(this, this);
        File dir = getDataFolder();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                getLogger().log(Level.WARNING, "Failed to create plugin directory:  {0}", dir);
            }
        }
        getLogger().info("Creating NBTTools interface");
        try {
            this.nbtTools = CBShim.createShim(NBTTools.class, this);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error loading NBTTools.", e);
        } catch (Error e) {
            getLogger().log(Level.WARNING, "Error loading NBTTools.", e);
        }
        getLogger().info("Creating InstanceTools interface");
        try {
            this.instanceTools = CBShim.createShim(InstanceTools.class, this);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error loading InstanceTools.", e);
        } catch (Error e) {
            getLogger().log(Level.WARNING, "Error loading InstanceTools.", e);
        }
        List<DependencyFactory<?, PacketHooks>> packetHooksFactories = new ArrayList<DependencyFactory<?, PacketHooks>>();
        packetHooksFactories.add(new ProtocolLibPacketHooksFactory(this));
        packetHooks = DependencyUtil.first(getLogger(), PacketHooks.class, packetHooksFactories);
        List<DependencyFactory<?, WorldInheritance>> worldInheritanceFactories = new ArrayList<DependencyFactory<?,WorldInheritance>>();
        worldInheritanceFactories.add(new MultiverseCoreWorldInheritanceFactory(this));
        worldInheritanceFactories.add(new MultiverseInventoriesWorldInheritanceFactory(this));
        worldInheritanceFactories.add(new PEXWorldInheritanceFactory(this));
        worldInheritanceFactories.add(new WorldGuardWorldInheritanceFactory(this));
        this.worldInheritance = DependencyUtil.merge(getLogger(), WorldInheritance.class, worldInheritanceFactories);
        List<DependencyFactory<?,Bank>> bankFactories = new ArrayList<DependencyFactory<?,Bank>>();
        bankFactories.add(new VaultBankFactory(this));
        this.bank = DependencyUtil.first(getLogger(), Bank.class, bankFactories);
        List<DependencyFactory<?,CuboidSelection>> cuboidSelectionFactories = new ArrayList<DependencyFactory<?,CuboidSelection>>();
        cuboidSelectionFactories.add(new WorldEditCuboidSelectionFactory(this));
        cuboidSelectionFactories.add(new InstancesCuboidSelectionFactory(this));
        this.cuboidSelection = DependencyUtil.first(getLogger(), CuboidSelection.class, cuboidSelectionFactories);
        getLogger().info("Registering packet handler for no-op command block editing");
        try {
            packetHooks.install();
        } catch (UnsupportedOperationException e) {
            getLogger().log(Level.WARNING, "Could not install packet hooks, protocollib is required for command block editing to work.");
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error installing PacketHooks.", e);
        } catch (Error e) {
            getLogger().log(Level.WARNING, "Error installing PacketHooks.", e);
        }
    }


    @Override
    public void onDisable() {
        super.onDisable();
        save();
        clear();
        bank = null;
        worldInheritance = null;
        cuboidSelection = null;
        try {
            packetHooks.uninstall();
        } catch (UnsupportedOperationException e) {
            // We never installed any in the first place.
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error uninstalling PacketHooks.", e);
        } catch (Error e) {
            getLogger().log(Level.WARNING, "Error uninstalling PacketHooks.", e);
        }
    }

    public void clear() {
        // This will cause instances to be unloaded, and teleport
        // players out of any instances.
        for (Party p : parties.values()) {
            for (Player player : new ArrayList<Player>(p.getMembers())) {
                partyRemove(p, player);
            }
        }
        // Clear the rest of the state.
        parties.clear();
        partyMap.clear();
        homes.clear();
        sessions.clear();
        portals.clear();
        portalMap.clear();
        lastPortal.clear();
        motd = null;
    }

    public void load() {
        FileConfiguration config = getConfig();
        partyNamePrefix = config.getString("partyNamePrefix", "");
        partyNameSuffix = config.getString("partyNameSuffix", "");
        leaderInvite = config.getBoolean("leaderInvite", false);
        leaderKick = config.getBoolean("leaderKick", true);
        leaderDisband = config.getBoolean("leaderDisband", true);
        editCommandInCreative = config.getBoolean("editCommandInCreative", true);
        selectionTool = config.getItemStack("selectionTool");
        spawnName = config.getString("spawnWorld");
        ConfigurationSection homeSection = config.getConfigurationSection("homes");
        if (homeSection != null) {
            for (String s : homeSection.getKeys(false)) {
                ConfigurationSection home = homeSection.getConfigurationSection(s);
                this.homes.put(s, new HomeLocation(home));
            }
        }
        loadMotd();
        ConfigurationSection portalSection = config.getConfigurationSection("portals");
        if (portalSection != null) {
            for (String s : portalSection.getKeys(false)) {
                ConfigurationSection thisSection = portalSection.getConfigurationSection(s);
                ConfigurationSection entranceSection = thisSection.getConfigurationSection("entrance");
                ConfigurationSection destinationSection = thisSection.getConfigurationSection("destination");
                InstanceEntrancePortal entrance = new InstanceEntrancePortal(loadCuboid(entranceSection));
                InstanceDestinationPortal destination = new InstanceDestinationPortal(loadCuboid(destinationSection));
                Facing entranceFacing = loadFacing(entranceSection);
                Facing destinationFacing = loadFacing(destinationSection);
                double entryPrice = thisSection.getDouble("entryPrice");
                double createPrice = thisSection.getDouble("createPrice");
                ItemStack entryItem = thisSection.getItemStack("entryItem");
                ItemStack createItem = thisSection.getItemStack("createItem");
                int unloadTime = thisSection.getInt("unloadTime");
                int reenterTime = thisSection.getInt("recreateTime");
                int maxPlayers = thisSection.getInt("maxPlayers", 0);
                int maxInstances = thisSection.getInt("maxInstances", 0);
                Difficulty difficulty = Difficulty.valueOf(thisSection.getString("difficulty", "NORMAL"));
                World.Environment environment = World.Environment.valueOf(thisSection.getString("environment", "NORMAL"));
                String defaultParty = thisSection.getString("defaultParty", null);
                PortalPair portal = new PortalPair(s, entrance, destination, entryPrice, createPrice, entryItem, createItem, unloadTime, reenterTime, environment, difficulty, defaultParty, entranceFacing, destinationFacing, maxPlayers, maxInstances);
                ConfigurationSection playerSection = thisSection.getConfigurationSection("lastCreate");
                if (playerSection != null) {
                    for (String p : playerSection.getKeys(false)) {
                        portal.getLastCreate().put(p, playerSection.getLong(p));
                    }
                }
                addPortalPair(portal);
            }
        }
    }

    private Cuboid loadCuboid(ConfigurationSection section) {
        String world = section.getString("world");
        int minX = section.getInt("minX");
        int maxX = section.getInt("maxX");
        int minY = section.getInt("minY");
        int maxY = section.getInt("maxY");
        int minZ = section.getInt("minZ");
        int maxZ = section.getInt("maxZ");
        return new Cuboid(world, minX, maxX, minY, maxY, minZ, maxZ);
    }

    private Facing loadFacing(ConfigurationSection section) {
        if (section.contains("facing")) {
            ConfigurationSection facingSection = section.getConfigurationSection("facing");
            return new Facing(facingSection.getDouble("yaw"), facingSection.getDouble("pitch"));
        } else {
            return null;
        }
    }

    private void loadMotd() {
        File motdFile = new File(getDataFolder(), "motd.txt");
        List<String> tmp = new ArrayList<String>();
        if (motdFile.isFile()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(motdFile), CHARSET));
                String line;
                while ((line = reader.readLine()) != null) {
                    tmp.add(line);
                }
                reader.close();
            } catch (IOException ex) {
                getLogger().log(Level.SEVERE, null, ex);
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    getLogger().log(Level.SEVERE, null, ex);
                }
            }
        }
        motd = tmp.isEmpty() ? null : tmp.toArray(new String[tmp.size()]);
    }

    public void save() {
        FileConfiguration config = getConfig();
        config.set("partyNamePrefix", partyNamePrefix);
        config.set("partyNameSuffix", partyNameSuffix);
        config.set("leaderInvite", leaderInvite);
        config.set("leaderKick", leaderKick);
        config.set("leaderDisband", leaderDisband);
        config.set("editCommandInCreative", editCommandInCreative);
        if (spawnName != null) {
            config.set("spawnWorld", spawnName);
        }
        config.set("selectionTool", getSelectionTool());
        ConfigurationSection homeSection;
        if (!config.isConfigurationSection("homes")) {
            homeSection = config.createSection("homes");
        } else {
            homeSection = config.getConfigurationSection("homes");
        }
        for (Map.Entry<String, HomeLocation> e : this.homes.entrySet()) {
            ConfigurationSection home = homeSection.createSection(e.getKey());
            e.getValue().save(home);
        }
        ConfigurationSection portalSection = config.createSection("portals");
        for (PortalPair pair : portals.values()) {
            ConfigurationSection pairSection = portalSection.createSection(pair.getName());
            ConfigurationSection entranceSection = pairSection.createSection("entrance");
            ConfigurationSection destinationSection = pairSection.createSection("destination");
            saveCuboid(entranceSection, pair.getEnter().getCuboid());
            saveCuboid(destinationSection, pair.getDestination().getCuboid());
            saveFacing(entranceSection, pair.getEnter().getFacing());
            saveFacing(destinationSection, pair.getDestination().getFacing());
            pairSection.set("entryItem", pair.getEntryItem());
            pairSection.set("entryPrice", pair.getEntryPrice());
            pairSection.set("createItem", pair.getCreateItem());
            pairSection.set("createPrice", pair.getCreatePrice());
            pairSection.set("unloadTime", pair.getUnloadTime());
            pairSection.set("recreateTime", pair.getRecreateTime());
            pairSection.set("environment", pair.getEnvironment().name());
            pairSection.set("difficulty", pair.getDifficulty().name());
            if (pair.getDefaultParty() != null) {
                pairSection.set("defaultParty", pair.getDefaultParty());
            }
            pairSection.set("maxPlayers", pair.getMaxPlayers());
            pairSection.set("maxInstances", pair.getMaxInstances());
            ConfigurationSection playerSection = pairSection.createSection("lastCreate");
            for (Map.Entry<String, Long> e : pair.getLastCreate().entrySet()) {
                playerSection.set(e.getKey(), e.getValue());
            }
        }
        saveConfig();
    }

    private void saveCuboid(ConfigurationSection section, Cuboid cuboid) {
        section.set("world", cuboid.getWorld());
        section.set("minX", cuboid.getMinX());
        section.set("maxX", cuboid.getMaxX());
        section.set("minY", cuboid.getMinY());
        section.set("maxY", cuboid.getMaxY());
        section.set("minZ", cuboid.getMinZ());
        section.set("maxZ", cuboid.getMaxZ());
    }

    private void saveFacing(ConfigurationSection section, Facing facing) {
        if (facing == null) {
            return;
        }
        ConfigurationSection facingSection = section.createSection("facing");
        facingSection.set("yaw", facing.getYaw());
        facingSection.set("pitch", facing.getPitch());
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Command cmd = commands.get(command.getName());
        if (cmd == null) {
            return false;
        }
        SenderType type = SenderType.getSenderType(sender);

        try {
            List<String> result = cmd.execute(this, type, sender, args);
            if (result == null) {
                return false; // Show usage.
            } else {
                // TODO: Results paging.
                if (!result.isEmpty()) {
                    String[] msg = new String[result.size()];
                    for (int i = 0; i < result.size(); i++) {
                        msg[i] = StringUtil.success(result.get(i));
                    }
                    sender.sendMessage(msg);
                }
            }
        } catch (NotAvailableException e) {
            if (e.getMessage() == null) {
                sender.sendMessage(StringUtil.error(command.getName() + " is not available to " + type.getPluralDisplayName()));
            } else {
                sender.sendMessage(StringUtil.error(String.format(e.getMessage(), command.getName(), type.getPluralDisplayName())));
            }
        } catch (InvocationException e) {
            if (e.getMessage() == null) {
                sender.sendMessage(StringUtil.error("An error occurred with " + command.getName()));
                getLogger().log(Level.WARNING, "Error occurred executing a command", e);
            } else {
                sender.sendMessage(StringUtil.error(String.format(e.getMessage(), command.getName())));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        // TODO
        return super.onTabComplete(sender, command, alias, args);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        // Remove player from an instance if they are in one.
        Player player = e.getPlayer();
        Party party = getParty(player);
        if (party != null) {
            // Remove player from party.
            // Will teleport player out of an instance if they are in one.
            // Will cause a call to checkInstances(party) via teleport event.
            partyRemove(party, player);
            if (party.getMembers().isEmpty()) {
                partyDisband(party);
            } else {
                if (player.equals(party.getLeader())) {
                    party.setLeader(party.getMembers().iterator().next());
                    party.sendAll(
                            getPartyNamePrefix() + party.getName() + getPartyNameSuffix() + ' '
                            + player.getName() + " has quit. " + party.getLeader().getName() + " is now leader.");
                } else {
                    party.sendAll(
                            getPartyNamePrefix() + party.getName() + getPartyNameSuffix() + ' '
                            + player.getName() + " has quit.");
                }
            }
            // If party is empty, disband the party.
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Party party = getParty(e.getPlayer());
        // Should run after teleport has completed.
        if (party != null) {
            scheduleCheckInstances(party);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (spawnName != null) {
            e.setRespawnLocation(getSpawn().getSpawnLocation());
        }
        Party party = getParty(e.getPlayer());
        // Should run after the player has respawned (technically a teleport).
        if (party != null) {
            scheduleCheckInstances(party);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        String fromWorld = e.getFrom().getWorld().getName();
        // Translate world names for players in instances.
        Party party = getParty(player);
        Coord from = Coord.fromLocation(e.getFrom());
        Coord to = Coord.fromLocation(e.getTo());
        if (from.equals(to)) {
            return;
        }
        // Disable portals for players in a vehicle.
        if (player.isInsideVehicle()) {
            return;
        }

        boolean isInstance = false;
        if (party != null) {
            Instance instance = party.getInstanceFromInstanceWorld(fromWorld);
            if (instance != null) {
                fromWorld = instance.getSourceWorld();
                isInstance = true;
            }
        }

        // Check if they entered or left any portals in that world.
        List<Portal> pList = portalMap.get(fromWorld);
        if (pList != null) {
            for (Portal p : pList) {
                if (isInstance == p.isDestination()) {
                    boolean fromInside = p.getCuboid().contains(from);
                    boolean toInside = p.getCuboid().contains(to);
                    if (!fromInside && toInside) {
                        p.onEnter(this, e);
                        break;
                    } else if (fromInside && !toInside) {
                        p.onLeave(this, e);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLogin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("instances.general.motd")) {
            if (motd != null) {
                e.getPlayer().sendMessage(motd);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onClick(PlayerInteractEvent e) {
        if (getCuboidSelection().isNative()) {
            Player p = e.getPlayer();
            if (getSelectionTool() != null && getSelectionTool().equals(p.getItemInHand()) && p.hasPermission("instances.portal.create")) {
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    Block b = e.getClickedBlock();
                    Location l = b.getLocation();
                    getSelection(
                            p).setFrom(l);
                    p.sendMessage("Selection area location from: " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
                    e.setCancelled(true);
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block b = e.getClickedBlock();
                    Location l = b.getLocation();
                    getSelection(
                            p).setTo(l);
                    p.sendMessage("Selection area location to: " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
                    e.setCancelled(true);
                }
            }
        }
    }

    public void removePlayerFromInstance(Player player) {
        Party party = getParty(player);
        if (party != null) {
            Instance instance = party.getInstance(player);
            if (instance != null) {
                // Teleport them to the last instance entrance they used.
                InstanceEntrancePortal portal = lastPortal.get(player);
                if (portal != null) {
                    portal.teleport(this, player);
                } else {
                    teleportToSpawn(player);
                }
            }
        }
    }

    public void teleportToSpawn(Player player) {
        if (spawnName != null) {
            player.teleport(getSpawn().getSpawnLocation());
        } else {
            // If we don't know about the spawn world, just teleport them to the first world in the list.
            player.teleport(getServer().getWorlds().get(0).getSpawnLocation());
        }
    }

    private void scheduleCheckInstances(final Party party) {
        getServer().getScheduler().runTask(this,
                new Runnable() {

                    public void run() {
                        checkInstances(party);
                    }
                });
    }

    private void checkInstances(final Party party) {
        List<Instance> instances = new LinkedList(party.getInstances());
        for (Player p : party.getMembers()) {
            Instance i = party.getInstance(p);
            if (i != null) {
                i.cancelDelete();
                instances.remove(i);
            }
        }
        for (Instance i : instances) {
            if (i.getPortal().getUnloadTime() > 0) {
                BukkitTask task = i.getDeleteTask();
                if (i.getDeleteTask() == null) {
                    final Instance ii = i;
                    i.setDeleteTask(getServer().getScheduler().runTaskLater(this, new Runnable() {

                        public void run() {
                            party.removeInstance(ii);
                            deleteInstance(ii);
                        }
                    }, i.getPortal().getUnloadTime()));
                }
            } else {
                party.removeInstance(i);
                deleteInstance(i);
            }
        }
    }

    private void deleteInstance(Instance instance) {
        // Make sure we're not called twice.
        instance.cancelDelete();
        // Drop the world from the server, teleporting anyone inside it out.
        World world = getServer().getWorld(instance.getInstance());
        if (world != null) {
            getLogger().log(Level.INFO, "Deleting instance: {0}", instance);
            // Remove all players from the world.
            for (Player p : world.getPlayers()) {
                teleportToSpawn(p);
            }
            // Remove world inheritance.
            getWorldInheritance().preRemoveInheritance(instance.getSourceWorld(), instance.getInstance());

            // Finally delete the instance without saving.
            InstanceTools tools = getInstanceTools();
            if (tools != null) {
                tools.unloadWorld(this, world);
            }

            // Remove any world inheritance.
            getWorldInheritance().postRemoveInheritance(instance.getSourceWorld(), instance.getInstance());

            if (instanceMap.containsKey(instance.getPortal().getName())) {
                instanceMap.get(instance.getPortal().getName()).remove(instance);
            }
        }
    }

    public boolean getEditCommandInCreative() {
        return editCommandInCreative;
    }
}
