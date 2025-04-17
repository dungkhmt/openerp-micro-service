package openerp.openerpresourceserver.dto.response;

public record Result(Object data, Object meta, boolean success, String message) {

    public static Result ok(Object data) {
        return new Result(data, null, true, "Success");
    }

    public static Result ok(Object data, Object meta) {
        return new Result(data, meta, true, "Success");
    }

    public static Result error(String message) {
        return new Result(null, null, false, message);
    }
}
