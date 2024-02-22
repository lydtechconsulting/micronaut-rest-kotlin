package demo.service

import demo.domain.Item
import demo.exception.ItemNotFoundException
import demo.rest.api.CreateItemRequest
import demo.rest.api.GetItemResponse
import demo.rest.api.UpdateItemRequest
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.collections.HashMap

@Singleton
class ItemService {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
    private val itemStore = HashMap<UUID, Item>()

    fun createItem(request: CreateItemRequest): UUID {
        val item = Item(UUID.randomUUID(), request.name)
        itemStore.put(item.id, item)
        log.info("Item created with id: {}", item.id)
        return item.id
    }

    fun getItem(itemId: UUID): GetItemResponse {
        val item = itemStore[itemId]
        return if (item != null) {
            log.info("Found item with id: {}", item.id)
            GetItemResponse(item.id, item.name)
        } else {
            log.warn("Item with id: {} not found.", itemId)
            throw ItemNotFoundException()
        }
    }

    fun updateItem(itemId: UUID, request: UpdateItemRequest) {
        val item = itemStore[itemId]
        if (item != null) {
            log.info("Found item with id: $itemId")
            val updatedItem = Item(item.id, request.name)
            itemStore.replace(item.id, updatedItem)
            log.info("Item updated with id: {} - name: {}", itemId, request.name)
        } else {
            log.error("Item with id: {} not found.", itemId)
            throw ItemNotFoundException()
        }
    }

    fun deleteItem(itemId: UUID) {
        val item = itemStore[itemId]
        if (item != null) {
            itemStore.remove(item.id)
            log.info("Deleted item with id: {}", item.id)
        } else {
            log.error("Item with id: {} not found.", itemId)
            throw ItemNotFoundException()
        }
    }
}
