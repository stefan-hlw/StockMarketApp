package com.pepa.stockmarketapp.data.repository

import com.pepa.stockmarketapp.data.csv.CSVParser
import com.pepa.stockmarketapp.data.local.StockDatabase
import com.pepa.stockmarketapp.data.mapper.toCompanyListing
import com.pepa.stockmarketapp.data.mapper.toCompanyListingEntity
import com.pepa.stockmarketapp.data.remote.StockApi
import com.pepa.stockmarketapp.domain.model.CompanyListing
import com.pepa.stockmarketapp.domain.repository.StockRepository
import com.pepa.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>
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
}