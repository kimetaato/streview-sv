package com.streview.domain.reviews

import com.streview.domain.commons.UUID
import java.math.BigDecimal

class DraftReview private constructor(
    val comment: Comment,
    val star: Star,
    val imageUUIDs: List<UUID>,
) {
    companion object {
        fun factory(comment: String, star: BigDecimal, imageUUIDs: List<UUID>): DraftReview {
            return DraftReview(
                Comment(comment),
                Star(star),
                imageUUIDs,
            )
        }

        fun reconstruct(comment: String, star: BigDecimal, imageUUIDs: List<String>): DraftReview {
            return DraftReview(
                Comment(comment),
                Star(star),
                imageUUIDs.map { imageUUID -> UUID(imageUUID) },
            )
        }
    }
}
