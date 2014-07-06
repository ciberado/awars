package com.javiermoreno.awsdemos.awarsconsole;

import com.javiermoreno.awsdemos.awarsconsole.domain.ConflictStats;
import com.javiermoreno.awsdemos.awarsconsole.dto.ConflictDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

    @POST 
    @Path("/defenders/{defenderWarId}/instances/{instanceId}")
    @Produces("application/json; charset=utf-8")
    public Response registerNewStat(@PathParam("defenderWarId") String defenderWarId, 
                                    @PathParam("instanceId") String instanceId,
                                    @QueryParam("attackerWarId") String attackerWarId,
                                    @QueryParam("instanceType") String instanceType,
                                    @QueryParam("requestPerSecond") double requestPerSecond) {
        service.updateConflictStats(defenderWarId, attackerWarId, instanceId, instanceType, requestPerSecond);
        return Response.ok().build();
    }
    
    /*
    @GET 
    @Path("/conflicts")
    @Produces("application/json; charset=utf-8")
    public Response getConflicts() {
        Map<String, ConflictStats> stats = service.getConflictStats();
        
        return Response.ok(stats).build();
    }
    */
    
    @GET 
    @Path("/report")
    @Produces("application/json; charset=utf-8")
    public Response getReports() {
        List<ConflictDTO> conflictsDTO = new ArrayList<ConflictDTO>();
        for (ConflictStats currentConflictStats : service.getConflictStats().values()) {
            ConflictDTO dto = new ConflictDTO(currentConflictStats);
            if (dto.getMachinesCount() > 0) {
                conflictsDTO.add(dto);
            }
        }
        return Response.ok(conflictsDTO).build();
    }
    
}
