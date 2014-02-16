package com.javiermoreno.awsdemos.defender;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author ciberado
 */
public class PIResponseDTO implements Serializable {
    String pi;
    Date timestamp;

    public PIResponseDTO() {
    }
    
    public PIResponseDTO(BigDecimal pi) {
        this.pi = String.valueOf(pi);
        this.timestamp = new Date();
    }

    public String getPi() {
        return pi;
    }

    public void setPi(String pi) {
        this.pi = pi;
    }
    

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    
}
