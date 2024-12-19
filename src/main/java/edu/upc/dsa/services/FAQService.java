package edu.upc.dsa.services;

import edu.upc.dsa.FAQResource;
import edu.upc.dsa.models.FAQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "/faqs", description = "Endpoint to FAQ Service")
@Path("/faqs")
@Produces(MediaType.APPLICATION_JSON)
public class FAQService {

    private FAQResource faqResource = new FAQResource();

    @GET
    @ApiOperation(value = "get all FAQs", notes = "Retrieve all FAQs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = FAQ.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "No FAQs found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response getAllFAQs() {
        try {
            List<FAQ> faqs = faqResource.getFAQs();
            if (faqs.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"message\": \"No FAQs found\"}").build();
            }
            GenericEntity<List<FAQ>> entity = new GenericEntity<List<FAQ>>(faqs) {};
            return Response.ok(entity).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"Internal Server Error\"}").build();
        }
    }
}
