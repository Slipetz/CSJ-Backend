/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import dtos.ProductDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.enterprise.context.RequestScoped;
import javax.sql.DataSource;

/**
 *
 * @author ryan.slipetz
 */
@RequestScoped
public class ProductModel {
    
    public ProductModel() {
        
    }
    
    public ArrayList<ProductDTO> getProducts(DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        String sql = "SELECT * FROM Products";
        ArrayList<ProductDTO> products = new ArrayList<>();
        
        try {
            con = ds.getConnection();
            pstmt = con.prepareCall(sql);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()){
                    ProductDTO product = new ProductDTO();
                    product.setProductcode(rs.getString("productcode"));
                    product.setVendorno(rs.getInt("vendorno"));
                    product.setVendorsku(rs.getString("vendorsku"));
                    product.setProductname(rs.getString("productname"));
                    product.setCostprice(rs.getDouble("costprice"));
                    product.setMsrp(rs.getDouble("msrp"));
                    product.setRop(rs.getInt("rop"));
                    product.setEoq(rs.getInt("eoq"));
                    product.setQoh(rs.getInt("qoh"));
                    product.setQoo(rs.getInt("qoo"));
                    products.add(product);
                }
            }
        } catch (SQLException se) {
            //Handle errors for the JDBC
            System.out.println("SQL issue: " + se.getMessage());
        } catch (Exception e) {
            //Handle all other erros
            System.out.println("Other Issue: " + e.getMessage());
        } finally {
            try {
                if(con != null) {
                    con.close();
                }
            } catch (SQLException se) {
                System.out.println("SQL Issue on close: " + se.getMessage());
            }
        }
        
        return products;
    }
    
    public int addProduct(ProductDTO product, DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        String sql = "INSERT INTO Products (productcode, vendorno, vendorsku, productname, costprice,"
                + " msrp, rop, eoq, qoh, qoo) Values(?,?,?,?,?,?,?,?,?,?)";
        int productid = 0;
        
        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, product.getProductcode());
            pstmt.setInt(2, product.getVendorno());
            pstmt.setString(3, product.getVendorsku());
            pstmt.setString(4, product.getProductname());
            pstmt.setDouble(5, product.getCostprice());
            pstmt.setDouble(6, product.getMsrp());
            pstmt.setInt(7, product.getRop());
            pstmt.setInt(8, product.getEoq());
            pstmt.setInt(9, product.getQoh());
            pstmt.setInt(10, product.getQoo());
            pstmt.execute();
            
            productid = pstmt.getUpdateCount();
        } catch (SQLException ex) {
            //Handle errors for the JDBC
            System.out.println("SQL issue: " + ex.getMessage());
        } catch (Exception e) {
            //Catch All other errors
            System.out.println("Unknown Error: " + e.getMessage());
        } finally {
            //finally block used to close resources
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException se) {
                System.out.println("SQL issue on close " + se.getMessage());
            }//end finally try
        }
        return productid;
    }
    
    public int updateProduct(ProductDTO product, DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        int numOfRowsChange = -1;
        
        String sql = "UPDATE Products SET vendorno = ?, vendorsku = ?, productname = ?,"
                + " costprice = ?, msrp = ?, rop = ?, eoq = ?, qoh = ?, qoo = ? WHERE productcode = ?";
        
         try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, product.getVendorno());
            pstmt.setString(2, product.getVendorsku());
            pstmt.setString(3, product.getProductname());
            pstmt.setDouble(4, product.getCostprice());
            pstmt.setDouble(5, product.getMsrp());
            pstmt.setInt(6, product.getRop());
            pstmt.setInt(7, product.getEoq());
            pstmt.setInt(8, product.getQoh());
            pstmt.setInt(9, product.getQoo());
            pstmt.setString(10, product.getProductcode());

            numOfRowsChange = pstmt.executeUpdate();
        } catch (SQLException ex) {
            //Handle errors for the JDBC
            System.out.println("SQL issue: " + ex.getMessage());
        } catch (Exception e) {
            //Catch All other errors
            System.out.println("Unknown Error: " + e.getMessage());
        } finally {
            //finally block used to close resources
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException se) {
                System.out.println("SQL issue on close " + se.getMessage());
            }//end finally try
        }
        return numOfRowsChange;
    }
    
    public int deleteProduct(String productcode, DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        int numOfRows = -1;
        
        String sql = "DELETE FROM Products WHERE productcode = ?";
        
        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, productcode);
            numOfRows = pstmt.executeUpdate();
        } catch (SQLException ex) {
            //Handle errors for the JDBC
            System.out.println("SQL issue: " + ex.getMessage());
        } catch (Exception e) {
            //Catch All other errors
            System.out.println("Unknown Error: " + e.getMessage());
        } finally {
            //finally block used to close resources
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException se) {
                System.out.println("SQL issue on close " + se.getMessage());
            }//end finally try
        }
        
        return numOfRows;
    }

    public ArrayList<ProductDTO> getAllProductsForVendor(int vendorno, DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        ArrayList<ProductDTO> products = new ArrayList<ProductDTO>();
        
        String sql = "SELECT * FROM PRODUCTS WHERE VENDORNO = ?";
        
        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, vendorno);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()){
                    ProductDTO product = new ProductDTO();
                    product.setProductcode(rs.getString("productcode"));
                    product.setVendorno(rs.getInt("vendorno"));
                    product.setVendorsku(rs.getString("vendorsku"));
                    product.setProductname(rs.getString("productname"));
                    product.setCostprice(rs.getDouble("costprice"));
                    product.setMsrp(rs.getDouble("msrp"));
                    product.setRop(rs.getInt("rop"));
                    product.setEoq(rs.getInt("eoq"));
                    product.setQoh(rs.getInt("qoh"));
                    product.setQoo(rs.getInt("qoo"));
                    products.add(product);
                }
            }
            
        } catch (SQLException ex) {
            //Handle errors for the JDBC
            System.out.println("SQL issue: " + ex.getMessage());
        } catch (Exception e) {
            //Catch All other errors
            System.out.println("Unknown Error: " + e.getMessage());
        } finally {
            //finally block used to close resources
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException se) {
                System.out.println("SQL issue on close " + se.getMessage());
            }//end finally try
        }
     
        return products;
    }
    
    public ProductDTO getProduct(String productnum, DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        String sql = "SELECT * FROM Products WHERE productcode = ?";
        ProductDTO product = new ProductDTO();

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, productnum);
            try (ResultSet rs = pstmt.executeQuery()) {
                 while(rs.next()) {
                    product.setProductcode(rs.getString("productcode"));
                    product.setVendorno(rs.getInt("vendorno"));
                    product.setVendorsku(rs.getString("vendorsku"));
                    product.setProductname(rs.getString("productname"));
                    product.setCostprice(rs.getDouble("costprice"));
                    product.setMsrp(rs.getDouble("msrp"));
                    product.setRop(rs.getInt("rop"));
                    product.setEoq(rs.getInt("eoq"));
                    product.setQoh(rs.getInt("qoh"));
                    product.setQoo(rs.getInt("qoo"));
                 }
             }
        } catch (SQLException ex) {
            //Handle errors for the JDBC
            System.out.println("SQL issue: " + ex.getMessage());
        } catch (Exception e) {
            //Catch All other errors
            System.out.println("Unknown Error: " + e.getMessage());
        } finally {
            //finally block used to close resources
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException se) {
                System.out.println("SQL issue on close " + se.getMessage());
            }//end finally try
        }
        
        return product;
    }
}
