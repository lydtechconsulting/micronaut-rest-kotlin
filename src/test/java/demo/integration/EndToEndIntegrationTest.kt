package demo.integration

import demo.rest.api.GetItemResponse
import demo.util.TestRestData
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@MicronautTest
class EndToEndIntegrationTest {

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
    fun testItemCRUD() {
        // Create the item.
        val createItemRequest = TestRestData.buildCreateItemRequest(RandomStringUtils.randomAlphabetic(8).lowercase())
        val createItemHttpRequest: HttpRequest<*> = HttpRequest.POST("/v1/items", createItemRequest).accept(MediaType.APPLICATION_JSON)
        val createItemResponse = client.toBlocking().exchange(createItemHttpRequest, Void::class.java)
        MatcherAssert.assertThat(createItemResponse.status(), Matchers.equalTo(HttpStatus.CREATED))
        MatcherAssert.assertThat(createItemResponse.header("Location"), Matchers.notNullValue())
        val itemId = createItemResponse.header("Location").toString()
        MatcherAssert.assertThat(itemId, Matchers.notNullValue())

        // Retrieve the new item.
        val getItemHttpRequest: HttpRequest<*> = HttpRequest.GET<Any>("/v1/items/$itemId").accept(MediaType.APPLICATION_JSON)
        val getItemResponse = client.toBlocking().exchange(getItemHttpRequest, GetItemResponse::class.java)
        MatcherAssert.assertThat(getItemResponse.status(), Matchers.equalTo(HttpStatus.OK))
        MatcherAssert.assertThat(getItemResponse.body().name, Matchers.equalTo(createItemRequest.name))
        MatcherAssert.assertThat(getItemResponse.body().id.toString(), Matchers.equalTo(itemId))

        // Update the item.
        val updateItemRequest = TestRestData.buildUpdateItemRequest(RandomStringUtils.randomAlphabetic(8).lowercase())
        val updateItemHttpRequest: HttpRequest<*> = HttpRequest.PUT("/v1/items/$itemId", updateItemRequest).accept(MediaType.APPLICATION_JSON)
        val updateItemResponse = client.toBlocking().exchange(updateItemHttpRequest, Void::class.java)
        MatcherAssert.assertThat(updateItemResponse.status(), Matchers.equalTo(HttpStatus.NO_CONTENT))

        // Retrieve the updated item.
        val getItemResponseUpdated = client.toBlocking().exchange(getItemHttpRequest, GetItemResponse::class.java)
        MatcherAssert.assertThat(getItemResponseUpdated.status(), Matchers.equalTo(HttpStatus.OK))
        MatcherAssert.assertThat(getItemResponseUpdated.body().name, Matchers.equalTo(getItemResponseUpdated.body().name))

        // Delete the item
        val deleteItemHttpRequest: HttpRequest<*> = HttpRequest.DELETE<Any>("/v1/items/$itemId").accept(MediaType.APPLICATION_JSON)
        val deleteItemResponse = client.toBlocking().exchange(deleteItemHttpRequest, Void::class.java)
        MatcherAssert.assertThat(deleteItemResponse.status(), Matchers.equalTo(HttpStatus.NO_CONTENT))

        // Retrieve the deleted item - should be NOT FOUND.
        try {
            val getItemResponseDeleted = client.toBlocking().exchange(getItemHttpRequest, GetItemResponse::class.java)
            Assertions.fail<Any>("Expected item to be deleted, but found item with Id: " + getItemResponseDeleted.body().id)
        } catch (e: HttpClientResponseException) {
            MatcherAssert.assertThat(e.status, Matchers.equalTo(HttpStatus.NOT_FOUND))
        }
    }
}
