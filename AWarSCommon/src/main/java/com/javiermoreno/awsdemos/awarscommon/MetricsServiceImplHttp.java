package com.javiermoreno.awsdemos.awarscommon;

import java.io.Serializable;
import java.util.Date;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;

/**
 *
 * @author ciberado
 */
public class MetricsServiceImplHttp implements Serializable {
    
    private static final int COLLECT_TIMEOUT_MS = 1000 * 20;
    
    private final String appName;
    private final String metricsServerURL;
    private Client client;
    private boolean keepRunning;
    
    
    private final String warId;
    private final String opponentWarId;
    private final String instanceId;
    private final String instanceType;
    private int requestCount;
    private Date startTimestamp;
    private SynchronizedDescriptiveStatistics responseTimeInMsStats;

    public MetricsServiceImplHttp(String appName, String metricsServerURL, String optionalWarId, String optionalOpponentWarId) {
        client = ClientBuilder.newClient();
        this.appName = appName;
        this.metricsServerURL = metricsServerURL; 
        this.warId =  optionalWarId != null ? optionalWarId : System.getenv("WAR_ID");
        this.opponentWarId = System.getenv("OPPONENT_WAR_ID") == null ? optionalOpponentWarId : System.getenv("OPPONENT_WAR_ID");
        instanceId = getEC2Metadata("http://169.254.169.254/latest/meta-data/instance-id");
        instanceType = getEC2Metadata("http://169.254.169.254/latest/meta-data/instance-type");
        this.responseTimeInMsStats = new SynchronizedDescriptiveStatistics();
    }
    
    private String getEC2Metadata(String key) {
        try {
            System.out.format("LOG: Retreiving %s from metadata.%n", key);
            String result = client.target(key).request().get(String.class);
            System.out.format("LOG: Returned value is %s.%n", result);
            return result;
        } catch (javax.ws.rs.ProcessingException exc) {
            return "unknown";
        }
    }

    public void start() {
        if (keepRunning == true) return;
        
        keepRunning = true;
        resetStats();
        new Thread(new Runnable(){
            @Override
            public void run() {
                while (keepRunning == true) {
                    try {
                        Thread.sleep(COLLECT_TIMEOUT_MS);
                        if (keepRunning == true) {
                            saveStats();
                            resetStats();
                        }
                    } catch (Exception exc) {
                        exc.printStackTrace(System.err);                        
                    }
                }
            }
        }).start();
    }
    
    public void stop() {
        keepRunning = false;
        client.close();
    }
    
    public void saveStats() {
        /* TODO: Reimplementarlo para que no dependa de http. */
        Date endTimestamp = new Date();
        long timeDiff = endTimestamp.getTime() - startTimestamp.getTime();
        double requestPerSecond = requestCount / (timeDiff/1000.0);
        System.out.format("LOG: Statistics for %s%n"
                        + "request count: %d, request per second: %.2f, avg response time: %.2f.%n", 
                           appName, requestCount, requestPerSecond,
                           responseTimeInMsStats.getMean());
        client
            .target(this.metricsServerURL)
            .path("/metrics/applications/{appName}/customers/{warId}/instances/{instanceId}")
            .resolveTemplate("appName", appName)
            .resolveTemplate("warId", warId == null ? "WarId-test" : warId)
            .resolveTemplate("instanceId", instanceId == null ? "i-test" : instanceId)
            .queryParam("opponentWarId", opponentWarId)
            .queryParam("startTimestamp", startTimestamp.getTime())
            .queryParam("endTimestamp", endTimestamp.getTime())
            .queryParam("timeDiffMs", timeDiff)
            .queryParam("instanceType", instanceType == null ? "t.test" : instanceType)
            .queryParam("requestCount", requestCount)
            .queryParam("requestCountPerSecond", requestPerSecond)
            .queryParam("avgResponseTimeMs", (long) responseTimeInMsStats.getMean())
            .queryParam("maxResponseTimeMs", (long) responseTimeInMsStats.getMax())
            .queryParam("minResponseTimeMs", (long) responseTimeInMsStats.getMin())
            .queryParam("stDevResponseTimeMs", (long) responseTimeInMsStats.getStandardDeviation())
            .request()
            .async()
            .get(new InvocationCallbackImpl());
    }

    private void resetStats() {
        requestCount = 0;
        responseTimeInMsStats.clear();
        startTimestamp = new Date();
    }

    public void registerNewRequest() {
        requestCount = requestCount + 1;
    }

    public void registerResponseTimeInMs(long value) {
        responseTimeInMsStats.addValue(value);
    }
    
    private class InvocationCallbackImpl implements InvocationCallback<Response> {
        public void completed(Response res) {
            System.out.format("LOG: Statistics for application %s registered. Status %d.%n", appName, res.getStatus());
        }

        public void failed(Throwable thrwbl) {
            thrwbl.printStackTrace(System.err);
        }
    }    
    
}
