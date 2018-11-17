package com.github.tciesla.mutualfundsportfolioplanner.repository

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import org.springframework.stereotype.Repository

@Repository
class InvestmentStyleRepository {

    val investmentStyles = mapOf(
            "secure" to InvestmentStyle(name = "secure", mutualFundMixture = mapOf(
                    MutualFund.Type.POLISH to 20.00.toBigDecimal(),
                    MutualFund.Type.FOREIGN to 75.00.toBigDecimal(),
                    MutualFund.Type.MONEY to 5.00.toBigDecimal()
            )),
            "balanced" to InvestmentStyle(name = "balanced", mutualFundMixture = mapOf(
                    MutualFund.Type.POLISH to 30.00.toBigDecimal(),
                    MutualFund.Type.FOREIGN to 60.00.toBigDecimal(),
                    MutualFund.Type.MONEY to 10.00.toBigDecimal()
            )),
            "aggressive" to InvestmentStyle(name = "aggressive", mutualFundMixture = mapOf(
                    MutualFund.Type.POLISH to 40.00.toBigDecimal(),
                    MutualFund.Type.FOREIGN to 20.00.toBigDecimal(),
                    MutualFund.Type.MONEY to 40.00.toBigDecimal()
            ))
    )

    fun findByName(name: String)  = investmentStyles[name]

}