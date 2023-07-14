package wms.service.files;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import wms.common.enums.ErrorCode;
import wms.exception.CustomException;
import wms.service.BaseService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Service
public class FileService extends BaseService implements IFileService {
    @Override
    public File downloadTemplateExcelFile(String templateSource, String templateName) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(templateSource);
        Workbook workbook = new XSSFWorkbook(inputStream);
        File tempFile = File.createTempFile(templateName, ".xlsx");
        FileOutputStream fos = new FileOutputStream(tempFile);
        workbook.write(fos);
        fos.close();
        return tempFile;
    }

    public Iterator<Row> initWorkbookRow(MultipartFile file) throws CustomException, IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (fileName.equals("")) {
            throw caughtException(ErrorCode.USER_ACTION_FAILED.getCode(), "Hãy tải file lên hệ thống!");
        }
        String fileExt = "";
        if (fileName.contains(".")) {
            fileExt = fileName.substring(fileName.lastIndexOf("."));
        }
        if (!fileExt.contains("xls")) {
            throw caughtException(ErrorCode.FORMAT.getCode(), "Wrong file type. Only support .xls and .xlsx file extensions");
        }
        InputStream fis = file.getInputStream();
        Workbook workbook = null;
        Sheet sheet = null;
        if (fileExt.equals(".xls")) {
            workbook = new HSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0);
        }
        else {
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0);
        }
        return sheet.iterator();
    }
}
