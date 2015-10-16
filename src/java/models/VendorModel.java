/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import dtos.VendorDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.DataSource;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author ryan.slipetz
 */
@RequestScoped
public class VendorModel {
    
    public VendorModel() {
    }
    
    public ArrayList<VendorDTO> getVendors(DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        String sql = "SELECT * FROM Vendors";
        ArrayList<VendorDTO> vendors = new ArrayList<>();
        
        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    VendorDTO vendor = new VendorDTO();
                    vendor.setVendorno(rs.getInt("VendorNo"));
                    vendor.setName(rs.getString("Name"));
                    vendor.setAddress1(rs.getString("Address1"));
                    vendor.setCity(rs.getString("City"));
                    vendor.setProvince(rs.getString("Province"));
                    vendor.setPostalcode(rs.getString("PostalCode"));
                    vendor.setPhone(rs.getString("Phone"));
                    vendor.setVendortype(rs.getString("VendorType"));
                    vendor.setEmail(rs.getString("Email"));
                    vendors.add(vendor);
                }
            }
            con.close();
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

        return vendors;
    }
    
    public int addVendor(VendorDTO details, DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        String sql = "INSERT INTO Vendors (Name, Address1, City, Province, Postalcode,"
                + " Phone, VendorType, Email) Values(?,?,?,?,?,?,?,?)";
        int vendorID = -1;
        
        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, details.getName());
            pstmt.setString(2, details.getAddress1());
            pstmt.setString(3, details.getCity());
            pstmt.setString(4, details.getProvince());
            pstmt.setString(5, details.getPostalcode());
            pstmt.setString(6, details.getPhone());
            pstmt.setString(7, details.getVendortype());
            pstmt.setString(8, details.getEmail());
            pstmt.execute();
            
            try(ResultSet rs = pstmt.getGeneratedKeys())
            {
                rs.next();
                vendorID = rs.getInt(1);
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
        return vendorID;
    }
    
    public int updateVendor(VendorDTO details, DataSource ds)
    {
        PreparedStatement pstmt;
        Connection con = null;
        int numOfRowsChange = -1;
        
        String sql = "UPDATE Vendors SET Name = ?, Address1 = ?, City = ?, Province = ?,"
                + "Postalcode = ?, Phone = ?, Vendortype = ?, Email = ? WHERE VendorNo = ?";
        
        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, details.getName());
            pstmt.setString(2, details.getAddress1());
            pstmt.setString(3, details.getCity());
            pstmt.setString(4, details.getProvince());
            pstmt.setString(5, details.getPostalcode());
            pstmt.setString(6, details.getPhone());
            pstmt.setString(7, details.getVendortype());
            pstmt.setString(8, details.getEmail());
            pstmt.setInt(9, details.getVendorno());
            
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
    
    public int deleteVendor(int vendorID, DataSource ds)
    {
        PreparedStatement pstmt;
        Connection con = null;
        int numOfRows = -1;
        
        String sql = "DELETE FROM Vendors WHERE Vendorno = ?";
        
        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, vendorID);
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
    
    public VendorDTO getVendor(int vendorID, DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        String sql = "SELECT * FROM Vendors WHERE Vendorno = ?";
        VendorDTO vendor = new VendorDTO();

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, vendorID);
            try (ResultSet rs = pstmt.executeQuery()) {
                 while(rs.next()) {
                     vendor.setVendorno(rs.getInt("VendorNo"));
                     vendor.setName(rs.getString("Name"));
                     vendor.setAddress1(rs.getString("Address1"));
                     vendor.setCity(rs.getString("City"));
                     vendor.setProvince(rs.getString("Province"));
                     vendor.setPostalcode(rs.getString("PostalCode"));
                     vendor.setPhone(rs.getString("Phone"));
                     vendor.setVendortype(rs.getString("VendorType"));
                     vendor.setEmail(rs.getString("Email"));
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
        
        return vendor;
    }
}
