/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.awarsconsole.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author ciberado
 */
public class ConflictStats implements Serializable {
    private static final long serialVersionUID = 1;
    
    private String attackerName;
    private String defenderName;
    private long startTimestamp;
    private long endTimestamp;
    
    @JsonProperty("applicationStats")
    private Map<String, ApplicationStats> appStats = new HashMap<String, ApplicationStats>();

    public ConflictStats(String attacker, String defender) {
        this.attackerName = attacker;
        this.defenderName = defender;
        this.startTimestamp = Long.MAX_VALUE;
        this.endTimestamp = 0;
    }

    public void updateApplicationStats(String appName, String warId, String opponentWarId, String instanceId, String instanceType,
                                       long startTimestamp, long endTimestamp, int requestCount, double avgResponseTimeMs) {
        if (this.startTimestamp > startTimestamp) {
            this.startTimestamp = startTimestamp;
        }
        if (this.endTimestamp < endTimestamp) {
            this.endTimestamp = endTimestamp;
        }
        
        ApplicationStats appStats = this.appStats.get(appName);
        if (appStats == null) {
            appStats = new ApplicationStats();
            appStats.setId(appName);
            this.appStats.put(appName, appStats);
        }
        appStats.updateWarriorStats(warId, instanceId, instanceType, 
                                    startTimestamp, endTimestamp, requestCount, avgResponseTimeMs);
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

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    

    public Map<String, ApplicationStats> getAppStats() {
        return appStats;
    }

    
    
    
}
