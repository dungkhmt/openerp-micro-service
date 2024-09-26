package wms.service.files;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.web.multipart.MultipartFile;
import wms.exception.CustomException;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public interface IFileService {
    public File downloadTemplateExcelFile(String templateSource, String templateName) throws Exception;
    Iterator<Row> initWorkbookRow(MultipartFile file) throws CustomException, IOException;
}
