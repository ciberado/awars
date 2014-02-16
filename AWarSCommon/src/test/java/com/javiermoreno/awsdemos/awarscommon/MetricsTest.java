/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javiermoreno.awsdemos.awarscommon;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author ciberado
 */
public class MetricsTest {

    public MetricsTest() {
    }

    @Test
    public void checkSaveMetrics() throws IOException, InterruptedException {
        final MetricsServiceImplHttp metrics = new MetricsServiceImplHttp("test", "http://localhost:9999", "defender", "attacker");
        final Map<String, String> requestParams = new HashMap<String, String>();
        final StringBuilder requestUri = new StringBuilder();
        NanoHTTPD nano = new NanoHTTPD(9999) {
            @Override
            public NanoHTTPD.Response serve(String uri, NanoHTTPD.Method method, Map<String, String> headers,
                    Map<String, String> parms, Map<String, String> files) {
                requestUri.append(uri);
                requestParams.putAll(parms);
                metrics.stop();
                super.stop();
                synchronized (metrics) {
                    metrics.notifyAll();
                }
                System.out.println(uri);
                return new NanoHTTPD.Response(uri + "\r\n" + parms);
            }
        };
        nano.start();

        metrics.start();
        metrics.registerNewRequest();
        metrics.registerResponseTimeInMs(200);
        metrics.registerResponseTimeInMs(300);
        metrics.registerResponseTimeInMs(400);
        metrics.registerResponseTimeInMs(500);
        metrics.saveStats();
        synchronized (metrics) {
            metrics.wait();
        }
        Assert.assertEquals((200 + 300 + 400 + 500) / 4.0, Double.parseDouble(requestParams.get("avgResponseTimeMs")), 0.001);
    }

}
