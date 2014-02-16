package com.javiermoreno.awsdemos.defender;

import com.javiermoreno.awsdemos.awarscommon.MetricsServiceImplHttp;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 *
 * @author ciberado
 */
@WebFilter(filterName = "TelemetryFilter", 
           urlPatterns = {"/api/*"}, 
           dispatcherTypes = {DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.REQUEST, DispatcherType.INCLUDE})
public class TelemetryFilter implements Filter {
    private static final String APP_NAME = "server";
    private static final String DEFAULT_METRICS_SERVER_URL = "http://localhost:8080/awarsconsole/api";
    private Map<String, MetricsServiceImplHttp> metricsServices = new HashMap<String, MetricsServiceImplHttp>();

    
    @Override
    public void init(FilterConfig fc) throws ServletException {

    }

    private MetricsServiceImplHttp getMetricsServer(String opponentWarName) {
        MetricsServiceImplHttp service = metricsServices.get(opponentWarName);        
        if (service == null) {
            String warId = System.getenv("WAR_ID") == null ? IdentityController.DEFAULT_WAR_ID : System.getenv("WAR_ID");
            String metricsServerUrl = System.getenv("METRICS_URL") == null ? DEFAULT_METRICS_SERVER_URL : System.getenv("METRICS_URL");
            service = new MetricsServiceImplHttp(APP_NAME, metricsServerUrl, warId, opponentWarName);
            service.start();
            metricsServices.put(opponentWarName, service);
        }
        return service;
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        long t0 = System.currentTimeMillis();
        chain.doFilter(req, res);
        long tf = System.currentTimeMillis();
        String opponentWarId = req.getParameter("warId");
        if (opponentWarId != null) {
            MetricsServiceImplHttp service = getMetricsServer(opponentWarId);
            service.registerNewRequest();
            service.registerResponseTimeInMs(tf-t0);
        }
    }

    @Override
    public void destroy() {
        for (MetricsServiceImplHttp service : this.metricsServices.values()) {
            service.stop();
        }
    }
    

    
}
