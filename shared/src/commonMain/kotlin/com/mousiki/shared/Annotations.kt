package com.mousiki.shared

expect interface Parcelable

@UseExperimental(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Parcelize()

@UseExperimental(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
expect annotation class Keep()