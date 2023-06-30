package wms.service.files;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.text.pdf.BaseFont;
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
import java.net.MalformedURLException;
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
    public ResponseEntity<InputStreamResource> createPdfOrder(PurchaseOrder order, String dest) throws IOException {
        Document document = initPDF(dest);
        PdfFont font = PdfFontFactory.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        document.setFont(font);
        Table headerTable = initHeader(font);
        Table title = initTitle();
        Table orderTable = initOrderTable(order);

        document.add(headerTable);
        document.add(title);
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
    private Table initHeader(PdfFont font) throws MalformedURLException {
        DeviceRgb color = new DeviceRgb(60, 290, 30);
        float[] colWidth = {70f, 380, 145f};
        Table headerTable = new Table(colWidth);
        String imFile = "/home/hoangbui/Documents/DATN/openerp-micro-service/scm/wms_backend/src/main/resources/images/logo.jpg";
        ImageData imageData = ImageDataFactory.create(imFile);
        Image image = new Image(imageData)
                .setFixedPosition(50f, 750f)
                .setTextAlignment(TextAlignment.CENTER)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setWidth(50f)
                .setHeight(50f);

        headerTable.addCell(new Cell(0, 3).add(image)
                .setBorder(Border.NO_BORDER).setBackgroundColor(color));
        headerTable.setBackgroundColor(color).setFont(font).setFontColor(Color.WHITE);
        headerTable.addCell(new Cell(0, 3).add("HOÁ ĐƠN MUA HÀNG")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setMarginTop(30f)
                .setFontSize(30f)
                .setBorder(Border.NO_BORDER)
        );

        headerTable.addCell(new Cell(0, 3)
                .add(
                        "Hóa đơn mua hàng số: ...\n" +
                        "Chi nhánh SCM Hà Nội\n"+
                        "Liên hệ: 0123456789\n"+
                        "Địa chỉ: 50 Phan Đình Giót, Phương Liệt, Thanh Xuân, Hà Nội\n")
                .setMarginTop(30f)
                .setMarginBottom(30f)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
        );
        return headerTable;
    }
    private Table initTitle() {
        Table title = new Table(1);
        title.addCell(new Cell().add("CHI TIẾT ĐƠN HÀNG")
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setFontSize(20f)
                .setBorder(Border.NO_BORDER)
                .setFontColor(Color.GREEN)).setMarginTop(50f);
        return title;
    }
    private Table initOrderTable(PurchaseOrder order) {
        float[] orderTableColWidth = {20f, 60f, 200f, 100f, 80f, 125f};
        Table orderTable = new Table(orderTableColWidth);
        for (String header : purchaseOrderHeaders) {
            orderTable.addCell(new Cell().add(header))
                    .setFontSize(14f)
                    .setBackgroundColor(Color.CYAN)
                    .setTextAlignment(TextAlignment.CENTER);
        }
        List<PurchaseOrderItem> purchaseOrderItems = order.getPurchaseOrderItems();
        for (int i = 0; i < purchaseOrderItems.size(); i++) {
            PurchaseOrderItem item = purchaseOrderItems.get(i);
            double itemPrice = item.getPriceUnit();
            int itemQuantity = item.getQuantity();
            double total = itemPrice * itemQuantity;
            orderTable.addCell(new Cell().add(String.valueOf(i)).setFontSize(10f).setTextAlignment(TextAlignment.CENTER));
            orderTable.addCell(new Cell().add(item.getProduct().getCode()).setFontSize(10f).setTextAlignment(TextAlignment.LEFT));
            orderTable.addCell(new Cell().add(item.getProduct().getName()).setFontSize(10f).setTextAlignment(TextAlignment.LEFT));
            orderTable.addCell(new Cell().add(String.valueOf(itemPrice)).setFontSize(10f).setTextAlignment(TextAlignment.RIGHT));
            orderTable.addCell(new Cell().add(String.valueOf(itemQuantity)).setFontSize(10f).setTextAlignment(TextAlignment.RIGHT));
            orderTable.addCell(new Cell().add(String.valueOf(total)).setFontSize(10f).setTextAlignment(TextAlignment.RIGHT));
        }
        orderTable.addCell(new Cell(0, 3).add("Tổng cộng:").setBorderRight(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        orderTable.addCell(new Cell(0, 3).add(String.valueOf(order.getTotalMoney())).setTextAlignment(TextAlignment.RIGHT));
        orderTable.addCell(new Cell(0, 3).add("Thuế:").setBorderRight(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        orderTable.addCell(new Cell(0, 1).add(order.getVat() + " %").setHorizontalAlignment(HorizontalAlignment.CENTER));
        orderTable.addCell(new Cell(0, 2).add(String.valueOf(order.getVat() * order.getTotalMoney())).setBorderLeft(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        orderTable.addCell(new Cell(0, 3).add("Tổng phải trả:").setBorderRight(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        orderTable.addCell(new Cell(0, 3).add(String.valueOf(order.getTotalPayment())).setTextAlignment(TextAlignment.RIGHT));
        return orderTable;
    }
}
