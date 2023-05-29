package wms.utils;

import wms.bean.ImportConfigBean;
import wms.bean.ImportErrorBean;

import java.util.List;

/**
 * @author TruongTX5
 * @version 1.0 R
 * @since Sep 10, 2012
 */
public class ImportValidator {

    public String validate(ImportConfigBean columnConfig, String content, int row, int col) {
        return null;
    }

    public String validate(ImportConfigBean columnConfig, String content, int row, int col, List<ImportErrorBean> errorList) {
        return null;
    }

    public String validateNull(ImportConfigBean columnConfig, String content, int row, int col, List<ImportErrorBean> errorList) {
        return null;
    }

    public void addError(List<ImportErrorBean> errorList, int row, int col, String error, String content) {
        errorList.add(new ImportErrorBean(row, col, error, content));
    }
}