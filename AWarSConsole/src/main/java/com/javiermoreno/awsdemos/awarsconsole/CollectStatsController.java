/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javiermoreno.awsdemos.awarsconsole;

import com.javiermoreno.awsdemos.awarsconsole.domain.ConflictStats;
import com.javiermoreno.awsdemos.awarsconsole.dto.ConflictsReportDTO;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author ciberado
 */
@Path("/metrics")
@Singleton
public class CollectStatsController {
    
    private StatsService service = new StatsServiceImplMem();
    
    @GET
    @Path("/test")
    @Produces("application/json; charset=utf-8")
    public Response test() {
        Map<String, Object> responseData = new HashMap<String, Object>();
        responseData.put("a", 1);
        responseData.put("b", "c");
        return Response.ok(responseData).build();
    }

    @GET /* TODO: Pásalo a post. */
    @Path("/applications/{appName}/customers/{warId}/instances/{instanceId}")
    @Produces("application/json; charset=utf-8")
    public Response registerNewStat(@PathParam("appName") String appName,
                                    @PathParam("warId") String warId, 
                                    @PathParam("instanceId") String instanceId,
                                    @QueryParam("opponentWarId") String opponentWarId,
                                    @QueryParam("instanceType") String instanceType,
                                    @QueryParam("startTimestamp")  long start,
                                    @QueryParam("endTimestamp") long end,
                                    @QueryParam("requestCount") int requestCount,
                                    @QueryParam("avgResponseTimeMs") int avgResponseTimeMs) {
        service.updateConflictStats(warId, opponentWarId, appName, instanceId, instanceType, 
                                    start, end, requestCount, avgResponseTimeMs);
        return Response.ok().build();
    }
    
    @GET 
    @Path("/conflicts")
    @Produces("application/json; charset=utf-8")
    public Response getConflicts() {
        Map<String, ConflictStats> stats = service.getConflictStats();
        
        return Response.ok(stats).build();
    }
    
    @GET 
    @Path("/report")
    @Produces("application/json; charset=utf-8")
    public Response getReports() {
        Map<String, ConflictStats> conflictStats = service.getConflictStats();
        ConflictsReportDTO dto = new ConflictsReportDTO();
        for (ConflictStats currentConflictStats : conflictStats.values()) {
            dto.addConflictDTO(currentConflictStats);
        }
        return Response.ok(dto).build();
    }
    
}
