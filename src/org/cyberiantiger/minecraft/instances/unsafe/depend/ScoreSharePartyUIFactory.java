/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.Party;
import org.cyberiantiger.minecraft.instances.util.DependencyFactory;
import org.cyberiantiger.minecraft.scoreshare.api.AbstractObjectiveProvider;
import org.cyberiantiger.minecraft.scoreshare.api.AbstractTeamProvider;
import org.cyberiantiger.minecraft.scoreshare.api.ObjectiveProvider;
import org.cyberiantiger.minecraft.scoreshare.api.ObjectiveProviderFactory;
import org.cyberiantiger.minecraft.scoreshare.api.Team;
import org.cyberiantiger.minecraft.scoreshare.api.TeamProvider;
import org.cyberiantiger.minecraft.scoreshare.api.TeamProviderFactory;

/**
 *
 * @author antony
 */
public class ScoreSharePartyUIFactory extends DependencyFactory<Instances, PartyUI> {
    private static final String PLUGIN_NAME = "ScoreShare";

    public ScoreSharePartyUIFactory(Instances parent) {
        super(parent, PLUGIN_NAME);
    }

    @Override
    public Class<PartyUI> getInterfaceClass() {
        return PartyUI.class;
    }

    @Override
    protected PartyUI createInterface(Plugin plugin) throws Exception {
        ScoreSharePartyUI partyUi = new ScoreSharePartyUI(plugin);
        getThisPlugin().getServer().getServicesManager().register(TeamProviderFactory.class, partyUi.teamProviderFactory, plugin, ServicePriority.Low);
        getThisPlugin().getServer().getServicesManager().register(ObjectiveProviderFactory.class, partyUi.objectiveProviderFactory, plugin, ServicePriority.Low);
        return partyUi;
    }

    private class ScoreSharePartyUI implements PartyUI, Listener {

        private final Plugin plugin;
        private final Map<Player, InstancesObjectiveProvider> objectiveProviders = new WeakHashMap<Player, InstancesObjectiveProvider>();
        private final Map<Player, InstancesTeamProvider> teamProviders = new WeakHashMap<Player, InstancesTeamProvider>();

        public final TeamProviderFactory<Instances> teamProviderFactory = new TeamProviderFactory<Instances>() {

            public Instances getPlugin() {
                return getThisPlugin();
            }

            public TeamProvider<Instances> getTeamProvider(Player player) {
                InstancesTeamProvider ret = teamProviders.get(player);
                if (ret == null) {
                    ret = new InstancesTeamProvider(player.getName());
                    teamProviders.put(player, ret);
                }
                return ret;
            }
        };

        public final ObjectiveProviderFactory<Instances> objectiveProviderFactory = new ObjectiveProviderFactory<Instances>() {

            public Instances getPlugin() {
                return getThisPlugin();
            }

            public ObjectiveProvider<Instances> getProvider(Player player) {
                InstancesObjectiveProvider ret = objectiveProviders.get(player);
                if (ret == null) {
                    ret = new InstancesObjectiveProvider(player.getName());
                    objectiveProviders.put(player, ret);
                }
                return ret;
            }
        };

        public ScoreSharePartyUI(Plugin plugin) {
            this.plugin = plugin;
            getThisPlugin().getServer().getPluginManager().registerEvents(this, getThisPlugin());
        }

        public void init() {
        }

        public void createParty(Party party) {
            for (InstancesObjectiveProvider o : objectiveProviders.values()) {
                o.createParty(party);
            }
            for (InstancesTeamProvider t : teamProviders.values()) {
                t.createParty(party);
            }
        }

        public void removeParty(Party party) {
            for (InstancesObjectiveProvider o : objectiveProviders.values()) {
                o.removeParty(party);
            }
            for (InstancesTeamProvider t : teamProviders.values()) {
                t.removeParty(party);
            }
        }

        public void addMember(Party party, Player member) {
            for (InstancesObjectiveProvider o : objectiveProviders.values()) {
                o.addMember(party, member);
            }
            for (InstancesTeamProvider t : teamProviders.values()) {
                t.addMember(party, member);
            }
        }

        public void removeMember(Party party, Player member) {
            for (InstancesObjectiveProvider o : objectiveProviders.values()) {
                o.removeMember(party, member);
            }
            for (InstancesTeamProvider t : teamProviders.values()) {
                t.removeMember(party, member);
            }
        }

        public Plugin getPlugin() {
            return plugin;
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public void onEntityRegainHealth(EntityRegainHealthEvent e) {
            if (e.getEntity().getType() == EntityType.PLAYER) {
                final Player player = (Player) e.getEntity();
                final Party party = getThisPlugin().getParty(player);
                if (party != null) {
                    getThisPlugin().getServer().getScheduler().runTask(getThisPlugin(), new Runnable() {
                        
                        public void run() {
                            for (Player member : party.getMembers()) {
                                InstancesObjectiveProvider provider = objectiveProviders.get(member);
                                if (provider != null)
                                    provider.updateHealth(player);
                            }
                        }
                    });
                }
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public void onEntityDamage(EntityDamageEvent e) {
            if (e.getEntity().getType() == EntityType.PLAYER) {
                final Player player = (Player) e.getEntity();
                final Party party = getThisPlugin().getParty(player);
                if (party != null) {
                    getThisPlugin().getServer().getScheduler().runTask(getThisPlugin(), new Runnable() {
                        public void run() {
                            for (Player member : party.getMembers()) {
                                InstancesObjectiveProvider provider = objectiveProviders.get(member);
                                if (provider != null)
                                    provider.updateHealth(player);
                            }
                        }
                    });
                }
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public void onPlayerRespawn(PlayerRespawnEvent e) {
            final Player player = e.getPlayer();
            final Party party = getThisPlugin().getParty(player);
            if (party != null) {
                getThisPlugin().getServer().getScheduler().runTask(getThisPlugin(), new Runnable() {
                    public void run() {
                        for (Player member : party.getMembers()) {
                            InstancesObjectiveProvider provider = objectiveProviders.get(member);
                            if (provider != null)
                                provider.updateHealth(player);
                        }
                    }
                });
            }
        }
    }

    private class InstancesTeamProvider extends AbstractTeamProvider<Instances> implements PartyUI {
        private final String user;
        private String partyName = null;

        public InstancesTeamProvider(String user) {
            super(getThisPlugin(), "parties");
            this.user = user;
        }

        public Collection<Team> getTeams() {
            Player player = getPlugin().getServer().getPlayer(user);
            if (player == null) {
                return Collections.emptyList();
            }
            Party party = getPlugin().getParty(player);
            if (party == null) {
                return Collections.emptyList();
            }
            partyName = party.getName();
            Team team = new Team(party.getName());
            team.setPrefix("§b");
            for (Player p : party.getMembers()) {
                team.addMemberName(p.getName());
            }
            return Collections.singleton(team);
        }

        public void createParty(Party party) {
        }

        public void removeParty(Party party) {
        }

        public void addMember(Party party, Player member) {
            if (member.getName().equals(user)) {
                partyName = party.getName();
                fireAddTeam(partyName);
                fireModifyTeamPrefix(partyName, "§b");
                for (Player p : party.getMembers()) {
                    fireAddTeamMember(partyName, p.getName());
                }
            } else if (party.getName().equals(partyName)) {
                fireAddTeamMember(partyName, member.getName());
            }
        }

        public void removeMember(Party party, Player member) {
            if (member.getName().equals(user)) {
                fireRemoveTeam(partyName);
                partyName = null;
            } else if (party.getName().equals(partyName)) {
                fireRemoveTeamMember(partyName, member.getName());
            }
        }

        public void init() {
        }
    }

    private class InstancesObjectiveProvider extends AbstractObjectiveProvider<Instances> implements PartyUI {
        private final String user;
        private String partyName = null;

        public InstancesObjectiveProvider(String user) {
            super(getThisPlugin(), "partyHealth", "Health", "partyHealth");
            this.user = user;
        }

        public Map<String, Integer> getScores() {
            Player player = getPlugin().getServer().getPlayer(user);
            if (player == null) {
                return Collections.emptyMap();
            }
            Party party = getPlugin().getParty(player);
            if (party == null) {
                return Collections.emptyMap();
            }
            partyName = party.getName();
            Map<String, Integer> result = new HashMap<String, Integer>();
            for (Player p : party.getMembers()) {
                result.put(p.getName(), p.getHealth());
            }
            return result;
        }

        public void createParty(Party party) {
        }

        public void removeParty(Party party) {
        }

        public void addMember(Party party, Player member) {
            if (member.getName().equals(user)) {
                partyName = party.getName();
                for (Player p : party.getMembers()) {
                    firePutScore(p.getName(), p.getHealth());
                }
            } else if (party.getName().equals(partyName)) {
                firePutScore(member.getName(), member.getHealth());
            }
        }

        public void removeMember(Party party, Player member) {
            if (member.getName().equals(user)) {
                for (Player p : party.getMembers()) {
                    fireRemoveScore(p.getName());
                }
                partyName = null;
            } else if (party.getName().equals(partyName)) {
                fireRemoveScore(member.getName());
            }
        }

        public void init() {
        }

        public void updateHealth(Player player) {
            if (partyName == null) {
                return;
            }
            Party party = getPlugin().getParty(player);
            if (party == null || !partyName.equals(party.getName())) {
                return;
            }
            firePutScore(player.getName(), player.getHealth());
        }
    }
}
