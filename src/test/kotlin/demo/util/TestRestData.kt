package demo.util

import java.util.UUID
import demo.rest.api.CreateItemRequest
import demo.rest.api.GetItemResponse
import demo.rest.api.UpdateItemRequest

object TestRestData {

    fun buildCreateItemRequest(name: String): CreateItemRequest {
        return CreateItemRequest(name)
    }

    fun buildUpdateItemRequest(name: String): UpdateItemRequest {
        return UpdateItemRequest(name)
    }

    fun buildGetItemResponse(id: UUID, name: String): GetItemResponse {
        return GetItemResponse(id, name)
    }
}
