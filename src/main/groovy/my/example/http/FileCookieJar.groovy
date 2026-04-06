package my.example.http

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * A file-backed {@link CookieJar} implementation that persists cookies to a local JSON file.
 * Cookies survive application restarts. Usage is identical to {@link InMemoryCookieJar}.
 *
 * <pre>
 *   def cookieJar = new FileCookieJar(new File("cookies.json"))
 *   def clientA = new ServiceAClient(cookieJar)
 *   def clientB = new ServiceBClient(cookieJar)
 *   // clientA and clientB share cookies, persisted across restarts
 * </pre>
 */
class FileCookieJar implements CookieJar {

    private final File cookieFile
    private final Map<String, List<Cookie>> store = [:]

    /**
     * @param cookieFile File to persist cookies in (JSON format).
     *                   Created automatically on first save. Parent directories are created if needed.
     */
    FileCookieJar(File cookieFile) {
        this.cookieFile = cookieFile
        cookieFile.parentFile?.mkdirs()
        loadFromFile()
    }

    @Override
    void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        store[url.host()] = cookies
        flush()
    }

    @Override
    List<Cookie> loadForRequest(HttpUrl url) {
        store[url.host()] ?: []
    }

    /** Removes all cookies from the store and overwrites the file with an empty object. */
    void clear() {
        store.clear()
        cookieFile.text = '{}'
    }

    /** Forces the current store to be written to the file. */
    void flush() {
        cookieFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(
            store.collectEntries { host, cookies -> [host, cookies.collect(serialize)] }
        ))
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void loadFromFile() {
        if (!cookieFile.exists()) return
        try {
            new JsonSlurper().parse(cookieFile).each { host, list ->
                store[host] = list.collect(deserialize).findAll()
            }
        } catch (e) {
            System.err.println "[FileCookieJar] Failed to parse cookie file, starting with empty store: ${e.message}"
            store.clear()
        }
    }

    private final serialize = { Cookie c ->
        [
            name      : c.name(),
            value     : c.value(),
            domain    : c.domain(),
            path      : c.path(),
            expiresAt : c.expiresAt(),
            secure    : c.secure(),
            httpOnly  : c.httpOnly(),
            hostOnly  : c.hostOnly(),
            persistent: c.persistent()
        ]
    }

    private final deserialize = { map ->
        try {
            long expiresAt = map.expiresAt ?: Long.MAX_VALUE
            if (map.persistent && expiresAt < System.currentTimeMillis()) return null

            def builder = new Cookie.Builder()
                .name(map.name)
                .value(map.value)
                .path(map.path)

            map.hostOnly
                ? builder.hostOnlyDomain(map.domain)
                : builder.domain(map.domain)

            if (map.secure)   builder.secure()
            if (map.httpOnly) builder.httpOnly()
            if (map.persistent && expiresAt > System.currentTimeMillis()) builder.expiresAt(expiresAt)

            builder.build()
        } catch (e) {
            System.err.println "[FileCookieJar] Skipping malformed cookie entry: ${e.message}"
            null
        }
    }
}
