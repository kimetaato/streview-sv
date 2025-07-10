package com.streview.domain.exceptions

// HACK: これ絶対いらんけどエラーの扱いがミスっている証拠
/**
 * 永続化層から取得したデータがドメインに変換出来なかったことを示す例外クラス
 * これはなんでしょう？500返すけどハンドリングする必要ないよね
 * @param message  エラーメッセージ
 * @param cause 元となったエラー
 */
class DataFormatException(
    message: String,
    cause: Throwable? = null
) : BusinessException(message, cause)
