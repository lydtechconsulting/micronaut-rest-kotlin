package demo.rest.api

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class UpdateItemRequest(val name: String)
