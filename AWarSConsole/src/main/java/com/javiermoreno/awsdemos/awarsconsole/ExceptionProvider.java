package com.javiermoreno.awsdemos.awarsconsole;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author ciberado
 */
@Provider
public class ExceptionProvider implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException ex) {
        ex.printStackTrace(System.out);
        return Response.status(500)
                        .entity(ex.getMessage())
                        .type("text/plain")
                        .build();
    }

}
