/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.awarsconsole.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author ciberado
 */
public class WarriorStats implements Serializable {
    private static final long serialVersionUID = 1;
    
    @JsonIgnore
    private String id;
    @JsonProperty("machineStats")
    private Map<String, MachineStats> stats = new HashMap<String, MachineStats>();

    public WarriorStats() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, MachineStats> getMachineStats() {
        return stats;
    }

    
    public void updateMachineStats(String instanceId, String instanceType, 
                                   double requestCount, double avgResponseTimeMs) {
        MachineStats machineStats = stats.get(id);
        if (machineStats == null) {
            machineStats = new MachineStats();
            machineStats.setId(instanceId);
            machineStats.setType(instanceType);
            stats.put(instanceId, machineStats);
        }
        machineStats.setLastLectureTimestamp(new Date().getTime());
        machineStats.setLastRequestCountPerSecond(requestCount);
        machineStats.setLastAvgResponseTimeMs(avgResponseTimeMs);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WarriorStats other = (WarriorStats) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CustomerStats{" + "id=" + id + ", size=" + stats.size() + ", stats=" + stats + '}';
    }
    
    
    
}
