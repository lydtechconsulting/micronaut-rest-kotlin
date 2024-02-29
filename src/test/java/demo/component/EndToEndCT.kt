package demo.component

import demo.rest.api.CreateItemRequest
import demo.rest.api.UpdateItemRequest
import demo.util.TestRestData
import dev.lydtech.component.framework.client.service.ServiceClient
import dev.lydtech.component.framework.extension.ComponentTestExtension
import io.micronaut.http.HttpStatus
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory

@Slf4j
@ExtendWith(ComponentTestExtension::class)
class EndToEndCT {

    @BeforeEach
    fun setup() {
        val serviceBaseUrl: String = ServiceClient.getInstance().getBaseUrl()
        RestAssured.baseURI = serviceBaseUrl
    }

    /**
     * A REST request is POSTed to the v1/item endpoint in order to create a new Item entity.
     *
     * The item is then updated to change the name.
     *
     * The item is then deleted.
     */
    @Test
    fun testItemCRUD() {

        // Test the POST endpoint to create an item.
        val createRequest = TestRestData.buildCreateItemRequest(
            RandomStringUtils.randomAlphabetic(8).lowercase() + "1"
        )
        val createItemResponse: Response = sendCreateItemRequest(createRequest)
        val itemId: String = createItemResponse.header("Location")
        MatcherAssert.assertThat(itemId, Matchers.notNullValue())
        log.info("Create item response location header: $itemId")

        // Test the GET endpoint to fetch the item.
        sendGetItemRequest(itemId, createRequest.name)

        // Test the PUT endpoint to update the item name.
        val updateRequest = TestRestData.buildUpdateItemRequest(
            RandomStringUtils.randomAlphabetic(1).lowercase() + "2"
        )
        sendUpdateRequest(itemId, updateRequest)

        // Ensure the name was updated.
        sendGetItemRequest(itemId, updateRequest.name)

        // Test the DELETE endpoint to delete the item.
        sendDeleteRequest(itemId)

        // Ensure the deleted item cannot be found.
        sendGetItemRequest(itemId, HttpStatus.NOT_FOUND)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        private fun sendCreateItemRequest(createRequest: CreateItemRequest): Response {
            return Given {
                header("Content-type", "application/json")
                body(createRequest)
            } When {
                post("/v1/items")
            } Then {
                statusCode(HttpStatus.CREATED.code)
            } Extract {
                response()
            }
        }

        private fun sendUpdateRequest(location: String, updateRequest: UpdateItemRequest) {
            return Given {
                pathParam("id", location)
                header("Content-type", "application/json")
                body(updateRequest)
            } When {
                put("/v1/items/{id}")
            } Then {
                statusCode(HttpStatus.NO_CONTENT.code)
            } Extract {
                response()
            }
        }

        private fun sendDeleteRequest(location: String) {
            Given {
                pathParam("id", location)
            } When {
                delete("/v1/items/{id}")
            } Then {
                statusCode(HttpStatus.NO_CONTENT.code)
            }
        }

        private fun sendGetItemRequest(location: String, expectedName: String) {
            Given {
                pathParam("id", location)
            } When {
                get("/v1/items/{id}")
            } Then {
                statusCode(HttpStatus.OK.code)
                body("name", Matchers.containsString(expectedName))
            }
        }

        private fun sendGetItemRequest(location: String, expectedHttpStatus: HttpStatus) {
            Given {
                pathParam("id", location)
            } When {
                get("/v1/items/{id}")
            } Then {
                statusCode(expectedHttpStatus.code)
            }
        }
    }
}
