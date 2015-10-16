/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author ryan.slipetz
 */
import dtos.PurchaseOrderLineItemDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.sql.DataSource;
import dtos.PurchaseOrderDTO;
@RequestScoped
public class PurchaseOrderModel {
    
    public PurchaseOrderModel() {
        
    }
    
    public int purchaseOrderAdd(BigDecimal total, int vendorno, ArrayList<PurchaseOrderLineItemDTO> items, PurchaseOrderDTO po, DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        String sql = "INSERT INTO PurchaseOrders (vendorno, amount, podate) Values (?,?,?)";
        int ponumber = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String curdate = df.format(Calendar.getInstance().getTime());
        
        try {
            con = ds.getConnection();
            con.setAutoCommit(false);
            pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, vendorno);
            pstmt.setBigDecimal(2, total);
            pstmt.setString(3, curdate);
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            ponumber = rs.getInt(1);
            pstmt.close();
            
            for(PurchaseOrderLineItemDTO item : items) {
                if(item.getQty() > 0) {
                    sql = "INSERT INTO PurchaseOrderLineItems (ponumber, productcode, qty, price) VALUES(?,?,?,?)";
                    pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                    pstmt.setInt(1, ponumber);
                    pstmt.setString(2, item.getProductcode());
                    pstmt.setInt(3, item.getQty());
                    pstmt.setBigDecimal(4, item.getPrice());
                    pstmt.execute();
                    rs = pstmt.getGeneratedKeys();
                    rs.next();
                }
            }
            con.commit();
            con.close();
        } catch (SQLException ex) {
            //Handle errors for the JDBC
            System.out.println("SQL issue: " + ex.getMessage());
            ponumber = 0;
            try {
                con.rollback();
            } catch (SQLException sqx) {
                System.out.println("Rollback failed - " + sqx.getMessage());
            }
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
        return ponumber;
    }
    
    public ArrayList<PurchaseOrderLineItemDTO> getLineItemsForPoNumber(int ponumber, DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        String sql = "SELECT * FROM PurchaseOrderLineItems WHERE ponumber = ?";
        ArrayList<PurchaseOrderLineItemDTO> lineitems = new ArrayList<>();
        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, ponumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                 while(rs.next()) {
                     PurchaseOrderLineItemDTO lineitem = new PurchaseOrderLineItemDTO();
                     lineitem.setLineid(rs.getInt("lineid"));
                     lineitem.setPonumber(rs.getInt("ponumber"));
                     lineitem.setProductcode(rs.getString("productcode"));
                     lineitem.setQty(rs.getInt("qty"));
                     lineitem.setPrice(rs.getBigDecimal("price"));
                     lineitems.add(lineitem);
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
        return lineitems;
    }
    
    public PurchaseOrderDTO getPurchaseOrder(int ponumber, DataSource ds) {
        PreparedStatement pstmt;
        Connection con = null;
        String sql = "SELECT * FROM PurchaseOrders WHERE Ponumber = ?";
        PurchaseOrderDTO po = new PurchaseOrderDTO();

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, ponumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                 while(rs.next()) {
                    po.setVendorno(rs.getInt("VendorNo"));
                    po.setTotal(rs.getBigDecimal("amount"));
                    po.setPodate(rs.getString("podate"));
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
        
        return po;
    }
}
