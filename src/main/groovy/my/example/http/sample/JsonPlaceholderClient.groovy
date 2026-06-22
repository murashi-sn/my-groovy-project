package my.example.http.sample

import my.example.http.BaseHttpClient
import my.example.http.HttpInterceptor
import my.example.http.HttpResponse
import okhttp3.CookieJar

/**
 * HTTP client for JSONPlaceholder (https://jsonplaceholder.typicode.com).
 * Extends {@link BaseHttpClient} and provides methods for each endpoint.
 *
 * This class serves as a sample implementation showing how to create a service-specific client
 * and how to use context for dynamic request handling.
 *
 * <h3>Using Context for Request Metadata</h3>
 * <p>Requests can pass a context map to control behavior in interceptors.
 * For example, enable verbose logging for specific requests:</p>
 * <pre>
 *   def client = new JsonPlaceholderClient()
 *   // Normal request (default logging)
 *   client.getPost(1)
 *   // Verbose request (detailed logging)
 *   client.getPostVerbose(1)
 * </pre>
 */
class JsonPlaceholderClient extends BaseHttpClient {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com"

    JsonPlaceholderClient(CookieJar cookieJar) {
        super(BASE_URL, cookieJar)
        // Add interceptor after instance initialization
        addInterceptor(new HttpInterceptor(
                onRequest: { method, url, builder, context ->
                    // Instance members can be referenced here
                    if (context?.verbose) {
                        println "[${this.class.simpleName}] ${method} ${url} (context: ${context})"
                    } else {
                        println "[${this.class.simpleName}] ${method} ${url}"
                    }
                },
                onResponse: { res -> println "[<<] ${res.statusCode} (success=${res.success})" }
        ))
        addInterceptor(new HttpInterceptor(
                onRequest: { method, url, builder, context ->
                    // Example: Add custom headers to every request
                    builder.addHeader("X-Custom-Header", "groovy-client")
                    builder.addHeader("User-Agent", "GroovyHttpClient/1.0")
                }
        ))
    }


    /**
     * Retrieves all TODO items.
     * @return {@link HttpResponse} whose json field contains a List of TODOs
     */
    HttpResponse getTodos() {
        return get("/todos")
    }

    /**
     * Retrieves a TODO item by ID.
     * @param id TODO ID
     * @return {@link HttpResponse}
     */
    HttpResponse getTodo(int id) {
        return get("/todos/${id}")
    }

    /**
     * Retrieves all posts.
     * @return {@link HttpResponse}
     */
    HttpResponse getPosts() {
        return get("/posts")
    }

    /**
     * Retrieves a post by ID.
     * @param id Post ID
     * @return {@link HttpResponse}
     */
    HttpResponse getPost(int id) {
        return get("/posts/${id}")
    }

    /**
     * Creates a new post.
     * @param data Post data (e.g. [title: "...", body: "...", userId: 1])
     * @return {@link HttpResponse} whose json field contains the created resource
     */
    HttpResponse createPost(Map data) {
        return post("/posts", data)
    }

    /**
     * Creates a new post from a raw JSON string.
     * @param jsonString Raw JSON string containing the post data
     * @return {@link HttpResponse} whose json field contains the created resource
     */
    HttpResponse createPost(String jsonString) {
        return post("/posts", jsonString)
    }

    /**
     * Creates a new post from a JSON file.
     * @param jsonFile JSON file containing the post data
     * @return {@link HttpResponse} whose json field contains the created resource
     */
    HttpResponse createPost(File jsonFile) {
        return post("/posts", jsonFile)
    }

    /**
     * Updates a post by ID.
     * @param id Post ID
     * @param data Updated data
     * @return {@link HttpResponse}
     */
    HttpResponse updatePost(int id, Map data) {
        return put("/posts/${id}", data)
    }

    /**
     * Deletes a post by ID.
     * @param id Post ID
     * @return {@link HttpResponse}
     */
    HttpResponse deletePost(int id) {
        return delete("/posts/${id}")
    }

    /**
     * Retrieves a post by ID with verbose logging enabled.
     * Demonstrates how to pass context to a request for interceptor handling.
     *
     * @param id Post ID
     * @return {@link HttpResponse}
     *
     * <pre>
     *   def client = new JsonPlaceholderClient()
     *   // Standard logging
     *   client.getPost(1)
     *   // Verbose logging with context information
     *   client.getPostVerbose(1)
     * </pre>
     */
    HttpResponse getPostVerbose(int id) {
        return get("/posts/${id}", [:], [:], [verbose: true])
    }
}
