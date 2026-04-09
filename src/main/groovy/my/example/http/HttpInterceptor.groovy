package my.example.http

/**
 * Interceptor that separates pre-request and post-response callbacks.
 * Both closures are optional; omit either one if not needed.
 *
 * <h3>onRequest</h3>
 * <p>Called before the request is sent. Receives:</p>
 * <ul>
 *   <li>{@code String method} — HTTP method (e.g. "GET", "POST")</li>
 *   <li>{@code String url}    — Full request URL</li>
 *   <li>{@code Request.Builder builder} — Request builder for adding headers dynamically</li>
 * </ul>
 *
 * <h3>onResponse</h3>
 * <p>Called after the response is received and parsed. Receives:</p>
 * <ul>
 *   <li>{@link HttpResponse} — The parsed response object</li>
 * </ul>
 *
 * <pre>
 *   def interceptor = new HttpInterceptor(
 *       onRequest:  { method, url, builder -> 
 *           println "[>>] ${method} ${url}"
 *           builder.addHeader("Authorization", "Bearer token123")
 *       },
 *       onResponse: { res -> println "[<<] ${res.statusCode}" }
 *   )
 *   def client = new JsonPlaceholderClient([interceptor])
 * </pre>
 */
class HttpInterceptor {

    /**
     * Called before the request is sent.
     * Signature: {@code { String method, String url, Request.Builder builder -> ... }}
     */
    Closure onRequest

    /**
     * Called after the response is received and parsed.
     * Signature: {@code { HttpResponse response -> ... }}
     */
    Closure onResponse
}
