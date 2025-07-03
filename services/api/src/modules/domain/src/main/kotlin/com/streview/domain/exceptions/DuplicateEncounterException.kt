package com.streview.domain.exceptions

// すでにすれ違いを登録済みである例外 すでにすれ違っていることは利用者側に通知する必要はない
data class DuplicateEncounterException(override val message: String) : BusinessException(message)