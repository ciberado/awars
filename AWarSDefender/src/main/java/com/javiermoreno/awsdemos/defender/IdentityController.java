/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.defender;

import com.javiermoreno.awsdemos.awarscommon.IdentityDTO;
import com.javiermoreno.awsdemos.awarscommon.MetadataService;
import com.javiermoreno.awsdemos.awarscommon.MetadataServiceImplAWS;
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
    
    private MetadataService metadataService;

    public IdentityController() {
        metadataService = new MetadataServiceImplAWS();
    }
    
    
    
    @GET
    @Path("/")
    @Produces("application/json; charset=utf-8")
    public IdentityDTO calculatePI() {
        String warId = System.getenv("WAR_ID");
        if (warId == null) {
            warId = System.getProperty("WAR_ID");
        }
        if (warId == null) {
            warId = DEFAULT_WAR_ID;
        }
        return new IdentityDTO(warId, metadataService.getInstanceName());
    }
    
    
}
