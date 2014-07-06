/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javiermoreno.awsdemos.defender;

import com.javiermoreno.awsdemos.awarscommon.MetadataService;
import com.javiermoreno.awsdemos.awarscommon.MetadataServiceImplAWS;
import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;

/**
 *
 * @author ciberado
 */
@WebFilter(filterName = "TelemetryFilter",
        urlPatterns = {"/api/mathematics/*"},
        dispatcherTypes = {DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.REQUEST, DispatcherType.INCLUDE})
public class SimpleTelemetryFilter implements Filter {

    private static final String APP_NAME = "DEFENDER";
    private static final String DEFAULT_METRICS_SERVER_URL = "http://localhost:8080/awarsconsole/api";
    private static final String DEFAULT_WAR_ID = "ARGENTINA";
    private static final String DEFAULT_INSTANCE_ID = "TEST_INSTANCE_ID";
    private static final String DEFAULT_INSTANCE_TYPE = "TEST_INSTANCE_TYPE";
    private static final long TIMEOUT = 1000 * 10;

    private MetadataService metadataService;
    
    private Client client;
    private String defenderWarId;
    private String metricsServerUrl;
    private String instanceId;
    private String instanceType;

    private boolean run;
    private String attackerWarId;
    private long requestCount;
    private long sum;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        metadataService = new MetadataServiceImplAWS();
        client = ClientBuilder.newClient();

        defenderWarId = System.getenv("WAR_ID") == null ? DEFAULT_WAR_ID : System.getenv("WAR_ID");
        metricsServerUrl = System.getenv("METRICS_URL") == null ? DEFAULT_METRICS_SERVER_URL : System.getenv("METRICS_URL");
        instanceId = metadataService.getInstanceName();
        if (instanceId == null) {
            instanceId = DEFAULT_INSTANCE_ID;
        }
        instanceType = metadataService.getInstanceType();
        if (instanceType == null) {
            instanceType = DEFAULT_INSTANCE_TYPE;
        }
        this.run = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (run == true) {
                    try {
                        Thread.sleep(1000 * 10);
                    } catch (InterruptedException ex) {
                    }
                    postStats();
                    requestCount = 0;
                    sum = 0;
                }
            }
        }).start();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long t0 = System.currentTimeMillis();

        attackerWarId = request.getParameter("attackerWarId");

        chain.doFilter(request, response);

        long tf = System.currentTimeMillis();
        long diff = tf - t0;
        requestCount = requestCount + 1;
        sum = sum + diff;
    }

    private void postStats() {
        if (requestCount == 0) return;
        
        double rps  = 1000 / (sum / (double) requestCount);
        System.out.format("LOG: Posting stats: %s (%s/%s) vs %s: %f rps.%n", 
                          defenderWarId, instanceId, instanceType, attackerWarId, rps);
        client
                .target(this.metricsServerUrl)
                .path("/metrics/defenders/{defenderWarId}/instances/{instanceId}")
                .resolveTemplate("defenderWarId", defenderWarId)
                .resolveTemplate("instanceId", instanceId)
                .queryParam("attackerWarId", attackerWarId)
                .queryParam("instanceType", instanceType)
                .queryParam("requestPerSecond", rps)
                .request()
                .async()
                .post(null, new InvocationCallbackImpl());
    }

    private class InvocationCallbackImpl implements InvocationCallback<Response> {

        public void completed(Response res) {
            System.out.format("LOG: Statistics for application %s registered. Status %d.%n", APP_NAME, res.getStatus());
        }

        public void failed(Throwable thrwbl) {
            thrwbl.printStackTrace(System.err);
        }
    }



    @Override
    public void destroy() {
        this.run = false;
    }

}
