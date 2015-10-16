/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import dtos.ProductDTO;
import java.net.URI;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import models.ProductModel;

/**
 * REST Web Service
 *
 * @author ryan.slipetz
 */
@Path("product")
public class ProductResource {

    @Context
    private UriInfo context;
    
    @Resource(lookup = "jdbc/Info5059db")
    DataSource ds;

    /**
     * Creates a new instance of ProductResource
     */
    public ProductResource() {
    }

    //Get - Get a list of all of the products from the database
    @GET
    @Produces("application/json")
    public ArrayList<ProductDTO> getProductJson() {
        ProductModel model = new ProductModel();
        ArrayList<ProductDTO> products = model.getProducts(ds);
        return products;
    }

    //POST method for creating a product in the DB
    //Product - JSON/DTOP object of the product we want to create
    //Return - HTTP response with ProductCode we added
    @POST
    @Consumes("application/json")
    public Response createProductFromJson(ProductDTO product) {
        ProductModel model = new ProductModel();
        int addedCount = model.addProduct(product, ds);
        URI uri = context.getAbsolutePath();
        return Response.created(uri).entity(addedCount).build();
    }
    
    //PUT - Update an instance of Product
    //Product - JSON/DTO of the object we want to update
    //Returns - HTTP response with the 
    @PUT
    @Consumes("application/json")
    public Response updateProductFromJson(ProductDTO product) {
        ProductModel model = new ProductModel();
        int rowsUpdated = model.updateProduct(product, ds);
        URI uri = context.getAbsolutePath();
        return Response.created(uri).entity(rowsUpdated).build();
    }
    
    //DELETE - Deletes the passed-in Product
    //PathParam ProductCode - Code of the Product we want to delete
    //Return - Number of rows that were deleted
    @DELETE
    @Path("/{productcode}")
    public Response deleteProduct(@PathParam("productcode") String productcode) {
        ProductModel model = new ProductModel();
        int numOfRowsDeleted = model.deleteProduct(productcode, ds);
        URI uri = context.getAbsolutePath();
        return Response.created(uri).entity(numOfRowsDeleted).build();
    }
    
    @GET
    @Path("/{vendorno}")
    @Produces("application/json")
    public ArrayList<ProductDTO> getVendorProductsJson(@PathParam("vendorno") int vendorno) {
        ProductModel model = new ProductModel();
        return model.getAllProductsForVendor(vendorno, ds);
    }
}
