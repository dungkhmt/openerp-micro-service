package thesisdefensejuryassignment.thesisdefenseserver.dto;

public abstract class Response {
    private String message;

    private int statusCode;

    public Response(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
