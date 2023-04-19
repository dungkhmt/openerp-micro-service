package wms.service;

import org.springframework.stereotype.Service;
import wms.common.response.IPaging;
import wms.common.response.IResult;
import wms.dto.ReturnPaginationDTO;
import wms.exception.CustomException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class BaseService implements IResult, IPaging {

    /**
     * format du lieu
     *
     * @param format
     * @param args
     * @return
     */
    protected String format(String format, Object... args) {
        return String.format(format, args);
    }

    CustomException getUnauthentication(String message) {
        return new CustomException(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    CustomException getUnauthentication() {
        return new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token ");
    }

    protected CustomException caughtException(String message, Object... objects) {
        return new CustomException(String.format(message, objects));
    }

    /**
     * Tra ve loi voi cac thong so
     *
     * @param code    ma loi
     * @param message Noi dung loi
     */
    protected CustomException caughtException(int code, String message, Object... objects) {
        String testMessage = String.format(message, objects);
        return new CustomException(code, String.format(message, objects));
    }

    protected CustomException caughtException(int code, String message) {
        return new CustomException(code, message);
    }

    public static <T> ReturnPaginationDTO<T> getPaginationResult(List<T> content, int page, int totalPages, long totalElements){
        ReturnPaginationDTO<T> result= new ReturnPaginationDTO<>();
        result.setContent(content);
        result.setPageNumber(page);
        result.setTotalPages(totalPages);
        result.setTotalElements((int)totalElements);
        return result;
    }
}
