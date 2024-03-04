package demo.controller

import demo.exception.ItemNotFoundException
import demo.service.ItemService
import demo.util.TestRestData
import io.micronaut.http.HttpStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class ItemControllerTest {

    private lateinit var serviceMock: ItemService
    private lateinit var controller: ItemController

    @BeforeEach
    fun setUp() {
        serviceMock = mockk()
        controller = ItemController(serviceMock)
        clearMocks(serviceMock)
    }

    @Test
    fun testCreateItem_Success() {
        val itemId = UUID.randomUUID()
        val request = TestRestData.buildCreateItemRequest(RandomStringUtils.randomAlphabetic(8))
        every { serviceMock.createItem(request) } returns itemId
        val response = controller.createItem(request)
        assertEquals(HttpStatus.CREATED, response.status)
        assertEquals(itemId.toString(), response.header("Location"))
        verify(exactly = 1) { serviceMock.createItem(request) }
    }

    @Test
    fun testCreateItem_ServiceThrowsException() {
        val request = TestRestData.buildCreateItemRequest(RandomStringUtils.randomAlphabetic(8))
        every { serviceMock.createItem(request) } throws RuntimeException("Service failure")
        val response = controller.createItem(request)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.status)
        verify(exactly = 1) { serviceMock.createItem(request) }
    }

    @Test
    fun testGetItem_Success() {
        val itemId = UUID.randomUUID()
        val getItemResponse = TestRestData.buildGetItemResponse(itemId, "test-item")
        every { serviceMock.getItem(itemId) } returns getItemResponse
        val response = controller.getItem(itemId)
        assertEquals(HttpStatus.OK, response.status)
        assertEquals(itemId, response.body().id)
        assertEquals("test-item", response.body().name)
        verify(exactly = 1) { serviceMock.getItem(itemId) }
    }

    @Test
    fun testGetItem_NotFound() {
        val itemId = UUID.randomUUID()
        every { serviceMock.getItem(itemId) } throws ItemNotFoundException()
        val response = controller.getItem(itemId)
        assertEquals(HttpStatus.NOT_FOUND, response.status)
        verify(exactly = 1) { serviceMock.getItem(itemId) }
    }

    @Test
    fun testUpdateItem_Success() {
        val itemId = UUID.randomUUID()
        val request = TestRestData.buildUpdateItemRequest(RandomStringUtils.randomAlphabetic(8))
        justRun { serviceMock.updateItem(itemId, request) }
        val response = controller.updateItem(itemId, request)
        assertEquals(HttpStatus.NO_CONTENT, response.status)
        verify(exactly = 1) { serviceMock.updateItem(itemId, request) }
    }

    @Test
    fun testUpdateItem_NotFound() {
        val itemId = UUID.randomUUID()
        val request = TestRestData.buildUpdateItemRequest(RandomStringUtils.randomAlphabetic(8))
        every { serviceMock.updateItem(itemId, request) } throws ItemNotFoundException()
        val response = controller.updateItem(itemId, request)
        assertEquals(HttpStatus.NOT_FOUND, response.status)
        verify(exactly = 1) { serviceMock.updateItem(itemId, request) }
    }

    @Test
    fun testUpdateItem_ServiceThrowsException() {
        val itemId = UUID.randomUUID()
        val request = TestRestData.buildUpdateItemRequest(RandomStringUtils.randomAlphabetic(8))
        every { serviceMock.updateItem(itemId, request) } throws RuntimeException("Service failure")
        val response = controller.updateItem(itemId, request)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.status)
        verify(exactly = 1) { serviceMock.updateItem(itemId, request) }
    }

    @Test
    fun testDeleteItem_Success() {
        val itemId = UUID.randomUUID()
        justRun { serviceMock.deleteItem(itemId) }
        val response = controller.deleteItem(itemId)
        assertEquals(HttpStatus.NO_CONTENT, response.status)
        verify(exactly = 1) { serviceMock.deleteItem(itemId) }
    }

    @Test
    fun testDeleteItem_NotFound() {
        val itemId = UUID.randomUUID()
        every { serviceMock.deleteItem(itemId) } throws ItemNotFoundException()
        val response = controller.deleteItem(itemId)
        assertEquals(HttpStatus.NOT_FOUND, response.status)
        verify(exactly = 1) { serviceMock.deleteItem(itemId) }
    }
}
