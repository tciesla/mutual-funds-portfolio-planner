package com.github.tciesla.mutualfundsportfolioplanner.service

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentPortfolio
import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle.AGGRESSIVE
import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle.BALANCED
import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle.SECURE
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
                polishMutualFund1,
                polishMutualFund2,
                foreignMutualFund1,
                foreignMutualFund2,
                foreignMutualFund3,
                moneyMutualFund1
        )

        // when
        val portfolio = investmentPortfolioService.createPortfolio(
                selectedMutualFunds = selectedMutualFunds,
                investmentStyle = SECURE,
                availableCapital = 10_000L
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
                polishMutualFund1,
                polishMutualFund2,
                foreignMutualFund1,
                foreignMutualFund2,
                foreignMutualFund3,
                moneyMutualFund1
        )

        // when
        val portfolio = investmentPortfolioService.createPortfolio(
                selectedMutualFunds = selectedMutualFunds,
                investmentStyle = SECURE,
                availableCapital = 10_001L
        )

        // then
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
                polishMutualFund1,
                polishMutualFund2,
                polishMutualFund3,
                foreignMutualFund1,
                foreignMutualFund2,
                moneyMutualFund1
        )

        // when
        val portfolio = investmentPortfolioService.createPortfolio(
                selectedMutualFunds = selectedMutualFunds,
                investmentStyle = SECURE,
                availableCapital = 10_000L
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

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when no mutual fund selected`() {
        // when
        investmentPortfolioService.createPortfolio(
                selectedMutualFunds = listOf(),
                investmentStyle = BALANCED,
                availableCapital = thousand
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when no mutual fund selected with type required by investment style`() {
        // when
        investmentPortfolioService.createPortfolio(
                selectedMutualFunds = listOf(polishMutualFund1, moneyMutualFund1),
                investmentStyle = BALANCED,
                availableCapital = thousand
        )
    }

    @Test
    fun `should create portfolio according to secure investment style`() {
        // when
        val portfolio = investmentPortfolioService.createPortfolio(
                selectedMutualFunds = listOf(polishMutualFund1, foreignMutualFund1, moneyMutualFund1),
                investmentStyle = SECURE,
                availableCapital = thousand
        )

        // then
        assertThat(portfolio.remainingCapital).isEqualTo(0)
        assertThat(portfolio.items).containsExactly(
                InvestmentPortfolio.Item(polishMutualFund1, 200, 20.0),
                InvestmentPortfolio.Item(foreignMutualFund1, 750, 75.0),
                InvestmentPortfolio.Item(moneyMutualFund1, 50, 5.0)
        )
    }

    @Test
    fun `should create portfolio according to balanced investment style`() {
        // when
        val portfolio = investmentPortfolioService.createPortfolio(
                selectedMutualFunds = listOf(polishMutualFund1, foreignMutualFund1, moneyMutualFund1),
                investmentStyle = BALANCED,
                availableCapital = thousand
        )

        // then
        assertThat(portfolio.remainingCapital).isEqualTo(0)
        assertThat(portfolio.items).containsExactly(
                InvestmentPortfolio.Item(polishMutualFund1, 300, 30.0),
                InvestmentPortfolio.Item(foreignMutualFund1, 600, 60.0),
                InvestmentPortfolio.Item(moneyMutualFund1, 100, 10.0)
        )
    }

    @Test
    fun `should create portfolio according to aggressive investment style`() {
        // when
        val portfolio = investmentPortfolioService.createPortfolio(
                selectedMutualFunds = listOf(polishMutualFund1, foreignMutualFund1, moneyMutualFund1),
                investmentStyle = AGGRESSIVE,
                availableCapital = thousand
        )

        // then
        assertThat(portfolio.remainingCapital).isEqualTo(0)
        assertThat(portfolio.items).containsExactly(
                InvestmentPortfolio.Item(polishMutualFund1, 400, 40.0),
                InvestmentPortfolio.Item(foreignMutualFund1, 200, 20.0),
                InvestmentPortfolio.Item(moneyMutualFund1, 400, 40.0)
        )
    }

    companion object {
        const val thousand = 1000L

        val polishMutualFund1 = MutualFund(1L, "Polish Fund 1", POLISH)
        val polishMutualFund2 = MutualFund(2L, "Polish Fund 2", POLISH)
        val polishMutualFund3 = MutualFund(3L, "Polish Fund 3", POLISH)
        val foreignMutualFund1 = MutualFund(4L, "Foreign Fund 1", FOREIGN)
        val foreignMutualFund2 = MutualFund(5L, "Foreign Fund 2", FOREIGN)
        val foreignMutualFund3 = MutualFund(6L, "Foreign Fund 3", FOREIGN)
        val moneyMutualFund1 = MutualFund(7L, "Money Fund 1", MONEY)
    }
}