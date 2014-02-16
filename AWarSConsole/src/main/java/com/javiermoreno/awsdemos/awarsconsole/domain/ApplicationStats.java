package com.javiermoreno.awsdemos.awarsconsole.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author ciberado
 */
public class ApplicationStats implements Serializable {
    private static final long serialVersionUID = 1;
    
    @JsonIgnore
    private String id;
    @JsonProperty("warriorsStats")
    private Map<String, WarriorStats> warriorsStats = new HashMap<String, WarriorStats>();

    public ApplicationStats() {
    }

    public void updateWarriorStats(String warId, String instanceId, String instanceType,
                                    long startTimestamp, long endTimestamp,
                                    int requestCount, double avgResponseTimeMs) {
        WarriorStats warriorStats = warriorsStats.get(warId);
        if (warriorStats == null) {
            warriorStats = new WarriorStats();
            warriorStats.setId(warId);
            warriorsStats.put(warId, warriorStats);
        }
        long timeDiff = endTimestamp - startTimestamp;
        double requestCountPerSecond = requestCount / (timeDiff / 1000.0);
        warriorStats.updateMachineStats(instanceId, instanceType, requestCountPerSecond, avgResponseTimeMs);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, WarriorStats> getWarriorsStats() {
        return warriorsStats;
    }
    
    
    
    
}
