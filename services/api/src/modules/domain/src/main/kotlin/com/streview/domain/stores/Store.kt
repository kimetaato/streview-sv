package com.streview.domain.stores

import com.github.michaelbull.result.Result
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.UUID
import com.streview.domain.commons.errors.ValidationError
import com.streview.domain.commons.result.build
import com.streview.domain.stores.vo.Address
import com.streview.domain.stores.vo.Description
import com.streview.domain.stores.vo.Genre
import com.streview.domain.stores.vo.Name
import com.streview.domain.stores.vo.Open
import com.streview.domain.stores.vo.Tel

class Store private constructor(
    val storeUUID: UUID,
    val name: Name,
    val genre: Genre,
    val address: Address,
    val tel: Tel,
    val description: Description,
    val open: Open,
    val geoLocation: GeoLocation,
) {
    companion object {
        fun create(
            name: String,
            genre: String,
            address: String,
            tel: String,
            description: String,
            open: String,
            latitude: Double,
            longitude: Double
        ): Result<Store, ValidationError> {
            return build( // VOから生成した値をまとめて返すだけの関数に適当に値を突っ込んで拾う
                Name.create(name),
                Genre.create(genre),
                Address.create(address),
                Tel.create(tel),
                Description.create(description),
                Open.create(open),
                GeoLocation.create(latitude, longitude)
            ) { name, genre, address, tel, description, open, geoLocation ->
                Store(UUID.generate(), name, genre, address, tel, description, open, geoLocation)
            }
        }

        fun reconstruct(
            storeUUID: String,
            name: String,
            genre: String,
            address: String,
            tel: String,
            description: String,
            open: String,
            latitude: Double,
            longitude: Double
        ): Store = Store(
            UUID.generate(storeUUID),
            Name.reconstruct(name),
            Genre.reconstruct(genre),
            Address.reconstruct(address),
            Tel.reconstruct(tel),
            Description.reconstruct(description),
            Open.reconstruct(open),
            GeoLocation.reconstruct(latitude, longitude)
        )
    }
}
