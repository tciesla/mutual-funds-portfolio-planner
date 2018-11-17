package com.github.tciesla.mutualfundsportfolioplanner.service

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentPortfolio
import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import com.github.tciesla.mutualfundsportfolioplanner.repository.InvestmentStyleRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class InvestmentPortfolioService(val investmentStyleRepository: InvestmentStyleRepository) {

    fun createPortfolio(
            selectedMutualFunds: List<MutualFund>,
            investmentStyleName: String,
            investmentAmount: BigDecimal
    ) : InvestmentPortfolio {

        val investmentStyle = investmentStyleRepository.findByName(investmentStyleName)
            ?: throw IllegalArgumentException("investment style with name $investmentStyleName not found")

        val remainingEquity = calculateRemainingAmount(investmentStyle, investmentAmount)
        val investedAmount = investmentAmount - remainingEquity

        val portfolioItems = investmentStyle.mutualFundMixture
                .map { it.key to (it.value.divide(100.toBigDecimal())) * investedAmount }
                .map { (mutualFundType, mutualFundInvestedAmount) ->
                    val mutualFundCount = selectedMutualFunds.count { it.type == mutualFundType }
                    selectedMutualFunds.filter { it.type == mutualFundType }
                            .map {
                                println(mutualFundInvestedAmount.divide(mutualFundCount.toBigDecimal()))
                                InvestmentPortfolio.Item(
                                    it,
                                    (mutualFundInvestedAmount.divide(mutualFundCount.toBigDecimal())),
                                    ((mutualFundInvestedAmount.divide(mutualFundCount.toBigDecimal())).divide(investedAmount)) * 100.toBigDecimal()
                                    ) }
                }.flatten()

        return InvestmentPortfolio(
                investedAmount = investedAmount,
                notInvestedAmount = remainingEquity,
                investmentStyle = investmentStyle,
                items = portfolioItems
        ).also { println(it) }
    }

    private fun calculateRemainingAmount(investmentStyle: InvestmentStyle, investmentAmount: BigDecimal): BigDecimal {
        return investmentStyle.mutualFundMixture
                .map { investmentAmount * (it.value.divide(100.toBigDecimal())) }
                .also { println(it) }
                .map { it - it.setScale(0, RoundingMode.FLOOR) }
                .also { println(it) }
                .reduce { acc, amount -> acc + amount }
    }

}