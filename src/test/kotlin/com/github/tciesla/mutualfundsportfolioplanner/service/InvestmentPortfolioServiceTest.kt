package com.github.tciesla.mutualfundsportfolioplanner.service

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentPortfolio
import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.FOREIGN
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.MONEY
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.POLISH
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InvestmentPortfolioServiceTest {

    private val investmentPortfolioService = InvestmentPortfolioService()

    @Test
    fun `should pass task example #0`() {
        // given
        val selectedMutualFunds = listOf(
                MutualFund(1L, "Fundusz Polski 1", POLISH),
                MutualFund(2L, "Fundusz Polski 2", POLISH),
                MutualFund(3L, "Fundusz Zagraniczny 1", FOREIGN),
                MutualFund(4L, "Fundusz Zagraniczny 2", FOREIGN),
                MutualFund(5L, "Fundusz Zagraniczny 3", FOREIGN),
                MutualFund(6L, "Fundusz Pieniężny 1", MONEY)
        )

        // when
        val portfolio = investmentPortfolioService.createPortfolio(
                selectedMutualFunds = selectedMutualFunds,
                investmentStyle = InvestmentStyle.SECURE,
                availableCapital = 10_000
        )

        // then
        assertThat(portfolio.remainingCapital).isEqualTo(0L)

        portfolio.items[0].match(1000.00, 10.00)
        portfolio.items[1].match(1000.00, 10.00)
        portfolio.items[2].match(2500.00, 25.00)
        portfolio.items[3].match(2500.00, 25.00)
        portfolio.items[4].match(2500.00, 25.00)
        portfolio.items[5].match(500.00, 5.00)
    }

    @Test
    fun `should pass task example #1`() {
        // given
        val selectedMutualFunds = listOf(
                MutualFund(1L, "Fundusz Polski 1", POLISH),
                MutualFund(2L, "Fundusz Polski 2", POLISH),
                MutualFund(3L, "Fundusz Zagraniczny 1", FOREIGN),
                MutualFund(4L, "Fundusz Zagraniczny 2", FOREIGN),
                MutualFund(5L, "Fundusz Zagraniczny 3", FOREIGN),
                MutualFund(6L, "Fundusz Pieniężny 1", MONEY)
        )

        // when
        val portfolio = investmentPortfolioService.createPortfolio(
                selectedMutualFunds = selectedMutualFunds,
                investmentStyle = InvestmentStyle.SECURE,
                availableCapital = 10_001
        )

        // then
        println(portfolio.remainingCapital)
        assertThat(portfolio.remainingCapital).isEqualTo(1L)

        portfolio.items[0].match(1000.00, 10.00)
        portfolio.items[1].match(1000.00, 10.00)
        portfolio.items[2].match(2500.00, 25.00)
        portfolio.items[3].match(2500.00, 25.00)
        portfolio.items[4].match(2500.00, 25.00)
        portfolio.items[5].match(500.00, 5.00)
    }

    @Test
    fun `should pass task example #2`() {
        // given
        val selectedMutualFunds = listOf(
                MutualFund(1L, "Fundusz Polski 1", POLISH),
                MutualFund(2L, "Fundusz Polski 2", POLISH),
                MutualFund(3L, "Fundusz Polski 3", POLISH),
                MutualFund(4L, "Fundusz Zagraniczny 1", FOREIGN),
                MutualFund(5L, "Fundusz Zagraniczny 2", FOREIGN),
                MutualFund(6L, "Fundusz Pieniężny 1", MONEY)
        )

        // when
        val portfolio = investmentPortfolioService.createPortfolio(
                selectedMutualFunds = selectedMutualFunds,
                investmentStyle = InvestmentStyle.SECURE,
                availableCapital = 10_000
        )

        // then
        assertThat(portfolio.remainingCapital).isEqualTo(0L)

        portfolio.items[0].match(668.00, 6.68)
        portfolio.items[1].match(666.00, 6.66)
        portfolio.items[2].match(666.00, 6.66)
        portfolio.items[3].match(3750.00, 37.50)
        portfolio.items[4].match(3750.00, 37.50)
        portfolio.items[5].match(500.00, 5.00)
    }

    private fun InvestmentPortfolio.Item.match(
            expectedInvestedAmount: Double,
            expectedPortfolioShare: Double) {

        assertThat(this.investedCapital.compareTo(expectedInvestedAmount)).isEqualTo(0)
        assertThat(this.portfolioShare.compareTo(expectedPortfolioShare)).isEqualTo(0)
    }

}