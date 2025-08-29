package com.streview.domain.commons.result

import com.github.michaelbull.result.Result
import com.streview.domain.commons.errors.ValidationError

// =============================================================================
// build函数のオーバーロード（1個〜6個）
// Value Objectを材料にエンティティを構築する
// 内部実装は共通のResultCombinerを使用
// =============================================================================

/**
 * 2つのResultからエンティティを構築する
 */
@Suppress("UNCHECKED_CAST")
inline fun <T1, T2, R> build(
    result1: Result<T1, ValidationError>,
    result2: Result<T2, ValidationError>,
    crossinline transform: (T1, T2) -> R
): Result<R, ValidationError> {
    return ResultCombiner()
        .add(result1)
        .add(result2)
        .build { values ->
            transform(values[0] as T1, values[1] as T2)
        }
}

/**
 * 3つのResultを組み合わせる
 */
@Suppress("UNCHECKED_CAST")
inline fun <T1, T2, T3, R> build(
    result1: Result<T1, ValidationError>,
    result2: Result<T2, ValidationError>,
    result3: Result<T3, ValidationError>,
    crossinline transform: (T1, T2, T3) -> R
): Result<R, ValidationError> {
    return ResultCombiner()
        .add(result1)
        .add(result2)
        .add(result3)
        .build { values ->
            transform(values[0] as T1, values[1] as T2, values[2] as T3)
        }
}

/**
 * 4つのResultを組み合わせる
 */
@Suppress("UNCHECKED_CAST")
inline fun <T1, T2, T3, T4, R> build(
    result1: Result<T1, ValidationError>,
    result2: Result<T2, ValidationError>,
    result3: Result<T3, ValidationError>,
    result4: Result<T4, ValidationError>,
    crossinline transform: (T1, T2, T3, T4) -> R
): Result<R, ValidationError> {
    return ResultCombiner()
        .add(result1)
        .add(result2)
        .add(result3)
        .add(result4)
        .build { values ->
            transform(values[0] as T1, values[1] as T2, values[2] as T3, values[3] as T4)
        }
}

/**
 * 5つのResultを組み合わせる
 */
@Suppress("UNCHECKED_CAST")
inline fun <T1, T2, T3, T4, T5, R> build(
    result1: Result<T1, ValidationError>,
    result2: Result<T2, ValidationError>,
    result3: Result<T3, ValidationError>,
    result4: Result<T4, ValidationError>,
    result5: Result<T5, ValidationError>,
    crossinline transform: (T1, T2, T3, T4, T5) -> R
): Result<R, ValidationError> {
    return ResultCombiner()
        .add(result1)
        .add(result2)
        .add(result3)
        .add(result4)
        .add(result5)
        .build { values ->
            transform(
                values[0] as T1,
                values[1] as T2,
                values[2] as T3,
                values[3] as T4,
                values[4] as T5
            )
        }
}

/**
 * 6つのResultを組み合わせる
 */
@Suppress("UNCHECKED_CAST")
inline fun <T1, T2, T3, T4, T5, T6, R> build(
    result1: Result<T1, ValidationError>,
    result2: Result<T2, ValidationError>,
    result3: Result<T3, ValidationError>,
    result4: Result<T4, ValidationError>,
    result5: Result<T5, ValidationError>,
    result6: Result<T6, ValidationError>,
    crossinline transform: (T1, T2, T3, T4, T5, T6) -> R
): Result<R, ValidationError> {
    return ResultCombiner()
        .add(result1)
        .add(result2)
        .add(result3)
        .add(result4)
        .add(result5)
        .add(result6)
        .build { values ->
            transform(
                values[0] as T1,
                values[1] as T2,
                values[2] as T3,
                values[3] as T4,
                values[4] as T5,
                values[5] as T6
            )
        }
}

/**
 * 6つのResultを組み合わせる
 */
@Suppress("UNCHECKED_CAST")
inline fun <T1, T2, T3, T4, T5, T6, T7, R> build(
    result1: Result<T1, ValidationError>,
    result2: Result<T2, ValidationError>,
    result3: Result<T3, ValidationError>,
    result4: Result<T4, ValidationError>,
    result5: Result<T5, ValidationError>,
    result6: Result<T6, ValidationError>,
    result7: Result<T7, ValidationError>,
    crossinline transform: (T1, T2, T3, T4, T5, T6, T7) -> R
): Result<R, ValidationError> {
    return ResultCombiner()
        .add(result1)
        .add(result2)
        .add(result3)
        .add(result4)
        .add(result5)
        .add(result6)
        .add(result7)
        .build { values ->
            transform(
                values[0] as T1,
                values[1] as T2,
                values[2] as T3,
                values[3] as T4,
                values[4] as T5,
                values[5] as T6,
                values[6] as T7
            )
        }
}
