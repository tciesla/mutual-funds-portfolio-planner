package com.github.tciesla.mutualfundsportfolioplanner.domain

class InvestmentStyle(val name: String, mutualFundMixture: Map<MutualFund.Type, Double>) {

    val mutualFundMixture = validatedMutualFundMixture(mutualFundMixture)

    private fun validatedMutualFundMixture(mutualFundMixture: Map<MutualFund.Type, Double>)
            : Map<MutualFund.Type, Double> {

        if (mutualFundMixture.values.sum() != 100.00) {
            throw IllegalArgumentException("mutual fund mixture must sum up to 100%")
        }

        if (mutualFundMixture.values.any { it <= 0  }) {
            throw IllegalArgumentException("mutual fund mixture could have values only above 0.00%")
        }

        return mutualFundMixture
    }

}