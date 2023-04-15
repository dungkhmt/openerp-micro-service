package wms.common.response;

import org.springframework.data.domain.Page;
import wms.entity.ResultEntity;

import java.util.*;

public interface IResult<T> {
    default ResultEntity ok() throws Exception {
        return ok("status", 1);
    }

    /**
     * Create standard response code=0
     */
    default ResultEntity ok(Object data) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setData(data);
        return resultEntity;
    }


    default ResultEntity ok(Page<T> result) throws Exception {
        return ok(result.getTotalElements(), result.getContent());
    }

    default ResultEntity ok(Long total, Iterable<T> data) throws Exception {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setData(getResult(total, data));
        return resultEntity;
    }

    default ResultEntity ok(Integer total, Iterable<T> data) throws Exception {
        return ok(Long.valueOf(total), data);
    }

    default ResultEntity ok(String key, Object value) throws Exception {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setData(getResult(key, value));
        return resultEntity;
    }

    default ResultEntity ok(List<String> keys, List<Object> values) throws Exception {
        return ok(getResult(keys, values));
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

    default Map<String, Object> getResult(Long total, Iterable<T> objects) throws Exception {
        return getResult(Arrays.asList("total", "data"), Arrays.asList(total, objects));
    }

    /**
     * Create result with one key and value
     */
    default Map<String, Object> getResult(String key, Object value) throws Exception {
        return getResult(Collections.singletonList(key), Collections.singletonList(value));
    }
}
