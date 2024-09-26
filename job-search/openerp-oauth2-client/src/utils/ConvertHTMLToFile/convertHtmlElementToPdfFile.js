import html2canvas from "html2canvas";
import { convertMmToPixel } from "./convertMMtoPixel";
import jsPDF from "jspdf";

export const convertHtmlElementToPdfFile = async (
  element,
  fileTitle
) => {
  const canvas = await html2canvas(element, {
    backgroundColor: '#ffffff', // Set background color
    scale: 2, // Increase scale for better quality
    useCORS: true, // Enable cross-origin resource sharing if needed
  });
  const doc = new jsPDF({
    orientation: "p",
    unit: "px",
    format: [convertMmToPixel(110), convertMmToPixel(150)],
  });
  const imgData = canvas.toDataURL("image/png");
  doc.addImage(imgData, "PNG", 0, 0, convertMmToPixel(110), convertMmToPixel(150));

  const blob = doc.output("blob");
  return new File([blob], `${fileTitle}.pdf`, {
    type: "application/pdf",
  });
};
