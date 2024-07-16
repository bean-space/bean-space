package com.beanspace.beanspace.domain.space.model

import jakarta.persistence.Embeddable

@Embeddable
class Address(
    val zipCode: String,
    val streetNameAddress: String,
    val sido: String,
)