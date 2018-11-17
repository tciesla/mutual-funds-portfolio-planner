package com.github.tciesla.mutualfundsportfolioplanner.domain

import java.math.BigDecimal

class InvestmentStyle(val name: String, mutualFundMixture: Map<MutualFund.Type, BigDecimal>) {

    val mutualFundMixture = validatedMutualFundMixture(mutualFundMixture)

    companion object {
        val HUNDRED = 100.toBigDecimal()
    }

    private fun validatedMutualFundMixture(mutualFundMixture: Map<MutualFund.Type, BigDecimal>)
            : Map<MutualFund.Type, BigDecimal> {

        if (mutualFundMixture.isEmpty()) {
            throw IllegalArgumentException("mutual fund mixture is empty")
        }

        val overallPercentages = mutualFundMixture.values
                .reduce { acc, number -> acc.plus(number) }

        if (overallPercentages < HUNDRED || overallPercentages > HUNDRED) {
            throw IllegalArgumentException("mutual fund mixture must sum up to 100%")
        }

        if (mutualFundMixture.values.any { it <= BigDecimal.ZERO  }) {
            throw IllegalArgumentException("mutual fund mixture could have values only above 0.00%")
        }

        return mutualFundMixture
    }

}