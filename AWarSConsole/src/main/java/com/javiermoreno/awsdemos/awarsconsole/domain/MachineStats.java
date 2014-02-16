/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.awarsconsole.domain;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author ciberado
 */
public class MachineStats implements Serializable {

    private static final long serialVersionUID = 1;
    
    @JsonIgnore
    private String id;
    private String type;
    private long lastLectureTimestamp;
    private double lastAvgResponseTimeMs;
    private double lastRequestCountPerSecond;

    public MachineStats() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getLastLectureTimestamp() {
        return lastLectureTimestamp;
    }

    public void setLastLectureTimestamp(long lastLectureTimestamp) {
        this.lastLectureTimestamp = lastLectureTimestamp;
    }

    public double getLastAvgResponseTimeMs() {
        return lastAvgResponseTimeMs;
    }

    public void setLastAvgResponseTimeMs(double lastAvgResponseTimeMs) {
        this.lastAvgResponseTimeMs = lastAvgResponseTimeMs;
    }

    public double getLastRequestCountPerSecond() {
        return lastRequestCountPerSecond;
    }

    public void setLastRequestCountPerSecond(double lastRequestCountPerSecond) {
        this.lastRequestCountPerSecond = lastRequestCountPerSecond;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final MachineStats other = (MachineStats) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MachineStats{" + "id=" + id + ", type=" + type + ", lastLectureTimestamp=" + lastLectureTimestamp + ", lastAvgResponseTimeMs=" + lastAvgResponseTimeMs + ", lastRequestCountPerSecond=" + lastRequestCountPerSecond + '}';
    }
    
    
}
