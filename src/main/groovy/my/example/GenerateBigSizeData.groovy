package my.example

// Script to generate a large CSV file approximately 3GB in size, saved as gzip compressed file
// if you want to unzip.
// $ gunzip -c src/main/resources/big_size.csv.gzip > big_size.csv

import java.util.zip.GZIPOutputStream

// Generate a random string of given length
def generateRandomString = { int length, Random random ->
    def chars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + [' ', '.', ',', '!']
    (1..length).collect { chars[random.nextInt(chars.size())] }.join()
}

// Main script execution
def random = new Random()
def file = new File("../../../resources/big_size.csv.gzip")
new GZIPOutputStream(new FileOutputStream(file)).withWriter("UTF-8") { writer ->
    writer.println("id,name,email,description")
    def numRows = 15_000_000  // Increased to generate ~1GB uncompressed data
    (1..numRows).each { i ->
        def id = i
        def name = "Name${i}" + generateRandomString(20, random)
        def email = "user${i}@example.com"
        def description = generateRandomString(300, random)  // Increased from 100 to 300
        writer.println("${id},${name},${email},${description}")
        if (i % 100000 == 0) {
            println "Processed ${i} rows"
        }
    }
}
println "Gzip compressed CSV file created: ${file.absolutePath}, size: ${file.size()} bytes"
