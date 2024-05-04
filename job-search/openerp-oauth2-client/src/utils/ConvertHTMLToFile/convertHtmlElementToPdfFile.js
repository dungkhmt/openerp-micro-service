import html2canvas from "html2canvas";
import { convertMmToPixel } from "./convertMMtoPixel";
import jsPDF from "jspdf";

export const convertHtmlElementToPdfFile = async (
  element,
  fileTitle
) => {
  const canvas = await html2canvas(element);
  const doc = new jsPDF({
    orientation: "p",
    unit: "px",
    format: [convertMmToPixel(210), canvas.height],
  });
  const imgData = canvas.toDataURL("image/png");
  doc.addImage(imgData, "PNG", 0, 0, convertMmToPixel(210), canvas.height);

  const blob = doc.output("blob");
  return new File([blob], `${fileTitle}.pdf`, {
    type: "application/pdf",
  });
};
