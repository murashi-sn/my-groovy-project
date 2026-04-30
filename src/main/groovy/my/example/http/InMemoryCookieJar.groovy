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

    private final List<Cookie> store = []

    @Override
    void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookies.each { newCookie ->
            store.removeAll { isSameCookie(it, newCookie) }
        }
        store.addAll(cookies)
    }

    @Override
    List<Cookie> loadForRequest(HttpUrl url) {
        store.findAll { it.matches(url) }
    }

    private static boolean isSameCookie(Cookie cookie1, Cookie cookie2) {
        cookie1.name() == cookie2.name() &&
        cookie1.domain() == cookie2.domain() &&
        cookie1.path() == cookie2.path()
    }
}
