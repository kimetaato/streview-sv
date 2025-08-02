package com.streview.domain.commons.result

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.errors.ValidationError

/**
 * 可変長引数でResultを組み合わせるビルダークラス
 * 型安全性を保ちながら複数のResultを組み合わせる
 */
class ResultCombiner {
    private val values = mutableListOf<Any?>()
    private val errors = mutableListOf<ValidationError>()

    /**
     * Resultを追加する
     * エラーの場合は、エラーをフラットなリストとして `errors` に追加します。
     */
    fun <T> add(result: Result<T, ValidationError>): ResultCombiner {
        if (result.isErr) {
            // エラーであれば、そのエラーをフラットな形式で errors リストに追加
            errors.addAll(result.error.toFlatValidationErrors())
        } else {
            // 成功の場合は値を values リストに追加
            values.add(result.value)
        }
        return this
    }

    /**
     * すべてのResultを組み合わせて最終結果を生成
     */
    fun <R> build(transform: (List<Any?>) -> R): Result<R, ValidationError> {
        return if (errors.isEmpty()) {
            Ok(transform(values))
        } else {
            Err(if (errors.size == 1) errors.first() else ValidationError.Multiple(errors))
        }
    }
}
