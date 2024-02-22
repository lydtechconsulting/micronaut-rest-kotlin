package demo.controller

import demo.exception.ItemNotFoundException
import demo.rest.api.CreateItemRequest
import demo.rest.api.GetItemResponse
import demo.rest.api.UpdateItemRequest
import demo.service.ItemService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.Put

import jakarta.inject.Inject
import java.net.URI
import java.util.UUID
import org.slf4j.LoggerFactory
import javax.validation.Valid

@Controller("/v1/items")
class ItemController @Inject constructor(private val itemService: ItemService) {

    private val log = LoggerFactory.getLogger(ItemController::class.java)

    @Post
    fun createItem(@Body request: @Valid CreateItemRequest): HttpResponse<Void> {
        log.info("Received request to create item with name: {}", request.name)
        return try {
            val itemId = itemService.createItem(request)
            HttpResponse.created(URI.create(itemId.toString()))
        } catch (e: Exception) {
            log.error(e.message)
            HttpResponse.serverError()
        }
    }

    @Get("/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getItem(@PathVariable itemId: UUID): HttpResponse<GetItemResponse> {
        log.info("Looking up item with id: {}", itemId)
        return try {
            val response = itemService.getItem(itemId)
            HttpResponse.ok(response)
        } catch (e: ItemNotFoundException) {
            HttpResponse.notFound()
        }
    }

    @Put("/{itemId}")
    fun updateItem(@PathVariable itemId: UUID, @Body request: @Valid UpdateItemRequest): HttpResponse<Void> {
        log.info("Received request to update item with id: {} - name: {}", itemId, request.name)
        return try {
            itemService.updateItem(itemId, request)
            HttpResponse.noContent()
        } catch (e: ItemNotFoundException) {
            HttpResponse.notFound()
        } catch (e: Exception) {
            log.error(e.message)
            HttpResponse.serverError()
        }
    }

    @Delete("/{itemId}")
    fun deleteItem(@PathVariable itemId: UUID): HttpResponse<Void> {
        log.info("Deleting item with id: {}", itemId)
        return try {
            itemService.deleteItem(itemId)
            HttpResponse.noContent()
        } catch (e: ItemNotFoundException) {
            log.error(e.message)
            HttpResponse.notFound()
        }
    }
}
