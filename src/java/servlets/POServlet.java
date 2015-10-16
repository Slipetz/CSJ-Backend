/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dtos.ProductDTO;
import dtos.PurchaseOrderDTO;
import dtos.PurchaseOrderLineItemDTO;
import dtos.VendorDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import models.ProductModel;
import models.PurchaseOrderModel;
import models.VendorModel;

/**
 *
 * @author ryan.slipetz
 */
@WebServlet(name = "POServlet", urlPatterns = {"/POPDF"})
public class POServlet extends HttpServlet {
    
    @Resource(lookup = "jdbc/Info5059db")
    DataSource ds;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            int ponumber = Integer.parseInt(request.getParameter("po"));
            buildpdf(response, ponumber);
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }    
    }
    private void buildpdf(HttpServletResponse response, int ponumber) {
        Font catFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
        Font subFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
        Font smallBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        String IMG = getServletContext().getRealPath("/img/logo.png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        
        //Get the Purchase Order Items - That way we can access the vendor
        PurchaseOrderModel poModel = new PurchaseOrderModel();
        PurchaseOrderDTO poDTO = poModel.getPurchaseOrder(ponumber, ds);
        ArrayList<PurchaseOrderLineItemDTO> lineitems = poModel.getLineItemsForPoNumber(ponumber, ds);
        
        //Have items, now need to get vendor
        VendorModel vModel = new VendorModel();
        VendorDTO vendor = vModel.getVendor(poDTO.getVendorno(), ds);
        
        //Product Model - For looking up product names
        ProductModel prodMod = new ProductModel();
        
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            Paragraph preface = new Paragraph();
            // We add one empty line
            Image image1 = Image.getInstance(IMG);
            image1.setAbsolutePosition(55f, 650f);
            image1.scaleAbsolute(200f, 200f);
            preface.add(image1);
            preface.add(new Paragraph("Guardians Light Equipment\nBest Exotics in the Cosmos", titleFont));
            preface.setAlignment(Element.ALIGN_RIGHT);
            // Lets write a big header
            Paragraph mainHead = new Paragraph(String.format("%55s", "Purchase Order"), catFont);
            preface.add(mainHead);
            preface.add(new Paragraph(String.format("%82s", "PO#:" + ponumber), subFont));
            addEmptyLine(preface, 1);
            //2 Column Vendor Table
            PdfPTable vendorTable = new PdfPTable(1);
            PdfPCell vCell = new PdfPCell(new Phrase("Vendor:", smallBold));
            vCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            vCell.setBorder(0);
            vendorTable.addCell(vCell);
            vendorTable.addCell(GenerateBorderlessCell(vendor.getName(), 0,Element.ALIGN_RIGHT));
            vendorTable.addCell(GenerateBorderlessCell(vendor.getAddress1(), 0,Element.ALIGN_RIGHT));
            vendorTable.addCell(GenerateBorderlessCell(vendor.getCity() + "," + vendor.getProvince(), 0,Element.ALIGN_RIGHT));
            vendorTable.addCell(GenerateBorderlessCell(vendor.getPostalcode(), 0,Element.ALIGN_RIGHT));
            preface.add(vendorTable);
            addEmptyLine(preface,2);
            //Generate Headers
            PdfPTable table = new PdfPTable(5);

            //Generate the Headers
            table.addCell(GenerateCellData("Product Code"));
            table.addCell(GenerateCellData("Product Description"));
            table.addCell(GenerateCellData("Quantity Sold"));
            table.addCell(GenerateCellData("Price"));
            table.addCell(GenerateCellData("Extended Price"));

            double subTotal = 0.0;
            for(PurchaseOrderLineItemDTO item : lineitems) {
                ProductDTO product = prodMod.getProduct(item.getProductcode(), ds);
                table.addCell(GenerateCellData(product.getProductcode()));
                table.addCell(GenerateCellData(product.getProductname()));
                table.addCell(GenerateCellData(String.valueOf(item.getQty())));
                double ext = product.getCostprice() * item.getQty();
                table.addCell(GenerateCellData("$" + String.format("%.2f", product.getCostprice())));
                table.addCell(GenerateCellData("$" + String.format("%.2f", ext)));
                subTotal += ext;
            }

            //Generate Total, Tax, Order Total
            table.addCell(GenerateBorderlessCell("Subtotal:",4, Element.ALIGN_RIGHT));
            table.addCell(GenerateFinalValue("$" + String.format("%.2f",subTotal)));

            table.addCell(GenerateBorderlessCell("Tax:",4,Element.ALIGN_RIGHT));
            table.addCell(GenerateFinalValue("$" + String.format("%.2f",subTotal * 0.13)));

            table.addCell(GenerateBorderlessCell("Order Total:",4,Element.ALIGN_RIGHT));
            PdfPCell totalCell = GenerateFinalValue("$" + String.format("%.2f", poDTO.getTotal()));
            totalCell.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(totalCell);

            preface.add(table);
            addEmptyLine(preface, 3);
            preface.setAlignment(Element.ALIGN_CENTER);
            preface.add(new Paragraph(String.format("%60s", "PO Generated on: " + poDTO.getPodate()), subFont));
            document.add(preface);
            document.close();
            // setting some response headers
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Disposition", "inline; filename=\"sample.PDF\"");
            response.setContentType("application/octet-stream");
            try ( // write ByteArrayOutputStream to the ServletOutputStream
                    OutputStream os = response.getOutputStream()) {
                baos.writeTo(os);
                os.flush();
            }

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
            
        }

    private PdfPCell GenerateCellData(String description) {
        Font smallBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Paragraph(description, smallBold));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
    
    private PdfPCell GenerateBorderlessCell(String title, int colspan, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(title));
        cell.setColspan(colspan);
        cell.setBorder(0);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }
    
    private PdfPCell GenerateFinalValue(String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }
    
    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
