package demo.integration

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@MicronautTest
class HealthIntegrationTest {

    private lateinit var server: EmbeddedServer
    private lateinit var client: HttpClient

    @BeforeEach
    fun setupServer() {
        server = ApplicationContext.run(EmbeddedServer::class.java)
        client = HttpClient.create(server.url)
    }

    @AfterEach
    fun stopServer() {
        client.close()
        server.close()
    }

    @Test
    fun testHealthEndpoint() {
        val status = client.toBlocking().retrieve(
            HttpRequest.GET<Any>("/health"),
            HttpStatus::class.java
        )
        MatcherAssert.assertThat(status, Matchers.equalTo(HttpStatus.OK))
    }
}
