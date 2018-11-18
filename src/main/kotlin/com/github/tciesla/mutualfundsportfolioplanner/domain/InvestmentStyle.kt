package com.github.tciesla.mutualfundsportfolioplanner.domain

enum class InvestmentStyle(val mutualFundMixture: Map<MutualFund.Type, Int>) {

    SECURE(mapOf(
            MutualFund.Type.POLISH to 20,
            MutualFund.Type.FOREIGN to 75,
            MutualFund.Type.MONEY to 5
    )),

    BALANCED(mapOf(
            MutualFund.Type.POLISH to 30,
            MutualFund.Type.FOREIGN to 60,
            MutualFund.Type.MONEY to 10
    )),

    AGGRESSIVE(mapOf(
            MutualFund.Type.POLISH to 40,
            MutualFund.Type.FOREIGN to 20,
            MutualFund.Type.MONEY to 40
    ));

}
