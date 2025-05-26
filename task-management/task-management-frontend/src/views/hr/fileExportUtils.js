// fileExportUtils.js
import jsPDF from "jspdf";
import "jspdf-autotable";
import dayjs from "dayjs";

// BƯỚC QUAN TRỌNG: Đảm bảo bạn đã tạo các file này và chúng export đúng chuỗi base64
// Ví dụ đường dẫn, sửa lại cho đúng với cấu trúc project của bạn
import { robotoRegularBase64 } from './fonts/roboto-regular-font.js';
import { robotoBoldBase64 } from './fonts/roboto-bold-font.js';

const FONT_ROBOTO_REGULAR = "RobotoCustomRegular";
const FONT_ROBOTO_BOLD = "RobotoCustomBold";

// Hàm này sẽ được gọi cho mỗi instance `doc` mới
const registerPdfFontForThisDoc = (docInstance) => {
  try {
    // Luôn cố gắng thêm VFS và addFont cho instance `doc` hiện tại.
    // jsPDF có thể sẽ không thêm lại nếu tên file/font đã tồn tại trong instance đó,
    // nhưng việc gọi lại đảm bảo font được biết đến cho instance này.
    if (typeof robotoRegularBase64 === 'string' && robotoRegularBase64.length > 1000) {
      docInstance.addFileToVFS('Roboto-Regular.ttf', robotoRegularBase64);
      docInstance.addFont('Roboto-Regular.ttf', FONT_ROBOTO_REGULAR, 'normal');
    } else {
      console.warn("Chuỗi base64 cho Roboto Regular bị thiếu hoặc không hợp lệ. Font PDF có thể không hiển thị đúng tiếng Việt.");
    }

    if (typeof robotoBoldBase64 === 'string' && robotoBoldBase64.length > 1000) {
      docInstance.addFileToVFS('Roboto-Bold.ttf', robotoBoldBase64);
      docInstance.addFont('Roboto-Bold.ttf', FONT_ROBOTO_BOLD, 'bold');
    } else {
      console.warn("Chuỗi base64 cho Roboto Bold bị thiếu hoặc không hợp lệ. Font PDF có thể không hiển thị đúng tiếng Việt.");
    }
  } catch (e) {
    console.error("Lỗi nghiêm trọng khi đăng ký font cho instance PDF:", e);
  }
};

const ensureHexColor = (colorString, fallbackColor = '#000000') => {
  if (typeof colorString !== 'string') return fallbackColor;
  if (colorString.startsWith('rgba')) {
    const parts = colorString.match(/rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*([\d.]+))?\)/);
    if (parts) {
      const r = parseInt(parts[1]).toString(16).padStart(2, '0');
      const g = parseInt(parts[2]).toString(16).padStart(2, '0');
      const b = parseInt(parts[3]).toString(16).padStart(2, '0');
      return `#${r}${g}${b}`;
    }
    return fallbackColor;
  }
  if (colorString.startsWith('#')) {
    return colorString;
  }
  return fallbackColor;
};

export const exportToPDF = ({
                              data,
                              columns,
                              title = "Danh sách",
                              fileName = "ExportedDocument.pdf",
                              themePalette,
                              customColumnWidths = {}
                            }) => {
  const doc = new jsPDF({
    orientation: "p",
    unit: "pt",
    format: "a4"
  });

  // Gọi hàm đăng ký font cho instance `doc` này
  registerPdfFontForThisDoc(doc);

  let activeFont = FONT_ROBOTO_REGULAR;
  let activeBoldFont = FONT_ROBOTO_BOLD;

  // Cố gắng đặt font, jsPDF sẽ fallback nếu font tùy chỉnh không được đăng ký thành công
  try {
    doc.setFont(FONT_ROBOTO_REGULAR, 'normal');
  } catch (e) {
    console.warn(`Không tìm thấy font ${FONT_ROBOTO_REGULAR} cho jsPDF, sử dụng font mặc định. Lỗi: ${e.message}`);
    activeFont = doc.getFont().fontName; // Lấy font fallback
    activeBoldFont = doc.getFont().fontName; // Giả sử font fallback cũng dùng cho bold (jsPDF sẽ cố gắng làm đậm)
  }

  const headerColor = ensureHexColor(themePalette?.primary?.main, "#1976d2");
  const headerTextColor = ensureHexColor(themePalette?.primary?.contrastText, "#ffffff");
  const rowEvenColor = ensureHexColor(themePalette?.grey?.[100], "#f5f5f5");
  const borderColor = ensureHexColor(themePalette?.divider, "#e0e0e0");
  const primaryTextColorPdf = ensureHexColor(themePalette?.text?.primary, "#212121");
  // const secondaryTextColorPdf = ensureHexColor(themePalette?.text?.secondary, "#757575"); // Không còn dùng cho "Ngày xuất"
  const disabledTextColorPdf = ensureHexColor(themePalette?.text?.disabled, "#bdbdbd");

  // Tiêu đề chính của tài liệu
  try {
    doc.setFont(activeBoldFont, 'bold');
  } catch (e) {
    console.warn(`Không tìm thấy font ${activeBoldFont} (bold) cho tiêu đề, sử dụng font ${activeFont} với style bold. Lỗi: ${e.message}`);
    doc.setFont(activeFont, 'bold'); // Yêu cầu style bold cho font hiện tại
  }
  doc.setFontSize(18);
  doc.setTextColor(primaryTextColorPdf);
  const pageWidth = doc.internal.pageSize.getWidth();
  const titleWidth = doc.getTextWidth(title);
  doc.text(title, (pageWidth - titleWidth) / 2, 60); // Tọa độ y=60

  // Bỏ dòng "Ngày xuất"
  // doc.setFont(activeFont, 'normal');
  // doc.setFontSize(10);
  // doc.setTextColor(secondaryTextColorPdf);
  // doc.text(`Ngày xuất: ${dayjs().format("DD/MM/YYYY HH:mm:ss")}`, 40, 75);

  const tableColumns = columns.map(col => col.Header);
  const tableRows = data.map((row, rowIndex) => {
    return columns.map(col => {
      let cellValue;
      if (col.accessor && typeof col.accessor === 'function') {
        cellValue = col.accessor(row, rowIndex);
      } else if (col.accessor && row[col.accessor] !== undefined && row[col.accessor] !== null) {
        cellValue = row[col.accessor];
      } else {
        cellValue = "-";
      }
      return String(cellValue).split('\n');
    });
  });

  doc.autoTable({
    startY: 75, // Điều chỉnh startY vì đã bỏ dòng ngày xuất
    head: [tableColumns],
    body: tableRows,
    theme: 'grid',
    styles: {
      font: activeFont,
      fontSize: 9,
      cellPadding: { top: 5, right: 6, bottom: 5, left: 6 },
      valign: 'middle',
      lineWidth: 0.2,
      lineColor: borderColor,
      overflow: 'linebreak',
    },
    headStyles: {
      font: activeBoldFont, // Sử dụng biến đã kiểm tra
      fontStyle: 'bold',    // Thêm thuộc tính này để jsPDF cố gắng làm đậm nếu font bold riêng không có
      fillColor: headerColor,
      textColor: headerTextColor,
      fontSize: 10,
      halign: 'center',
      valign: 'middle',
      lineWidth: 0.2,
      lineColor: borderColor,
    },
    bodyStyles: {
      textColor: primaryTextColorPdf,
      lineWidth: 0.2,
      lineColor: borderColor,
    },
    alternateRowStyles: {
      fillColor: rowEvenColor,
    },
    columnStyles: {
      0: { cellWidth: 35, halign: 'center' }, // STT
      ...customColumnWidths
    },
    didDrawPage: function (hookData) {
      const pageCountTotal = doc.internal.getNumberOfPages();
      try {
        doc.setFont(activeFont, 'normal');
      } catch (e) {
        doc.setFont(undefined, 'normal'); // Fallback nếu có lỗi
      }
      doc.setFontSize(8);
      doc.setTextColor(disabledTextColorPdf);
      doc.text(`Trang ${hookData.pageNumber} / ${pageCountTotal}`, pageWidth / 2, doc.internal.pageSize.height - 20, { align: 'center' });
    }
  });

  doc.save(fileName);
};

export const prepareCSVData = ({ data, columns, currentPage, itemsPerPage }) => {
  if (!data || data.length === 0) return [];
  return data.map((row, index) => {
    const csvRow = {};
    columns.forEach(col => {
      if (col.id === 'stt' && typeof col.accessor === 'function') {
        csvRow[col.Header] = col.accessor(row, index);
      } else if (typeof col.accessor === 'string' && col.id !== 'actions') {
        const value = row[col.accessor];
        csvRow[col.Header] = (value === null || value === undefined) ? "" : String(value);
      }
    });
    return csvRow;
  });
};