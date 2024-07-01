package com.pepa.stockmarketapp.data.repository

import com.pepa.stockmarketapp.data.csv.CSVParser
import com.pepa.stockmarketapp.data.local.StockDatabase
import com.pepa.stockmarketapp.data.mapper.toCompanyInfo
import com.pepa.stockmarketapp.data.mapper.toCompanyListing
import com.pepa.stockmarketapp.data.mapper.toCompanyListingEntity
import com.pepa.stockmarketapp.data.remote.StockApi
import com.pepa.stockmarketapp.domain.model.CompanyInfo
import com.pepa.stockmarketapp.domain.model.CompanyListing
import com.pepa.stockmarketapp.domain.model.IntradayInfo
import com.pepa.stockmarketapp.domain.repository.StockRepository
import com.pepa.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayListingsParser: CSVParser<IntradayInfo>
): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {

        return flow {

            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if(shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = runCatching {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            }.getOrElse { e ->
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map {
                        it.toCompanyListingEntity()
                    }
                )
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return runCatching {
            val response = api.getIntradayInfo(symbol)
            val results = intradayListingsParser.parse(response.byteStream())
            Resource.Success(results)
        }.getOrElse { throwable ->
            when (throwable) {
                is HttpException -> Resource.Error(
                    throwable.localizedMessage ?: "Unexpected error occurred"
                )
                is IOException -> Resource.Error("Connection issue")
                else -> Resource.Error("An unknown error occurred")
            }
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return runCatching {
            val results = api.getCompanyInfo(symbol)
            Resource.Success(results.toCompanyInfo())
        }.getOrElse { throwable ->
            when (throwable) {
                is HttpException -> Resource.Error(
                    throwable.localizedMessage ?: "Unexpected error occurred"
                )
                is IOException -> Resource.Error("Connection issue")
                else -> Resource.Error("An unknown error occurred")
            }
        }
    }
}