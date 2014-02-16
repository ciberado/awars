/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.defender;

import com.javiermoreno.awsdemos.awarscommon.IdentityDTO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author ciberado
 */
@Path("/identity")
public class IdentityController {
    public static final String DEFAULT_WAR_ID = "ARGENTINA";
    
    @GET
    @Path("/")
    @Produces("application/json; charset=utf-8")
    public IdentityDTO calculatePI() {
        String warId = System.getenv("WAR_ID") == null ? DEFAULT_WAR_ID : System.getenv("WAR_ID");
        return new IdentityDTO(warId);
    }
    
    
}
