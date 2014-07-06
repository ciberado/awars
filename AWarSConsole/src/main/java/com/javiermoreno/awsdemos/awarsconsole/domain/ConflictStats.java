/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.awarsconsole.domain;

import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author ciberado
 */
public class ConflictStats implements Serializable {
    private static final long serialVersionUID = 1;
    
    private String attackerName;
    private String defenderName; 

    @JsonProperty("machines")
    private Map<String, MachineStats> machineStats = new PassiveExpiringMap<String, MachineStats>(1000*60);

    public ConflictStats(String attacker, String defender) {
        this.attackerName = attacker;
        this.defenderName = defender;
    }

    public void updateStats(String instanceId, String instanceType, double requestPerSecond) {
        String appName = "Defender";
        MachineStats stats = new MachineStats();
        stats.setId(instanceId);
        stats.setType(instanceType);
        stats.setRequestPerSecond(requestPerSecond);
        machineStats.put(instanceId, stats);
    }

    public String getAttackerName() {
        return attackerName;
    }

    public void setAttackerName(String attackerName) {
        this.attackerName = attackerName;
    }

    public String getDefenderName() {
        return defenderName;
    }

    public void setDefenderName(String defenderName) {
        this.defenderName = defenderName;
    }

    public Map<String, MachineStats> getMachineStats() {
        return machineStats;
    }

    public int getMachinesCount() {
        return machineStats.size();
    }
    
    public String getMachinesType() {
        if (machineStats.size() == 0) {
            return null;
        } else {
            return machineStats.values().iterator().next().getType();
        }
    }
    
    public double getTotalRequestPerSecond() {
        double sum = 0;
        for (MachineStats machineStats : machineStats.values()) {
            sum = sum + machineStats.getRequestPerSecond();
        }
        return sum;
    }
    
}
