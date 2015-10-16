/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import dtos.PurchaseOrderDTO;
import java.net.URI;
import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import models.PurchaseOrderModel;

/**
 * REST Web Service
 *
 * @author ryan.slipetz
 */
@Path("po")
public class POResource {

    @Context
    private UriInfo context;
    
    @Resource(lookup = "jdbc/Info5059db")
    DataSource ds;

    /**
     * Creates a new instance of POResource
     */
    public POResource() {
    }
    
    @POST
    @Consumes("application/json")
    public Response createPO(PurchaseOrderDTO po) {
        PurchaseOrderModel model = new PurchaseOrderModel();
        int ponumber = model.purchaseOrderAdd(po.getTotal(), po.getVendorno(), po.getItems(), po, ds);
        URI uri = context.getAbsolutePath();
        return Response.created(uri).entity(ponumber).build();
    }

    
}
