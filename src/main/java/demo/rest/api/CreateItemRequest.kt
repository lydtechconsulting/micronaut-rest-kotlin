package demo.rest.api

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class CreateItemRequest(val name: String)
