package my.example.http

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import my.example.http.cookies.InMemoryCookieJar
import okhttp3.*

/**
 * Base class for HTTP clients.
 * Wraps OkHttp and provides common functionality: cookie sharing, interceptors,
 * GET/POST/PUT/DELETE requests, and automatic JSON parsing of responses.
 *
 * <h3>Cookie sharing</h3>
 * <p>To share cookies across different client instances, pass a shared
 * {@link InMemoryCookieJar} to each constructor.</p>
 *
 * <h3>Interceptors</h3>
 * <p>Pass a list of {@link HttpInterceptor} instances to the constructor.
 * Each interceptor can define an {@code onRequest} callback (called before the request is sent)
 * and/or an {@code onResponse} callback (called after the response is parsed).
 * The same list can be shared across multiple client instances.</p>
 *
 * <pre>
 *   def interceptors = [
 *       new HttpInterceptor(
 *           onRequest:  { method, url, headers -> println "[>>] ${method} ${url}" },
 *           onResponse: { res -> println "[<<] ${res.statusCode}" }
 *       )
 *   ]
 *   def cookieJar = new InMemoryCookieJar()
 *   def clientA = new ServiceAClient(cookieJar, interceptors)
 *   def clientB = new ServiceBClient(cookieJar, interceptors)
 * </pre>
 */
abstract class BaseHttpClient {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8")

    /** Base URL for requests (e.g. "https://api.example.com") */
    private final String baseUrl

    protected final OkHttpClient client
    private final List<HttpInterceptor> interceptors

    /** @param baseUrl Base URL for requests   */
    BaseHttpClient(String baseUrl) {
        this(baseUrl, new InMemoryCookieJar(), [])
    }

    /**
     * Use this constructor to share cookies across different client instances.
     * @param baseUrl Base URL for requests
     * @param cookieJar Shared CookieJar instance
     */
    BaseHttpClient(String baseUrl, CookieJar cookieJar) {
        this(baseUrl, cookieJar, [])
    }

    /**
     * Use this constructor to register interceptors.
     * @param baseUrl Base URL for requests
     * @param interceptors List of {@link HttpInterceptor} applied to every request
     */
    BaseHttpClient(String baseUrl, List<HttpInterceptor> interceptors) {
        this(baseUrl, new InMemoryCookieJar(), interceptors)
    }

    /**
     * Full constructor. Use this to share both a cookie store and interceptors across instances.
     * @param baseUrl Base URL for requests
     * @param cookieJar Shared CookieJar instance
     * @param interceptors List of {@link HttpInterceptor} applied to every request
     */
    BaseHttpClient(String baseUrl, CookieJar cookieJar, List<HttpInterceptor> interceptors) {
        this.baseUrl = baseUrl
        this.interceptors = new ArrayList<>(interceptors)
        this.client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build()
    }

    /**
     * Adds an interceptor after initialization.
     * @param interceptor The interceptor to add
     */
    protected void addInterceptor(HttpInterceptor interceptor) {
        this.interceptors.add(interceptor)
    }

    /**
     * Sends a GET request.
     * @param path Path relative to the base URL (e.g. "/users/1")
     * @param headers Additional request headers (optional)
     * @param params URL query parameters (optional)
     * @param context Optional custom context object passed to interceptors
     * @return {@link HttpResponse} containing statusCode, headers, body, and parsed json
     */
    protected HttpResponse get(String path, Map<String, String> headers = [:], Map<String, String> params = [:], Object context = null) {
        def request = buildRequestBuilder(path, headers, params).get().build()
        return execute(request, context)
    }

    /**
     * Sends a POST request.
     * @param path Path relative to the base URL
     * @param body Request body (Map or List will be serialized to JSON)
     * @param headers Additional request headers (optional)
     * @param params URL query parameters (optional)
     * @param context Optional custom context object passed to interceptors
     * @return {@link HttpResponse}
     */
    protected HttpResponse post(String path, Object body, Map<String, String> headers = [:], Map<String, String> params = [:], Object context = null) {
        def requestBody = RequestBody.create(JsonOutput.toJson(body), JSON_MEDIA_TYPE)
        def request = buildRequestBuilder(path, headers, params).post(requestBody).build()
        return execute(request, context)
    }

    /**
     * Sends a POST request with a raw JSON string as the request body.
     * Unlike {@code post(path, Object)}, the string is sent as-is without re-serialization.
     * @param path Path relative to the base URL
     * @param jsonString Raw JSON string to send as the request body
     * @param headers Additional request headers (optional)
     * @param params URL query parameters (optional)
     * @param context Optional custom context object passed to interceptors
     * @return {@link HttpResponse}
     */
    protected HttpResponse post(String path, String jsonString, Map<String, String> headers = [:], Map<String, String> params = [:], Object context = null) {
        def request = buildRequestBuilder(path, headers, params).post(RequestBody.create(jsonString, JSON_MEDIA_TYPE)).build()
        return execute(request, context)
    }

    /**
     * Sends a POST request with the contents of a JSON file as the request body.
     * @param path Path relative to the base URL
     * @param jsonFile JSON file to send as the request body
     * @param headers Additional request headers (optional)
     * @param params URL query parameters (optional)
     * @param context Optional custom context object passed to interceptors
     * @return {@link HttpResponse}
     */
    protected HttpResponse post(String path, File jsonFile, Map<String, String> headers = [:], Map<String, String> params = [:], Object context = null) {
        def request = buildRequestBuilder(path, headers, params).post(RequestBody.create(jsonFile, JSON_MEDIA_TYPE)).build()
        return execute(request, context)
    }

    /**
     * Sends a PUT request.
     * @param path Path relative to the base URL
     * @param body Request body (Map or List will be serialized to JSON)
     * @param headers Additional request headers (optional)
     * @param params URL query parameters (optional)
     * @param context Optional custom context object passed to interceptors
     * @return {@link HttpResponse}
     */
    protected HttpResponse put(String path, Object body, Map<String, String> headers = [:], Map<String, String> params = [:], Object context = null) {
        def requestBody = RequestBody.create(JsonOutput.toJson(body), JSON_MEDIA_TYPE)
        def request = buildRequestBuilder(path, headers, params).put(requestBody).build()
        return execute(request, context)
    }

    /**
     * Sends a PUT request with a raw JSON string as the request body.
     * Unlike {@code put(path, Object)}, the string is sent as-is without re-serialization.
     * @param path Path relative to the base URL
     * @param jsonString Raw JSON string to send as the request body
     * @param headers Additional request headers (optional)
     * @param params URL query parameters (optional)
     * @param context Optional custom context object passed to interceptors
     * @return {@link HttpResponse}
     */
    protected HttpResponse put(String path, String jsonString, Map<String, String> headers = [:], Map<String, String> params = [:], Object context = null) {
        def request = buildRequestBuilder(path, headers, params).put(RequestBody.create(jsonString, JSON_MEDIA_TYPE)).build()
        return execute(request, context)
    }

    /**
     * Sends a PUT request with the contents of a JSON file as the request body.
     * @param path Path relative to the base URL
     * @param jsonFile JSON file to send as the request body
     * @param headers Additional request headers (optional)
     * @param params URL query parameters (optional)
     * @param context Optional custom context object passed to interceptors
     * @return {@link HttpResponse}
     */
    protected HttpResponse put(String path, File jsonFile, Map<String, String> headers = [:], Map<String, String> params = [:], Object context = null) {
        def request = buildRequestBuilder(path, headers, params).put(RequestBody.create(jsonFile, JSON_MEDIA_TYPE)).build()
        return execute(request, context)
    }

    /**
     * Sends a DELETE request.
     * @param path Path relative to the base URL
     * @param headers Additional request headers (optional)
     * @param params URL query parameters (optional)
     * @param context Optional custom context object passed to interceptors
     * @return {@link HttpResponse}
     */
    protected HttpResponse delete(String path, Map<String, String> headers = [:], Map<String, String> params = [:], Object context = null) {
        def request = buildRequestBuilder(path, headers, params).delete().build()
        return execute(request, context)
    }

    /**
     * Sends a HEAD request.
     * @param path Path relative to the base URL
     * @param headers Additional request headers (optional)
     * @param params URL query parameters (optional)
     * @param context Optional custom context object passed to interceptors
     * @return {@link HttpResponse}
     */
    protected HttpResponse head(String path, Map<String, String> headers = [:], Map<String, String> params = [:], Object context = null) {
        def request = buildRequestBuilder(path, headers, params).head().build()
        return execute(request, context)
    }

    private Request.Builder buildRequestBuilder(String path, Map<String, String> headers, Map<String, String> params = [:]) {
        def urlBuilder = HttpUrl.get(baseUrl + path).newBuilder()
        params.each { key, value -> urlBuilder.addQueryParameter(key, value) }
        def builder = new Request.Builder().url(urlBuilder.build())
        headers.each { key, value -> builder.addHeader(key, value) }
        return builder
    }

    private HttpResponse execute(Request request, Object context = null) {
        // Create builder from existing request to allow interceptors to add headers dynamically
        def builder = request.newBuilder()

        // Allow interceptors to add headers
        interceptors.each {
            it.onRequest?.call(request.method(), request.url().toString(), builder, context)
        }

        def finalRequest = builder.build()

        try (Response response = client.newCall(finalRequest).execute()) {
            String bodyString = null
            Object json = null
            // HEAD responses have no body, so skip parsing
            if (request.method() != "HEAD") {
                bodyString = response.body()?.string() ?: ""
                json = null
                // Parse the body as JSON when Content-Type is application/json
                def contentType = response.header("Content-Type") ?: ""
                if (contentType.contains("application/json") && bodyString) {
                    json = new JsonSlurper().parseText(bodyString)
                }
            }

            def httpResponse = new HttpResponse(
                    response.code(),
                    response.headers().toMultimap(),
                    bodyString,
                    json,
                    buildRequestInfo(response)
            )
            interceptors.each { it.onResponse?.call(httpResponse) }
            return httpResponse
        }
    }

    private static HttpResponse.RequestInfo buildRequestInfo(Response response) {
        def req = response.request()
        def url = req.url()

        // Inline query parameter extraction
        def params = [:]
        url.queryParameterNames().each { name ->
            params[name] = url.queryParameter(name)
        }

        // Inline headers conversion to lowercase keys
        def headersMap = [:]
        req.headers().names().each { name ->
            headersMap[name.toLowerCase()] = req.headers().values(name)
        }

        return new HttpResponse.RequestInfo(params as Map<String, String>, headersMap as Map<String, List<String>>)
    }
}
