package demo.util;

import java.util.UUID;

import demo.rest.api.CreateItemRequest;
import demo.rest.api.GetItemResponse;
import demo.rest.api.UpdateItemRequest;

public class TestRestData {

    public static CreateItemRequest buildCreateItemRequest(String name) {
        return new CreateItemRequest(name);
    }

    public static UpdateItemRequest buildUpdateItemRequest(String name) {
        return new UpdateItemRequest(name);
    }

    public static GetItemResponse buildGetItemResponse(UUID id, String name) {
        return new GetItemResponse(id, name);
    }
}
