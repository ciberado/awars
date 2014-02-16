/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.defender;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author ciberado
 */
@Path("/mathematics")
public class CalculatorController {
    
    private final CalculatorService service = new CalculatorServiceImplHighPrecision();
    
    @GET
    @Path("/pi")
    @Produces("application/json; charset=utf-8")
    public PIResponseDTO calculatePI() {
        PIResponseDTO dto = new PIResponseDTO(service.calculatePI());
        return dto;
    }
    
}
