/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.awarscommon;

/**
 *
 * @author ciberado
 */
public class IdentityDTO {
    String warId;
    String instanceId;

    public IdentityDTO() {
    }

    public IdentityDTO(String warId, String instanceId) {
        this.warId = warId;
        this.instanceId = instanceId;
    }

    public String getWarId() {
        return warId;
    }

    public void setWarId(String warId) {
        this.warId = warId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    
}
