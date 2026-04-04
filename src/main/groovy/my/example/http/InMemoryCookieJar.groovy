package my.example.http

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * An in-memory {@link CookieJar} implementation that stores cookies per host.
 * To share cookies across different client instances, create a single instance of this class
 * and pass it to each client constructor.
 *
 * <pre>
 *   def cookieJar = new InMemoryCookieJar()
 *   def clientA = new ServiceAClient(cookieJar)
 *   def clientB = new ServiceBClient(cookieJar)
 *   // clientA and clientB share the same cookies
 * </pre>
 */
class InMemoryCookieJar implements CookieJar {

    private final Map<String, List<Cookie>> store = [:]

    @Override
    void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        store[url.host()] = cookies
    }

    @Override
    List<Cookie> loadForRequest(HttpUrl url) {
        return store[url.host()] ?: []
    }
}
