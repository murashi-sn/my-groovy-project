package my.example.http
/**
 * Global configuration for HTTP clients.
 *
 * Provides default settings for all {@link BaseHttpClient} instances.
 * Settings can be overridden per-instance at construction time.
 *
 * <h3>Usage</h3>
 * <pre>
 *   // Set global defaults
 *   HttpClientConfig.setDefaults([
 *       connectTimeout: 15000,
 *       readTimeout: 60000,
 *       writeTimeout: 60000
 *   ])
 *
 *   // Create client with global defaults
 *   new MyClient("https://api.example.com")
 *
 *   // Override for a specific instance
 *   new MyClient("https://slow-api.example.com", [readTimeout: 120000])
 * </pre>
 */
final class HttpClientConfig {

    /**
     * Global default settings.
     * Common keys: connectTimeout, readTimeout, writeTimeout (all in milliseconds)
     */
    static Map<String, Object> defaults = [
            connectTimeout: 10000,
            readTimeout   : 30000,
            writeTimeout  : 30000
    ]

    /**
     * Updates global default settings.
     * @param config Map of settings to override
     */
    static void setDefaults(Map<String, Object> config) {
        defaults.putAll(config)
    }

    /**
     * Resets defaults to original values.
     */
    static void resetDefaults() {
        defaults = [
                connectTimeout: 10000,
                readTimeout   : 30000,
                writeTimeout  : 30000
        ]
    }
}
