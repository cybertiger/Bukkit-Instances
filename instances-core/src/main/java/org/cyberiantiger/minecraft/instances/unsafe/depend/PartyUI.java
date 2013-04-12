/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Party;
import org.cyberiantiger.minecraft.instances.util.Dependency;

/**
 *
 * @author antony
 */
public interface PartyUI extends Dependency {

    public void init();

    public void createParty(Party party);

    public void removeParty(Party party);

    public void addMember(Party party, Player member);

    public void removeMember(Party party, Player member);
    
}
