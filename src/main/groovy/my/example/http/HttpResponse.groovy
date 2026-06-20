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

    /** Request information captured at the time of the request */
    final RequestInfo requestInfo

    HttpResponse(int statusCode, Map<String, List<String>> headers, String body, Object json, RequestInfo requestInfo) {
        this.statusCode = statusCode
        this.headers = headers
        this.body = body
        this.json = json
        this.requestInfo = requestInfo
    }

    /** Returns true if the status code is in the 2xx range. */
    boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300
    }

    /**
     * Encapsulates request-time information: query parameters and request headers.
     * All fields are immutable (final).
     */
    static class RequestInfo {

        /** Query parameters extracted from the request URL */
        final Map<String, String> query

        /** Request headers with lowercase keys and values as lists */
        final Map<String, List<String>> headers

        RequestInfo(Map<String, String> query, Map<String, List<String>> headers) {
            this.query = query ?: [:]
            this.headers = headers ?: [:]
        }
    }
}
