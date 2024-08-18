package my.example

import okhttp3.OkHttpClient
import okhttp3.Request

def config = new ConfigSlurper().parse(new File('Config.groovy').text)

def client = new OkHttpClient()
def request = new Request.Builder()
        .url("https://example.com")
        .build()

def writer = new PrintWriter(new File("./output.txt"), "UTF-8")

try (def response = client.newCall(request).execute()) {
    println response.body().string()
    println response.headers().forEach {
        writer.println(it)
    }
}

writer.close()