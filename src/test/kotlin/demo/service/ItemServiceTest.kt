package demo.service

import demo.exception.ItemNotFoundException
import demo.util.TestRestData
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID
import kotlin.test.assertEquals

class ItemServiceTest {

    private lateinit var service: ItemService

    @BeforeEach
    fun setUp() {
        service = ItemService()
    }

    @Test
    fun testCreateItem() {
        val request = TestRestData.buildCreateItemRequest(randomAlphabetic(8))
        val newItemId = service.createItem(request)

        val itemResponse = service.getItem(newItemId)
        assertEquals(newItemId, itemResponse.id)
        assertEquals(request.name, itemResponse.name)
    }

    @Test
    fun testUpdateItem() {
        val createRequest = TestRestData.buildCreateItemRequest(randomAlphabetic(8))
        val newItemId = service.createItem(createRequest)

        val updateRequest = TestRestData.buildUpdateItemRequest(randomAlphabetic(8))
        service.updateItem(newItemId, updateRequest)

        val itemResponse = service.getItem(newItemId)
        assertEquals(newItemId, itemResponse.id)
        assertEquals(updateRequest.name, itemResponse.name)
    }

    @Test
    fun testUpdateItem_NotFound() {
        val updateRequest = TestRestData.buildUpdateItemRequest(randomAlphabetic(8))

        assertThrows(ItemNotFoundException::class.java) { service.updateItem(randomUUID(), updateRequest) }
    }

    @Test
    fun testGetItem_NotFound() {
        assertThrows(ItemNotFoundException::class.java) { service.getItem(randomUUID()) }
    }

    @Test
    fun testDeleteItem() {
        val request = TestRestData.buildCreateItemRequest(randomAlphabetic(8))
        val newItemId = service.createItem(request)

        service.deleteItem(newItemId)

        assertThrows(ItemNotFoundException::class.java) { service.getItem(newItemId) }
    }

    @Test
    fun testDeleteItem_NotFound() {
        assertThrows(ItemNotFoundException::class.java) { service.deleteItem(randomUUID()) }
    }
}
