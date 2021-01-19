package com.mousiki.shared

@UseExperimental(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
expect annotation class Keep()