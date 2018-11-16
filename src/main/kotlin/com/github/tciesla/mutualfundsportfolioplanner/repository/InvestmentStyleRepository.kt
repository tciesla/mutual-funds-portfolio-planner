package com.github.tciesla.mutualfundsportfolioplanner.repository

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import org.springframework.stereotype.Repository

@Repository
class InvestmentStyleRepository {

    val investmentStyles = mapOf(
            "secure" to InvestmentStyle(name = "secure", mutualFundMixture = mapOf(
                    MutualFund.Type.POLISH to 20.00,
                    MutualFund.Type.FOREIGN to 75.00,
                    MutualFund.Type.MONEY to 5.00
            )),
            "balanced" to InvestmentStyle(name = "balanced", mutualFundMixture = mapOf(
                    MutualFund.Type.POLISH to 30.00,
                    MutualFund.Type.FOREIGN to 60.00,
                    MutualFund.Type.MONEY to 10.00
            )),
            "aggressive" to InvestmentStyle(name = "aggressive", mutualFundMixture = mapOf(
                    MutualFund.Type.POLISH to 40.00,
                    MutualFund.Type.FOREIGN to 20.00,
                    MutualFund.Type.MONEY to 40.00
            ))
    )

    fun findByName(name: String)  = investmentStyles[name]

}