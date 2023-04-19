package wms.common.response;

import org.springframework.web.client.HttpClientErrorException;
import wms.entity.ResultEntity;
import wms.exception.CustomException;
import wms.utils.LogUtils;

public interface IErrorResult {
    default ResultEntity error(Exception ex) {
        LogUtils.getInstance().error(ex);
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setMessage(ex.getMessage());
        if (ex instanceof CustomException) {
            CustomException customException = (CustomException) ex;
            resultEntity.setCode(customException.getCode());
            resultEntity.setData(customException.getData());
        } else if (ex instanceof HttpClientErrorException.Unauthorized){
            resultEntity.setCode(401);
            resultEntity.setMessage("Unauthorized " + ex.getMessage());
        }
        else {
            resultEntity.setCode(500);
            resultEntity.setMessage("Internal server err: " + ex.getMessage());
        }
        return resultEntity;
    }
}
