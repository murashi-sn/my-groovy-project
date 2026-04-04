package my.example.http

/**
 * Represents an HTTP response returned by {@link BaseHttpClient}.
 */
class HttpResponse {

    /** HTTP status code (e.g. 200, 404) */
    final int statusCode

    /** Response headers */
    final Map<String, List<String>> headers

    /** Raw response body as a string */
    final String body

    /**
     * Parsed JSON body. Contains a Map or List when the Content-Type is application/json,
     * otherwise null.
     */
    final Object json

    HttpResponse(int statusCode, Map<String, List<String>> headers, String body, Object json) {
        this.statusCode = statusCode
        this.headers = headers
        this.body = body
        this.json = json
    }

    /** Returns true if the status code is in the 2xx range. */
    boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300
    }
}
