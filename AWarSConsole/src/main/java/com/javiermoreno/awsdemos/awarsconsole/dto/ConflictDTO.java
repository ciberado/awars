/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.awarsconsole.dto;

import com.javiermoreno.awsdemos.awarsconsole.domain.ConflictStats;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author ciberado
 */
public class ConflictDTO implements Serializable {
    private static final long serialVersionUID = 1;
    
    private static  Map<String, Double> instanceInfo;
    
    private String defender;
    private String attacker;
    private double machineWeight;
    private int machinesCount;
    private double requestPerSecond;
    
    public ConflictDTO(ConflictStats stats) {
        this.defender = stats.getDefenderName();
        this.attacker = stats.getAttackerName();
        this.machinesCount = stats.getMachinesCount();
        this.machineWeight = getWeight(stats.getMachinesType());
        this.requestPerSecond = stats.getTotalRequestPerSecond();
    }
    
    private static double getWeight(String type) {
        if (instanceInfo == null) {
            instanceInfo = new HashMap<String, Double>();
            for (int i=0; i < INSTANCES.length; i = i + 2) {
                instanceInfo.put((String) INSTANCES[i], ((Number) INSTANCES[i+1]).doubleValue());
            }
        }
        
        Double result =  instanceInfo.get(type);
        return result == null ? 0.25 : result;
    }

    public String getDefender() {
        return defender;
    }

    public void setDefender(String defender) {
        this.defender = defender;
    }

    public String getAttacker() {
        return attacker;
    }

    public void setAttacker(String attacker) {
        this.attacker = attacker;
    }

    public double getMachineWeight() {
        return machineWeight;
    }

    public void setMachineWeight(double machineWeight) {
        this.machineWeight = machineWeight;
    }

    public int getMachinesCount() {
        return machinesCount;
    }

    public void setMachinesCount(int machinesCount) {
        this.machinesCount = machinesCount;
    }

    public double getRequestPerSecond() {
        return requestPerSecond;
    }

    public void setRequestPerSecond(double requestPerSecond) {
        this.requestPerSecond = requestPerSecond;
    }
    
    
    
public static final Object[] INSTANCES = {    
    "t1.micro", 0.25, 
    "m1.small", 1,
    "m3.medium", 3,
    "m1.medium", 2,
    "c1.medium", 5,
    "c3.large", 7,
    "m3.large", 6.5,
    "m1.large", 4,
    "c3.xlarge", 14, 
    "m2.xlarge", 6.5,
    "m3.xlarge", 13,
    "m1.xlarge", 8,
    "c1.xlarge", 20,
    "c3.2xlarge", 28,
    "g2.2xlarge", 26,
    "m2.2xlarge", 13,
    "i2.xlarge", 14,
    "m3.2xlarge", 26,
    "c3.4xlarge", 55,
    "m2.4xlarge", 26,
    "i2.2xlarge", 27,
    "cg1.4xlarge", 33.5,
    "c3.8xlarge", 108,
    "cc2.8xlarge", 88,
    "hi1.4xlarge", 35,
    "i2.4xlarge", 53,
    "cr1.8xlarge", 88,
    "hs1.8xlarge", 35,
    "i2.8xlarge", 104};

    
    
    
}
