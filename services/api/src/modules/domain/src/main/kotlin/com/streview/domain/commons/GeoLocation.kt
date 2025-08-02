package com.streview.domain.commons

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.errors.InvalidFormatRules
import com.streview.domain.commons.errors.ValidationError

/**
 * 位置情報を緯度経度として管理するvo
 * XXX:Result型によるエラーハンドリングの練習
 */
class GeoLocation private constructor(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        // 緯度・経度の有効範囲を定数で定義
        val LATITUDE_RANGE = -90.0..90.0
        val LONGITUDE_RANGE = -180.0..180.0
        fun create(latitude: Double, longitude: Double): Result<GeoLocation, ValidationError> {
            val validationErrors = mutableListOf<ValidationError>()
            if (latitude !in LATITUDE_RANGE) {
                validationErrors.add(
                    ValidationError.InvalidFormat("latitude", InvalidFormatRules.PATTERN_MISMATCH, latitude)
                )
            }
            if (longitude !in LONGITUDE_RANGE) {
                validationErrors.add(
                    ValidationError.InvalidFormat("longitude", InvalidFormatRules.PATTERN_MISMATCH, longitude)
                )
            }
            return when (validationErrors.size) {
                0 -> {
                    Ok(GeoLocation(latitude, longitude))
                }
                1 -> {
                    Err(validationErrors.first())
                }
                else -> {
                    Err(ValidationError.Multiple(validationErrors))
                }
            }
        }

        fun reconstruct(latitude: Double, longitude: Double): GeoLocation {
            require(latitude in LATITUDE_RANGE && longitude in LONGITUDE_RANGE) {
                "Invalid coordinates: latitude=$latitude, longitude=$longitude"
            }
            return GeoLocation(latitude, longitude)
        }
    }
}
