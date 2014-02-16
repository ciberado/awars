/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.awarsconsole.dto;

import com.javiermoreno.awsdemos.awarsconsole.domain.ApplicationStats;
import com.javiermoreno.awsdemos.awarsconsole.domain.ConflictStats;
import com.javiermoreno.awsdemos.awarsconsole.domain.WarriorStats;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author ciberado
 */
public class ConflictsReportDTO implements Serializable {
    public static final long serialVersionUID = 1;
    
    private Date start;
    private Date finish;
    @JsonProperty("data")
    private List<ConflictDTO> conflictsResumes = new ArrayList<ConflictDTO>();

    public ConflictsReportDTO() {
    }

    public void addConflictDTO(ConflictStats conflictStats) {
        if (start == null || conflictStats.getStartTimestamp() < start.getTime()) {
            start = new Date(conflictStats.getStartTimestamp());
        }
        if (finish == null || conflictStats.getEndTimestamp() > finish.getTime()) {
            finish = new Date(conflictStats.getEndTimestamp());
        }

        // TODO: My eyeeeeees! Refactor this asap.
        ApplicationStats clientAppStats = conflictStats.getAppStats().get("client");
        ApplicationStats serverAppStats = conflictStats.getAppStats().get("server");
        if (clientAppStats != null && serverAppStats != null) {
            WarriorStats attacker = clientAppStats.getWarriorsStats().values().iterator().next();
            WarriorStats defender = serverAppStats.getWarriorsStats().values().iterator().next();
            ConflictDTO dto = new ConflictDTO(attacker, defender);
            this.conflictsResumes.add(dto);
        }
    }

    public List<ConflictDTO> getConflictsResumes() {
        return conflictsResumes;
    }

    public Date getFinish() {
        return finish;
    }

    public Date getStart() {
        return start;
    }
    
    
    
}
