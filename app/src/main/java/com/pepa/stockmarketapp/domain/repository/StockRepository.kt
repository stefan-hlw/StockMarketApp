package com.pepa.stockmarketapp.domain.repository

import com.pepa.stockmarketapp.domain.model.CompanyInfo
import com.pepa.stockmarketapp.domain.model.CompanyListing
import com.pepa.stockmarketapp.domain.model.IntradayInfo
import com.pepa.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getIntradayInfo(
        symbol: String
    ): Resource<List<IntradayInfo>>

    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfo>
}