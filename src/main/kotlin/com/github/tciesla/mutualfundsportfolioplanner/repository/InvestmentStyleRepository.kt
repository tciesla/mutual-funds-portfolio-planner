package com.github.tciesla.mutualfundsportfolioplanner.repository

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import org.springframework.stereotype.Repository

@Repository
class InvestmentStyleRepository {

    enum class Predefined {
        SECURE, BALANCED, AGGRESSIVE
    }

    private val investmentStyles = mapOf(
            Predefined.SECURE.name to InvestmentStyle(
                    name = Predefined.SECURE.name,
                    mutualFundMixture = mapOf(
                            MutualFund.Type.POLISH to 20.toBigDecimal(),
                            MutualFund.Type.FOREIGN to 75.toBigDecimal(),
                            MutualFund.Type.MONEY to 5.toBigDecimal()
                    )),
            Predefined.BALANCED.name to InvestmentStyle(
                    name = Predefined.BALANCED.name,
                    mutualFundMixture = mapOf(
                            MutualFund.Type.POLISH to 30.toBigDecimal(),
                            MutualFund.Type.FOREIGN to 60.toBigDecimal(),
                            MutualFund.Type.MONEY to 10.toBigDecimal()
                    )),
            Predefined.AGGRESSIVE.name to InvestmentStyle(
                    name = Predefined.AGGRESSIVE.name,
                    mutualFundMixture = mapOf(
                            MutualFund.Type.POLISH to 40.toBigDecimal(),
                            MutualFund.Type.FOREIGN to 20.toBigDecimal(),
                            MutualFund.Type.MONEY to 40.toBigDecimal()
                    ))
    )

    fun findAll() = investmentStyles.values.toSet()

    fun findByName(name: String)  = investmentStyles[name]

}