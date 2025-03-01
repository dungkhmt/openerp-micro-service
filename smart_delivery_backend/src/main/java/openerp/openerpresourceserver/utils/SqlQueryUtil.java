package openerp.openerpresourceserver.utils;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SqlQueryUtil {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SqlQueryUtil(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Thực thi truy vấn SQL và trả về danh sách các đối tượng.
     *
     * @param sql        Câu truy vấn SQL.
     * @param parameters Tham số truy vấn.
     * @param clazz      Lớp đối tượng cần ánh xạ.
     * @param <T>        Kiểu dữ liệu của đối tượng.
     * @return Danh sách các đối tượng.
     */
    public <T> List<T> queryForList(String sql, MapSqlParameterSource parameters, Class<T> clazz) {
        return jdbcTemplate.query(sql, parameters, new BeanPropertyRowMapper<>(clazz));
    }

    /**
     * Thực thi truy vấn SQL và trả về một đối tượng duy nhất (hoặc null nếu không tìm thấy).
     *
     * @param sql        Câu truy vấn SQL.
     * @param parameters Tham số truy vấn.
     * @param clazz      Lớp đối tượng cần ánh xạ.
     * @param <T>        Kiểu dữ liệu của đối tượng.
     * @return Đối tượng duy nhất hoặc null.
     */
    public <T> Optional<T> queryForObject(String sql, MapSqlParameterSource parameters, Class<T> clazz) {
        List<T> results = queryForList(sql, parameters, clazz);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Thực thi truy vấn SQL và trả về danh sách các giá trị nguyên thủy.
     *
     * @param sql        Câu truy vấn SQL.
     * @param parameters Tham số truy vấn.
     * @param clazz      Lớp kiểu dữ liệu nguyên thủy (ví dụ: Integer.class, String.class).
     * @param <T>        Kiểu dữ liệu nguyên thủy.
     * @return Danh sách các giá trị nguyên thủy.
     */
    public <T> List<T> queryForListPrimitive(String sql, MapSqlParameterSource parameters, Class<T> clazz) {
        return jdbcTemplate.queryForList(sql, parameters, clazz);
    }

    /**
     * Thực thi truy vấn SQL và trả về một giá trị nguyên thủy duy nhất (hoặc null nếu không tìm thấy).
     *
     * @param sql        Câu truy vấn SQL.
     * @param parameters Tham số truy vấn.
     * @param clazz      Lớp kiểu dữ liệu nguyên thủy (ví dụ: Integer.class, String.class).
     * @param <T>        Kiểu dữ liệu nguyên thủy.
     * @return Giá trị nguyên thủy hoặc null.
     */
    public <T> Optional<T> queryForObjectPrimitive(String sql, MapSqlParameterSource parameters, Class<T> clazz) {
        List<T> results = queryForListPrimitive(sql, parameters, clazz);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Thực thi truy vấn SQL cập nhật (INSERT, UPDATE, DELETE).
     *
     * @param sql        Câu truy vấn SQL.
     * @param parameters Tham số truy vấn.
     * @return Số bản ghi bị ảnh hưởng.
     */
    public int executeUpdate(String sql, MapSqlParameterSource parameters) {
        return jdbcTemplate.update(sql, parameters);
    }
}