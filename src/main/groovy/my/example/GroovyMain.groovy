package my.example

import groovy.json.JsonOutput
import my.example.http.FileCookieJar
import my.example.http.HttpInterceptor
import my.example.http.sample.JsonPlaceholderClient

// ---- Prepare Cookie Jar instance ----
def cookieFile = new File("cookies.json")
def cookieJar = new FileCookieJar(cookieFile)

// ---- Interceptors shared across client instances ----
def interceptors = [
        new HttpInterceptor(
                onRequest: { method, url, builder -> 
                    println "[>>] ${method} ${url}"
                    // Example: Add custom headers to every request
                    builder.addHeader("X-Custom-Header", "groovy-client")
                    builder.addHeader("User-Agent", "GroovyHttpClient/1.0")
                },
                onResponse: { res -> println "[<<] ${res.statusCode} (success=${res.success})" }
        )
]

// ---- Example 1: Using a standalone client instance ----
def client = new JsonPlaceholderClient(cookieJar, interceptors)

// ---- Example 2: Sharing cookies and interceptors across different client instances ----
// def cookieJar = new InMemoryCookieJar()
// def clientA = new JsonPlaceholderClient(cookieJar, interceptors)
// def clientB = new JsonPlaceholderClient(cookieJar, interceptors)
// clientA and clientB share the same cookie store and interceptors

// GET: fetch a single TODO
println "=== GET /todos/1 ==="
def todoResponse = client.getTodo(1)
println "Status: ${todoResponse.statusCode}"
println "Body:   ${JsonOutput.prettyPrint(JsonOutput.toJson(todoResponse.json))}"

// GET: fetch a single post
println "\n=== GET /posts/1 ==="
def postResponse = client.getPost(1)
println "Status: ${postResponse.statusCode}"
println "Body:   ${JsonOutput.prettyPrint(JsonOutput.toJson(postResponse.json))}"

// POST: create a new post from a Map
println "\n=== POST /posts (from Map) ==="
def createResponse = client.createPost([
        title : "Groovy HTTP Client test",
        body  : "A sample HTTP client implementation using OkHttp.",
        userId: 1
])
println "Status: ${createResponse.statusCode}"
println "Body:   ${JsonOutput.prettyPrint(JsonOutput.toJson(createResponse.json))}"

// POST: create a new post from a raw JSON string
println "\n=== POST /posts (from String) ==="
def jsonString = '{"title":"Groovy HTTP Client test","body":"Sent as a raw JSON string.","userId":1}'
def createFromStringResponse = client.createPost(jsonString)
println "Status: ${createFromStringResponse.statusCode}"
println "Body:   ${JsonOutput.prettyPrint(JsonOutput.toJson(createFromStringResponse.json))}"

// POST: create a new post from a JSON file
println "\n=== POST /posts (from File) ==="
def jsonFile = new File("src/main/resources/sample/post.json")
def createFromFileResponse = client.createPost(jsonFile)
println "Status: ${createFromFileResponse.statusCode}"
println "Body:   ${JsonOutput.prettyPrint(JsonOutput.toJson(createFromFileResponse.json))}"

// PUT: update a post
println "\n=== PUT /posts/1 ==="
def updateResponse = client.updatePost(1, [
        id    : 1,
        title : "Updated title",
        body  : "Updated body",
        userId: 1
])
println "Status: ${updateResponse.statusCode}"
println "Body:   ${JsonOutput.prettyPrint(JsonOutput.toJson(updateResponse.json))}"

// DELETE: delete a post
println "\n=== DELETE /posts/1 ==="
def deleteResponse = client.deletePost(1)
println "Status: ${deleteResponse.statusCode}"
println "Body:   ${deleteResponse.body}"
