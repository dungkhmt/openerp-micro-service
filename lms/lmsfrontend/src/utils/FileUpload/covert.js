export async function dataUrlToFile(dataUrl, fileName) {
  const res = await fetch(dataUrl);
  const blob = await res.blob();
  return new File([blob], fileName, { type: "image/png" });
}

export const randomImageName = () => Math.random().toString(36).substr(2, 5);

const IMAGE_EXTENSION = [".png", ".jpg", ".jpeg"];

const PDF_EXTENSION = [".pdf"];

const WORD_EXTENSION = [".doc", ".docx", ".docm"];

const TEXT_EXTENSION = [".txt"];

export const getFileType = (fileName) => {
  const fileNameLowerCase = fileName.toString().toLowerCase();
  let fileType = "img";
  IMAGE_EXTENSION.forEach((extension) => {
    if (fileNameLowerCase.endsWith(extension)) fileType = "img";
  });
  PDF_EXTENSION.forEach((extension) => {
    if (fileNameLowerCase.endsWith(extension)) fileType = "pdf";
  });
  WORD_EXTENSION.forEach((extension) => {
    if (fileNameLowerCase.endsWith(extension)) fileType = "word";
  });
  TEXT_EXTENSION.forEach((extension) => {
    if (fileNameLowerCase.endsWith(extension)) fileType = "txt";
  });
  return fileType;
};

function base64ToArrayBuffer(base64) {
  var binaryString = window.atob(base64);
  var binaryLen = binaryString.length;
  var bytes = new Uint8Array(binaryLen);
  for (var i = 0; i < binaryLen; i++) {
    var ascii = binaryString.charCodeAt(i);
    bytes[i] = ascii;
  }
  return bytes;
}

export const saveByteArray = (fileName, byte, fileType) => {
  let blobType = "";
  switch (fileType) {
    case "pdf":
      blobType = "application/pdf"
      break;
    case "word":
      blobType = "application/msword"
      break;
    case "txt":
      blobType = ""
      break;
    default:
      break;
  }

  var bytes = base64ToArrayBuffer(byte);
  var blob = new Blob([bytes], { type: blobType });
  var link = document.createElement("a");
  link.href = window.URL.createObjectURL(blob);
  link.download = fileName;
  link.click();
}