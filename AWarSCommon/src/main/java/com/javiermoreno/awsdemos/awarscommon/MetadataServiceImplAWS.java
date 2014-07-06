/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.awarscommon;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 *
 * @author ciberado
 */
public class MetadataServiceImplAWS implements MetadataService {

    private Client client;

    public MetadataServiceImplAWS() {
        client = ClientBuilder.newClient();
    }
    
    @Override
    public String getInstanceName() {
        String instanceId = getEC2Metadata("http://169.254.169.254/latest/meta-data/instance-id");
        return instanceId;
    }
    
    @Override
    public String getInstanceType() {
        String instanceType = getEC2Metadata("http://169.254.169.254/latest/meta-data/instance-type");
        return instanceType;
    }
 
            
    private String getEC2Metadata(String key) {
        try {
            System.out.format("LOG: Retreiving %s from metadata.%n", key);
            String result = client.target(key).request().get(String.class);
            System.out.format("LOG: Returned value is %s.%n", result);
            return result;
        } catch (javax.ws.rs.ProcessingException exc) {
            return null;
        }
    }            
}
