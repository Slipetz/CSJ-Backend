/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import dtos.VendorDTO;
import java.net.URI;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.sql.DataSource;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import models.VendorModel;

/**
 * REST Web Service
 *
 * @author ryan.slipetz
 */
@Path("vendor")
public class VendorResource {

    @Context
    private UriInfo context;

    @Resource(lookup = "jdbc/Info5059db")
    DataSource ds;
    
    /**
     * Creates a new instance of VendorResource
     */
    public VendorResource() {
    }

    //Get - Gets a list of all vendors from the database
    @GET
    @Produces("application/json")
    public ArrayList<VendorDTO> getVendorJson() {
       VendorModel model = new VendorModel();
       ArrayList<VendorDTO> vendors = model.getVendors(ds);
       return vendors;
    }
    
    //POST method for creating an instance of Vendor
    //Vendor - JSON/DTO object of the Vendor we want to create
    //Return - HTTP response with the id of the created Vendor
    @POST
    @Consumes("application/json")
    public Response createVendorFromJson(VendorDTO vendor)
    {
        VendorModel model = new VendorModel();
        int id = model.addVendor(vendor, ds);
        URI uri = context.getAbsolutePath();
        return Response.created(uri).entity(id).build();
    }
    
    //PUT Updates an instance of a Vendor
    //Vendor - JSON/DTO object of the Vendor we want to update
    //Return - HTTP response with the number of rows updated
    @PUT
    @Consumes("application/json")
    public Response updateVendorFromJson(VendorDTO vendor)
    {
        VendorModel model = new VendorModel();
        int numRowsUpdated = model.updateVendor(vendor, ds);
        URI uri = context.getAbsolutePath();
        return Response.created(uri).entity(numRowsUpdated).build();
    }
    
    //DELETE - Deletes an instance of Vendor
    //VendorID - ID of the Vendor we want to delete from the Path Parameter
    //Return - Number of Rows that were removed
    @DELETE
    @Path("/{vendorid}")
    public Response deleteVendor(@PathParam("vendorid") int vendorID)
    {
        VendorModel model = new VendorModel();
        int numOfRowsDeleted = model.deleteVendor(vendorID, ds);
        URI uri = context.getAbsolutePath();
        return Response.created(uri).entity(numOfRowsDeleted).build();
    }
}
