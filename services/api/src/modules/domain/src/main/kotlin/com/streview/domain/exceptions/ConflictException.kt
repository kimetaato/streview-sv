package com.streview.domain.exceptions


/**
 * 生成しようとするリソースがすでに存在していたことを示す例外クラス
 * @param message エラーメッセージ
 * @param cause 元となったエラー
 */
class ConflictException(
    message: String,
    cause: Throwable? = null
) : BusinessException(message, cause)