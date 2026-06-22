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
 *   <li>{@code Object context} — Optional custom context object passed from the request method</li>
 * </ul>
 *
 * <h3>onResponse</h3>
 * <p>Called after the response is received and parsed. Receives:</p>
 * <ul>
 *   <li>{@link HttpResponse} — The parsed response object</li>
 * </ul>
 *
 * <pre>
 *   // Usage with context (4 arguments)
 *   def authInterceptor = new HttpInterceptor(
 *       onRequest: { String method, String url, Request.Builder builder, Object context ->
 *           if (context?.useToken) {
 *               builder.addHeader("Authorization", "Bearer token123")
 *           } else {
 *               builder.addHeader("Authorization", "Basic dXNlcjpwYXNz")
 *           }
 *       },
 *       onResponse: { res -> println "[<<] ${res.statusCode}" }
 *   )
 *   client.get("/api/special", [:], [:], [useToken: true])  // Pass context to request
 * </pre>
 */
class HttpInterceptor {

    /**
     * Called before the request is sent.
     * Signature: {@code { String method, String url, Request.Builder builder, Object context -> ... }}
     */
    Closure onRequest

    /**
     * Called after the response is received and parsed.
     * Signature: {@code { HttpResponse response -> ... }}
     */
    Closure onResponse
}
