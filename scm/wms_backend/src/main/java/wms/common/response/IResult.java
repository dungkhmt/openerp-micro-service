package wms.common.response;

import org.springframework.data.domain.Page;
import wms.entity.ResultEntity;

import java.util.*;

public interface IResult<T> {
    default ResultEntity success() throws Exception {
        return success("status", 1);
    }

    default ResultEntity success(String key, Object value) throws Exception {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setData(getResult(key, value));
        return resultEntity;
    }
    default Map<String, Object> getResult(String key, Object value) throws Exception {
        return getResult(Collections.singletonList(key), Collections.singletonList(value));
    }
    /**
     * Create standard response code=0
     */
    default ResultEntity success(List<String> keys, List<Object> values) throws Exception {
        return success(getResult(keys, values));
    }
    default ResultEntity success(Object data) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setData(data);
        return resultEntity;
    }
    default ResultEntity success(Page<T> result) throws Exception {
        return success(result.getTotalElements(), result.getContent());
    }
    default ResultEntity success(Integer total, Iterable<T> data) throws Exception {
        return success(Long.valueOf(total), data);
    }
    default ResultEntity success(Long total, Iterable<T> data) throws Exception {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setData(getResult(total, data));
        return resultEntity;
    }
    default Map<String, Object> getResult(Long total, Iterable<T> objects) throws Exception {
        return getResult(Arrays.asList("total", "data"), Arrays.asList(total, objects));
    }

    /**
     * Create custom result from array keys and values
     */
    default Map<String, Object> getResult(List<String> keys, List<Object> values) throws Exception {
        if (keys.size() != values.size()) {
            throw new Exception("keys and values must be the same size");
        }
        Map<String, Object> mapResult = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            mapResult.put(keys.get(i), values.get(i));
        }
        return mapResult;
    }
}
