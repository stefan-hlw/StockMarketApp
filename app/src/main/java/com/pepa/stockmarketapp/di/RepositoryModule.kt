package com.pepa.stockmarketapp.di

import com.pepa.stockmarketapp.data.csv.CSVParser
import com.pepa.stockmarketapp.data.csv.CompanyListingsParser
import com.pepa.stockmarketapp.data.csv.IntradayInfoParser
import com.pepa.stockmarketapp.data.repository.StockRepositoryImpl
import com.pepa.stockmarketapp.domain.model.CompanyListing
import com.pepa.stockmarketapp.domain.model.IntradayInfo
import com.pepa.stockmarketapp.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepository: StockRepositoryImpl
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfo>

}
