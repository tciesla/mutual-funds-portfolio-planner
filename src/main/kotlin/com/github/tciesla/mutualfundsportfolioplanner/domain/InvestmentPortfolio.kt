package com.github.tciesla.mutualfundsportfolioplanner.domain

data class InvestmentPortfolio(
        val remainingCapital: Long,
        val items: List<Item>) {

    data class Item(
            val mutualFund: MutualFund,
            val investedCapital: Long,
            val portfolioShare: Double
    )

}
