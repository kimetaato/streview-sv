package com.streview.domain.exceptions

/**
 * 永続化層から取得したデータがドメインに変換出来なかったことを示す例外クラス
 * @param message  エラーメッセージ
 * @param cause 元となったエラー
 */
class DataFormatException(
    message: String,
    cause: Throwable? = null
) : BusinessException(message, cause)
