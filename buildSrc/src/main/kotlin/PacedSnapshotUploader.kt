/*
 *    Copyright 2026 Ugljesa Jovanovic
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.Base64

/**
 * Uploads a maven repository that was published to a local directory, one file at a time with a
 * pause between requests.
 *
 */
object PacedSnapshotUploader {

    private const val METADATA = "maven-metadata.xml"

    /**
     * @param intervalMillis minimum time between the start of two requests.
     * @param maxRateLimitPauses how many 429 answers are tolerated in the whole run. Each one pauses
     * the upload (Retry-After if the server sent it, at least [rateLimitPauseMillis], doubled on every
     * further 429) and doubles the interval for the rest of the run. One more 429 fails the upload.
     */
    fun upload(
        stagingDirectory: File,
        repositoryUrl: String,
        username: String,
        password: String,
        intervalMillis: Long = 1000,
        maxRateLimitPauses: Int = 3,
        rateLimitPauseMillis: Long = 60_000,
        skipSignatures: Boolean = false,
        log: (String) -> Unit = ::println
    ) {
        val files = filesInUploadOrder(stagingDirectory, skipSignatures)
        require(files.isNotEmpty()) { "Nothing to upload, $stagingDirectory has no files" }
        require(username.isNotEmpty() && password.isNotEmpty()) { "Sonatype credentials are not set" }

        val base = if (repositoryUrl.endsWith("/")) repositoryUrl else "$repositoryUrl/"
        val authorization = "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())
        val client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(30))
            .build()

        var interval = intervalMillis
        var pause = rateLimitPauseMillis
        var pausesLeft = maxRateLimitPauses
        var lastRequestStart = 0L

        log("Uploading ${files.size} files to $base, one request every $interval ms")
        files.forEachIndexed { index, file ->
            val path = file.relativeTo(stagingDirectory).invariantSeparatorsPath
            val request = HttpRequest.newBuilder(URI.create(base + path))
                .timeout(Duration.ofMinutes(10))
                .header("Authorization", authorization)
                .PUT(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                .build()

            while (true) {
                val wait = lastRequestStart + interval - System.currentTimeMillis()
                if (wait > 0) Thread.sleep(wait)
                lastRequestStart = System.currentTimeMillis()

                val response = client.send(request, HttpResponse.BodyHandlers.ofString())
                val status = response.statusCode()
                if (status in 200..299) {
                    log("[${index + 1}/${files.size}] $path")
                    break
                }
                if (status != 429) {
                    throw RuntimeException("Upload of $path failed with status $status: ${response.body().take(500)}")
                }
                if (pausesLeft == 0) {
                    throw RuntimeException(
                        "Upload of $path was rate limited (429) again after $maxRateLimitPauses pauses, giving up. " +
                                "Wait before running it again, repeated attempts make the block longer."
                    )
                }
                pausesLeft--
                val retryAfterMillis = response.headers().firstValue("Retry-After").orElse("")
                    .toLongOrNull()?.times(1000) ?: 0
                val sleep = maxOf(pause, retryAfterMillis)
                pause *= 2
                interval *= 2
                log("Rate limited (429) on $path, pausing ${sleep / 1000} s, then one request every $interval ms")
                Thread.sleep(sleep)
            }
        }
        log("Uploaded ${files.size} files")
    }
    
    fun filesInUploadOrder(stagingDirectory: File, skipSignatures: Boolean = false): List<File> {
        val files = stagingDirectory.walkTopDown()
            .filter { it.isFile && !isSkipped(it.name, skipSignatures) }
            .sortedBy { it.invariantSeparatorsPath }
            .toList()
        val (metadata, artifacts) = files.partition { it.name.startsWith(METADATA) }
        val (versionMetadata, moduleMetadata) = metadata.partition { it.parentFile.name.endsWith("-SNAPSHOT") }
        return artifacts + versionMetadata + moduleMetadata
    }

    private val checksumExtensions = listOf(".md5", ".sha1", ".sha256", ".sha512")

    fun isSkipped(fileName: String, skipSignatures: Boolean): Boolean {
        if (fileName.endsWith(".md5") || fileName.endsWith(".sha512") || fileName.endsWith(".sha1")) return true
        if (checksumExtensions.any { fileName.endsWith(".asc$it") }) return true
        return skipSignatures && fileName.endsWith(".asc")
    }
}
