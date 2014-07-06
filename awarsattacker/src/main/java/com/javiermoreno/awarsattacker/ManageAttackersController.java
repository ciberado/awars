package com.javiermoreno.awarsattacker;

import com.javiermoreno.awsdemos.attacker.WarService;
import com.javiermoreno.awsdemos.attacker.WarServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.math3.util.Pair;

/**
 *
 * @author ciberado
 */
@Path("/")
@Singleton
public class ManageAttackersController {

    private Map<String /* WairId */, String /* public dns elb */> defendersDns = new HashMap<String, String>();
    private Map<String /* WarId */, WarService> services = new HashMap<String, WarService>();

    @GET
    @Path("/test")
    @Produces("application/json; charset=utf-8")
    public Response test() {
        return Response.ok(new ArrayList()).build();
    }

    @DELETE
    @Path("/attackers/{attackerWarId}/vs/{defenderWarId}")
    @Produces("application/json; charset=utf-8")
    public Response cancelAttack(final @PathParam("attackerWarId") String attackerWarId,
            final @PathParam("defenderWarId") String defenderWarId) {
        String conflictName = attackerWarId + "_" + defenderWarId;
        WarService attackerService = services.get(conflictName);
        if (attackerService == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            attackerService.setDesiredThreadNumber(0);
            services.remove(conflictName);
            return Response.ok(getConflictsReport()).build();
        }
    }

    @GET
    @Path("/conflicts")
    @Produces("application/json; charset=utf-8")
    public Response listAttackers() {
        return Response.ok(getConflictsReport()).build();
    }

    private List<Pair<String, WarService>> getConflictsReport() {
        List<Pair<String, WarService>> report = new ArrayList<Pair<String, WarService>>();
        for (String currentConflictName : services.keySet()) {
            report.add(new Pair<String, WarService>(currentConflictName, services.get(currentConflictName)));
        }
        return report;
    }

    @POST
    @Path("/attackers/{attackerWarId}/vs/{defenderWarId}")
    @Produces("application/json; charset=utf-8")
    public Response launchNewAttack(final @PathParam("attackerWarId") String attackerWarId,
            final @PathParam("defenderWarId") String defenderWarId,
            final @QueryParam("desiredNumberOfThreads") int desiredNumberOfThreads) {
        String defenderDns = defendersDns.get(defenderWarId);
        if (defenderDns == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        int port = 80;
        if (defenderDns.contains(":") == true) {
            port = Integer.parseInt(defenderDns.substring(defenderDns.indexOf(":") + 1));
            defenderDns = defenderDns.substring(0, defenderDns.indexOf(":"));
        }

        String conflictName = attackerWarId + "_" + defenderWarId;
        WarService attackerService = services.get(conflictName);
        if (attackerService != null) {
            attackerService.setDesiredThreadNumber(0);
        }
        final String host = defenderDns;
        attackerService = new WarServiceImpl(attackerWarId, defenderDns, port);
        attackerService.addListener(new WarService.WarServiceListener() {
            public void requestCompleted(WarService.RequestCompletedEvent evt) {
                System.out.format("%tT: Request for %s vs %s (%s)  completed in %4d ms with a response status of %d.%n",
                        evt.getTimestamp(), attackerWarId, defenderWarId,
                        host, evt.getResponseTimeMs(), evt.getStatus());
            }
        });
        attackerService.setDesiredThreadNumber(desiredNumberOfThreads);
        services.put(conflictName, attackerService);
        final WarService attackerServiceJava7Sucks = attackerService;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                attackerServiceJava7Sucks.attack();
            }
        });
        thread.setDaemon(true);
        thread.start();
        return Response.ok(defenderDns).build();
    }

    @POST /* TODO: Pásalo a post. */
    @Path("/defenders/{defenderWarId}")
    @Produces("application/json; charset=utf-8")
    public Response registerNewStat(@PathParam("defenderWarId") String defenderWarId,
            @QueryParam("publicdns") String defenderPublicDns) {
        defendersDns.put(defenderWarId, defenderPublicDns);
        // Fucking Moxy map broken serialization...
        List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
        for (String key : defendersDns.keySet()) {
            result.add(new Pair<String, String>(key, defendersDns.get(key)));
        }
        return Response.ok(result).build();
    }

    public void destroy() {
        for (WarService attackerService : services.values()) {
            if (attackerService != null) {
                attackerService.setDesiredThreadNumber(0);
            }
        }        
    }
}
