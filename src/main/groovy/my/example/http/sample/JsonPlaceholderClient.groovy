package my.example.http.sample

import my.example.http.BaseHttpClient
import my.example.http.HttpInterceptor
import my.example.http.HttpResponse
import okhttp3.CookieJar

/**
 * HTTP client for JSONPlaceholder (https://jsonplaceholder.typicode.com).
 * Extends {@link BaseHttpClient} and provides methods for each endpoint.
 *
 * This class serves as a sample implementation showing how to create a service-specific client.
 */
class JsonPlaceholderClient extends BaseHttpClient {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com"

    JsonPlaceholderClient() {
        super(BASE_URL)
    }

    /** Use this constructor to share cookies with other client instances. */
    JsonPlaceholderClient(CookieJar cookieJar) {
        super(BASE_URL, cookieJar)
    }

    /** Use this constructor to register shared interceptors. */
    JsonPlaceholderClient(List<HttpInterceptor> interceptors) {
        super(BASE_URL, interceptors)
    }

    /** Use this constructor to share both cookies and interceptors across instances. */
    JsonPlaceholderClient(CookieJar cookieJar, List<HttpInterceptor> interceptors) {
        super(BASE_URL, cookieJar, interceptors)
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
     * @param id   Post ID
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
}
