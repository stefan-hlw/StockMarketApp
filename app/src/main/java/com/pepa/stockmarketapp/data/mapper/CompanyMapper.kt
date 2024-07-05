package com.pepa.stockmarketapp.data.mapper

import com.pepa.stockmarketapp.data.local.CompanyListingEntity
import com.pepa.stockmarketapp.data.remote.dto.CompanyInfoDto
import com.pepa.stockmarketapp.domain.model.CompanyInfo
import com.pepa.stockmarketapp.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyListing(): CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyListing.toCompanyListingEntity(): CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyInfoDto.toCompanyInfo(): CompanyInfo {
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}