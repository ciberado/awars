package com.javiermoreno.awsdemos.attacker;

import com.javiermoreno.awsdemos.awarscommon.IdentityDTO;
import com.javiermoreno.awsdemos.awarscommon.MetricsServiceImplHttp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;

/**
 *
 * @author ciberado
 */
class WarServiceImpl implements WarService {

    private static final String DEFENDER_BUSINESS_URL = "http://{server}:{port}/defender/api/mathematics/pi";
    private static final String DEFENDER_ID_URL = "http://{server}:{port}/defender/api/identity";
    private List<WarServiceListener> listeners = new ArrayList<WarServiceListener>();

    private MetricsServiceImplHttp metricsService;
    private String warId;
    private String server;
    private int serverPort;
    private String attackedWarName;

    private Client attackClient;
    
    private BlockingQueue<AsyncInvoker> invokers = new LinkedBlockingQueue<AsyncInvoker>();

    public WarServiceImpl(String warId, String server, int serverPort) {
        this.warId = warId;
        this.server = server;
        this.serverPort = serverPort;
        attackClient = ClientBuilder.newClient();
    }

    public void setMetricsService(MetricsServiceImplHttp metricsService) {
        this.metricsService = metricsService;
    }

    public String getAttackedWarName() {
        if (this.attackedWarName == null) {
            String url = DEFENDER_ID_URL.replace("{server}", server)
                                        .replace("{port}", String.valueOf(serverPort));
            Client client = ClientBuilder.newClient();
            IdentityDTO response = client.target(url).request().get(IdentityDTO.class);
            this.attackedWarName = response.getWarId();
        }
        return this.attackedWarName;
    }
    
    public void attack(int threads) throws InterruptedException {
        metricsService.start();

        for (int i=0; i < threads; i++) {
            createAsyncInvoker();
        }
        
        while (true) {
            AsyncInvoker invoker = invokers.take();
            invoker.get(new InvocationCallbackImpl());
            metricsService.registerNewRequest();
        }
    }
    
    public void addListener(WarServiceListener ls) {
        listeners.add(ls);
    }

    public void fireRequestCompletedEvent(int status, long responseTimeMs) {
        RequestCompletedEvent evt = new RequestCompletedEvent(status, responseTimeMs);
        for (WarServiceListener ls : listeners) {
            ls.requestCompleted(evt);
        }
    }

    private void createAsyncInvoker() {
        String url = DEFENDER_BUSINESS_URL.replace("{server}", server)
                                          .replace("{port}", String.valueOf(serverPort));
        AsyncInvoker invoker = attackClient.target(url)
                                           .queryParam("warId", warId)
                                           .queryParam("attackedWarName", attackedWarName)
                                           .request()
                                           .async();
        invokers.add(invoker);
    }

    private class InvocationCallbackImpl implements InvocationCallback<Response> {
        private long t0;

        public InvocationCallbackImpl() {
            t0 = System.currentTimeMillis();
        }

        public void completed(Response res) {
            long tf = System.currentTimeMillis();            
            metricsService.registerResponseTimeInMs(tf-t0);
            fireRequestCompletedEvent(res.getStatus(), tf -t0);
            createAsyncInvoker();
        }

        public void failed(Throwable thrwbl) {
            System.err.println("LOG: " + thrwbl.getMessage());
            thrwbl.printStackTrace(System.err);
            createAsyncInvoker();
        }
    }

}
