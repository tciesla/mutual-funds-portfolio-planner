package com.github.tciesla.mutualfundsportfolioplanner.domain

import java.math.BigDecimal

data class InvestmentPortfolio(
        val investedAmount: BigDecimal,
        val notInvestedAmount: BigDecimal,
        val investmentStyle: InvestmentStyle,
        val items: List<Item>) {

    data class Item(
            val mutualFund: MutualFund,
            val investedAmount: BigDecimal,
            val portfolioShare: BigDecimal
    )

}
