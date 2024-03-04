package demo.rest.api

import java.util.UUID
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class GetItemResponse (
    var id: UUID,
    var name: String
)
