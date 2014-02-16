package com.javiermoreno.awsdemos.awarsconsole.dto;

import com.javiermoreno.awsdemos.awarsconsole.domain.ApplicationStats;
import com.javiermoreno.awsdemos.awarsconsole.domain.MachineStats;
import com.javiermoreno.awsdemos.awarsconsole.domain.WarriorStats;
import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author ciberado
 */
public class ConflictPageDTO implements Serializable {
    private static final long serialVersionUID = 1;
    
    public static final long MACHINE_TIMEOUT = 1000*40;
    
    @JsonProperty("country")
    private String warId;
    private String instanceType;
    private int instanceCount;
    private double requestBySecond;
    private double avgResponseTimeMs;

    public ConflictPageDTO(WarriorStats warriorStats) {
        this.warId = warriorStats.getId();
        double avgResponseTimeMsSum = 0;
        for (MachineStats machineStats : warriorStats.getMachineStats().values()) {
            if (System.currentTimeMillis() - machineStats.getLastLectureTimestamp() > MACHINE_TIMEOUT) {
                continue;
            }
            if (instanceType == null) {
                instanceType = machineStats.getType();
            }
            instanceCount = instanceCount + 1;
            requestBySecond = requestBySecond + machineStats.getLastRequestCountPerSecond();
            avgResponseTimeMsSum = avgResponseTimeMsSum + machineStats.getLastAvgResponseTimeMs();
        }
        avgResponseTimeMs = (instanceCount == 0) ? Double.NaN : avgResponseTimeMsSum / instanceCount;
    }

    public String getWarId() {
        return warId;
    }

    public void setWarId(String warId) {
        this.warId = warId;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }

    public double getRequestBySecond() {
        return requestBySecond;
    }

    public void setRequestBySecond(double requestBySecond) {
        this.requestBySecond = requestBySecond;
    }

    public double getAvgResponseTimeMs() {
        return avgResponseTimeMs;
    }

    public void setAvgResponseTimeMs(double avgResponseTimeMs) {
        this.avgResponseTimeMs = avgResponseTimeMs;
    }
    
    
    
}
