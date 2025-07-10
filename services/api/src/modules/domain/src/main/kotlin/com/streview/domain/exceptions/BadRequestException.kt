package com.streview.domain.exceptions

/**
 * リクエストがおかしいよねって時に返す例外クラス
 *
 * 多分400返すよ
 */
class BadRequestException(
    message: String,
    cause: Throwable? = null
) : BusinessException(message, cause)
