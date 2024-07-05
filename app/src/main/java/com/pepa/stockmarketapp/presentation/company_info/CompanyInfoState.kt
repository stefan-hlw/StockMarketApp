package com.pepa.stockmarketapp.presentation.company_info

import com.pepa.stockmarketapp.domain.model.CompanyInfo
import com.pepa.stockmarketapp.domain.model.IntradayInfo

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
