package my.example

import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody

@Controller
class TestController {

    @GetMapping('/test')
    String test(Model model) {
        model.message = 'Hello, Groovy with Thymeleaf!'
        model.timestamp = new Date()
        'test'
    }

    @GetMapping('/download')
    ResponseEntity<StreamingResponseBody> download(HttpServletResponse httpServletResponse) {
        printlnWithThreadName("download called.")
        RestTemplate restTemplate = new RestTemplate()
        ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"big_size.csv\"")
                .header(HttpHeaders.CONTENT_ENCODING, "gzip")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body({ outputStream ->
                    printlnWithThreadName("streaming start sending to client.")
                    try {
                        restTemplate.execute("http://localhost:8080/download2", HttpMethod.GET, null, { ClientHttpResponse response ->
                            printlnWithThreadName("download start.")
                            try (def inputStream = response.body) {
                                byte[] buffer = new byte[8192]
                                int bytesRead
                                long total = 0
                                long loopCount = 0
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    total += bytesRead
                                    if (loopCount++ % 1_000_00 == 0) {
                                        printlnWithThreadName("download progress... total: ${total} bytes")
                                    }
                                    if (httpServletResponse.committed) {
                                        // A committed response has already had its status code and headers written.
                                        if (loopCount > 3_000_00) {
                                            // error occurred when sending http response body.
//                                             throw new RuntimeException("unexpected error")
                                        }
                                    }
                                    outputStream.write(buffer, 0, bytesRead)
                                    // Flush periodically to keep the connection alive
                                    if (loopCount % 100 == 0) {
                                        outputStream.flush()
                                    }
                                }
                                printlnWithThreadName("download finish. Total bytes: ${total}")
                            } catch (Exception exInnerTry) {
                                printlnWithThreadName("error occurred. [buffer copy]")
                                throw exInnerTry
                            }
                            return null
                        } as ResponseExtractor<Void>)
                        printlnWithThreadName("streaming finish sending to client.")
                    } catch (Exception exOuterTry) {
                        printlnWithThreadName("error occurred. [restTemplate.execute]")
                        if (!httpServletResponse.committed) {
                            httpServletResponse.sendError(500)
                        }
                        throw exOuterTry
                    }
                } as StreamingResponseBody)
    }

    @GetMapping('/download2')
    ResponseEntity<StreamingResponseBody> download2() {
        printlnWithThreadName("download2 called, this is loopback call")
        def resource = new ClassPathResource("big_size.csv.gzip")
        ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"big_size.csv\"")
                .header(HttpHeaders.CONTENT_ENCODING, "gzip")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body({ outputStream ->
                    printlnWithThreadName("streaming start sending to myself")
                    long loopCount = 0
                    try (def inputStream = resource.getInputStream()) {
                        byte[] buffer = new byte[8192]
                        int bytesRead
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            // Flush periodically to keep the connection alive
                            if (loopCount++ % 100 == 0) {
                                outputStream.flush()
                            }
                        }
                    } catch (Exception ex) {
                        printlnWithThreadName("error occurred. [download2]")
                        throw ex
                    }
                } as StreamingResponseBody)
    }

    private static void printlnWithThreadName(String message) {
        System.out.println("[Thread: ${Thread.currentThread().name}] ${message}")
    }
}
