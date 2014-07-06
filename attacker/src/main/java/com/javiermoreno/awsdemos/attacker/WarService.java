/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.attacker;

import java.util.Date;

/**
 *
 * @author ciberado
 */
public interface WarService {
    
    void addListener(WarServiceListener ls);

    void attack();

    String getDefenderWarId();

    int getDesiredThreadNumber();
    
    void setDesiredThreadNumber(int n);
    
    long getLastLatencyMs();
    
    class RequestCompletedEvent {

        private int status;
        private long responseTimeMs;
        private Date timestamp;

        public RequestCompletedEvent(int status, long responseTimeMs) {
            this.status = status;
            this.responseTimeMs = responseTimeMs;
            this.timestamp = new Date();
        }

        public int getStatus() {
            return status;
        }

        public long getResponseTimeMs() {
            return responseTimeMs;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }


    interface WarServiceListener {
        void requestCompleted(RequestCompletedEvent evt);
    }
    
}
