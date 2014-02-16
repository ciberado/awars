/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.awarsconsole.dto;

import com.javiermoreno.awsdemos.awarsconsole.domain.ConflictStats;
import com.javiermoreno.awsdemos.awarsconsole.domain.WarriorStats;
import java.io.Serializable;

/**
 *
 * @author ciberado
 */
class ConflictDTO implements Serializable {
    private static final long serialVersionUID = 1;
    
    private ConflictPageDTO client;
    private ConflictPageDTO server;
    
    public ConflictDTO(WarriorStats attacker, WarriorStats defender) {
        this.client = new ConflictPageDTO(attacker);
        this.server = new ConflictPageDTO(defender);
    }

    public ConflictPageDTO getClient() {
        return client;
    }

    public ConflictPageDTO getServer() {
        return server;
    }
    
    
    
}
