package com.javiermoreno.awsdemos.attacker;

import com.javiermoreno.awsdemos.awarscommon.IdentityDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;

/**
 *
 * @author ciberado
 */
public class WarServiceImpl implements WarService {

    private static final String DEFENDER_BUSINESS_URL = "http://{server}:{port}/defender/api/mathematics/pi";
    private static final String DEFENDER_ID_URL = "http://{server}:{port}/defender/api/identity";
    private List<WarServiceListener> listeners = new ArrayList<WarServiceListener>();

    private String warId;
    private String server;
    private int serverPort;
    private String defenderWarId;

    private Client attackClient;

    private BlockingQueue<AsyncInvoker> waitingInvokers = new LinkedBlockingQueue<AsyncInvoker>();
    private int processingInvokersNumber;
    private int desiredThreadNumber;
    private long lastLatencyMs;

    public WarServiceImpl(String warId, String server, int serverPort) {
        this.warId = warId;
        this.server = server;
        this.serverPort = serverPort;
        attackClient = ClientBuilder.newClient();
        this.desiredThreadNumber = 1;
    }

    public void incrementDesiredNumberOfThreads() {
        desiredThreadNumber = desiredThreadNumber + 1;
    }

    public void desiredThreadNumberOfThreads() {
        desiredThreadNumber = Math.max(0, desiredThreadNumber - 1);
    }

    public int getDesiredThreadNumber() {
        return desiredThreadNumber;
    }

    public void setDesiredThreadNumber(int desiredThreadNumber) {
        this.desiredThreadNumber = desiredThreadNumber;
    }

    public String getDefenderWarId() {
        if (this.defenderWarId == null) {
            String url = DEFENDER_ID_URL.replace("{server}", server)
                    .replace("{port}", String.valueOf(serverPort));
            Client client = ClientBuilder.newClient();
            IdentityDTO response = client.target(url).request().get(IdentityDTO.class);
            this.defenderWarId = response.getWarId();
        }
        return this.defenderWarId;
    }

    public void attack() {
        try {
            for (int i = 0; i < desiredThreadNumber; i++) {
                createAsyncInvoker();
            }
            while (desiredThreadNumber > 0) {
                AsyncInvoker invoker = waitingInvokers.take();
                processingInvokersNumber = processingInvokersNumber + 1;
                invoker.get(new InvocationCallbackImpl());
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(WarServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
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
                .queryParam("attackerWarId", warId)
                .queryParam("defenderWarId", defenderWarId)
                .request()
                .async();
        waitingInvokers.add(invoker);
    }

    public long getLastLatencyMs() {
        return lastLatencyMs;
    }
    
    private class InvocationCallbackImpl implements InvocationCallback<Response> {

        private long t0;

        public InvocationCallbackImpl() {
            t0 = System.currentTimeMillis();
        }

        public void completed(Response res) {
            processingInvokersNumber = processingInvokersNumber - 1;
            long tf = System.currentTimeMillis();
            fireRequestCompletedEvent(res.getStatus(), tf - t0);
            lastLatencyMs = tf-t0;
            createAsyncInvoker();
        }

        public void failed(Throwable thrwbl) {
            processingInvokersNumber = processingInvokersNumber - 1;
            System.err.println("LOG (" + server + "): " + thrwbl.getMessage());
            createAsyncInvoker();
        }
    }

}
