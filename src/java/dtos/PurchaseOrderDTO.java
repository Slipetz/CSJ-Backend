/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author ryan.slipetz
 */
public class PurchaseOrderDTO implements Serializable {
    private int ponumber;
    private int vendorno;
    private ArrayList<PurchaseOrderLineItemDTO> items;
    private BigDecimal total;
    private String podate;

    public int getPonumber() {
        return ponumber;
    }

    public void setPonumber(int ponumber) {
        this.ponumber = ponumber;
    }

    public int getVendorno() {
        return vendorno;
    }

    public void setVendorno(int vendorno) {
        this.vendorno = vendorno;
    }

    public ArrayList<PurchaseOrderLineItemDTO> getItems() {
        return items;
    }

    public void setItems(ArrayList<PurchaseOrderLineItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getPodate() {
        return podate;
    }

    public void setPodate(String podate) {
        this.podate = podate;
    }
    
    
}
