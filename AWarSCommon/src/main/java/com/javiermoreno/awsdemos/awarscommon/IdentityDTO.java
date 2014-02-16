/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.awarscommon;

import java.io.Serializable;

/**
 *
 * @author ciberado
 */
public class IdentityDTO implements Serializable {
    private static final long serialVersionUID = 1;
    private String warId;

    public IdentityDTO() {
    }

    public IdentityDTO(String warId) {
        this.warId = warId;
    }

    public String getWarId() {
        return warId;
    }

    public void setWarId(String warId) {
        this.warId = warId;
    }
    
    
}
