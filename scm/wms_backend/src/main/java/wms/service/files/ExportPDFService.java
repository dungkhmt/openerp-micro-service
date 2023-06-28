package wms.service.files;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wms.entity.PurchaseOrder;
import wms.entity.PurchaseOrderItem;

import java.io.*;
import java.util.Arrays;
import java.util.List;

@Service
public class ExportPDFService {
    @Value("${file.font.path}")
    private String fontPath;
    private static final int COLUMNS = 6;
    private static final List<String> purchaseOrderHeaders = Arrays.asList("STT", "Mã sản phẩm", "Tên sản phẩm", "Đơn giá",
            "Số lượng", "Thành tiền"
            );
    public ResponseEntity<InputStreamResource> createPdfOrder(PurchaseOrder order) throws IOException {
        String dest = "sample.pdf";
        Document document = initPDF(dest);
//        Table orderTable = initTable();
//        createOrderHeaders(orderTable);
//        document.add(orderTable);
        float col = 280f;
        float[] colWidth = {col, col};

        PdfFont font = PdfFontFactory.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        document.setFont(font);
        Table headerTable = new Table(colWidth);
        headerTable.setBackgroundColor(new DeviceRgb(60, 239, 39)).setFont(font).setFontColor(Color.WHITE);
        headerTable.addCell(new Cell().add("HOÁ ĐƠN MUA HÀNG").setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setMarginTop(30f).setFontSize(30f).setBorder(Border.NO_BORDER)
        );
        headerTable.addCell(new Cell().add("Chi nhánh scm tại Hà Nội\nĐịa chỉ: 50 Phan Đình Giót, Phương Liệt, Thanh Xuân, Hà Nội\n" +
                "Liên hệ: 0123456789" +
                "\nHóa đơn bán hàng số: ...")
                .setMarginTop(30f).setMarginBottom(30f)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
        );
        // Closing the document
        document.add(headerTable);
        Table orderTable = new Table(6);
        for (String header : purchaseOrderHeaders) {
            orderTable.addCell(new Cell().add(header)).setFontSize(20f).setBackgroundColor(Color.CYAN);
        }
        List<PurchaseOrderItem> purchaseOrderItems = order.getPurchaseOrderItems();
        for (int i = 0; i < purchaseOrderItems.size(); i++) {
            PurchaseOrderItem item = purchaseOrderItems.get(i);
            double itemPrice = item.getPriceUnit();
            int itemQuantity = item.getQuantity();
            double total = itemPrice * itemQuantity;
            orderTable.addCell(new Cell().add(String.valueOf(i)).setFontSize(14f));
            orderTable.addCell(new Cell().add(item.getProduct().getCode()).setFontSize(14f));
            orderTable.addCell(new Cell().add(item.getProduct().getName()).setFontSize(14f));
            orderTable.addCell(new Cell().add(String.valueOf(itemPrice)).setFontSize(14f));
            orderTable.addCell(new Cell().add(String.valueOf(itemQuantity)).setFontSize(14f));
            orderTable.addCell(new Cell().add(String.valueOf(total)).setFontSize(14f));
        }
        orderTable.addCell(new Cell(0, 3).add("Tổng cộng"));
        orderTable.addCell(new Cell(0, 1));
        orderTable.addCell(new Cell(0, 1).add(String.valueOf(order.getTotalMoney())));
        orderTable.addCell(new Cell(0, 3).add("Thuế"));
        orderTable.addCell(new Cell(0, 1).add(String.valueOf(order.getVat())));
        orderTable.addCell(new Cell(0, 1).add(String.valueOf(order.getVat() * order.getTotalMoney())));
        orderTable.addCell(new Cell(0, 3).add("Tổng phải trả"));
        orderTable.addCell(new Cell(0, 1));
        orderTable.addCell(new Cell(0, 1).add(String.valueOf(order.getTotalPayment())));
        document.add(orderTable);
        document.close();
        HttpHeaders responseHeader = new HttpHeaders();
        File file = new File(dest);
        byte[] data = FileUtils.readFileToByteArray(file);
        responseHeader.setContentType(MediaType.APPLICATION_PDF);
        responseHeader.set("Content-disposition", "attachment; filename=" + file.getName());
        responseHeader.setContentLength(data.length);
        InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        return new ResponseEntity<>(inputStreamResource, responseHeader, HttpStatus.OK);
    }
    private Document initPDF(String dest) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();
        pdfDoc.setDefaultPageSize(PageSize.A4);
        return new Document(pdfDoc);
    }
    private Table initTable() {
        Table table = new Table(ExportPDFService.COLUMNS);
        table.setPadding(50);
        for (String header : ExportPDFService.purchaseOrderHeaders) {
            Cell cell = new Cell();
            cell.add(header);
            table.addCell(cell);
        }
        return table;
    }
    private void createOrderHeaders(Table currTable) {
        currTable.addCell(new Cell().add("HOÁ ĐƠN MUA HÀNG"));
        currTable.addCell(new Cell().add("Chi nhánh scm tại Hà Nội\nĐịa chỉ: 50 Phan Đình Giót, Phương Liệt, Thanh Xuân, Hà Nội\n" +
                "Liên hệ: 0123456789" +
                "Hóa đơn bán hàng số: ..."));
    }
}
