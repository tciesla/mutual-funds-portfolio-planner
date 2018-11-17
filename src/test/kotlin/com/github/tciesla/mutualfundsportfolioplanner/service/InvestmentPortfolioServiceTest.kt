package com.github.tciesla.mutualfundsportfolioplanner.service

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentPortfolio
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.FOREIGN
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.MONEY
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.POLISH
import com.github.tciesla.mutualfundsportfolioplanner.repository.InvestmentStyleRepository.Predefined.SECURE
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO

@SpringBootTest
@RunWith(SpringRunner::class)
class InvestmentPortfolioServiceTest {

    @Autowired
    private lateinit var investmentPortfolioService: InvestmentPortfolioService

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
                investmentStyleName = SECURE.name,
                investmentAmount = 10_000.00.toBigDecimal()
        )

        // then
        assertThat(portfolio.notInvestedAmount.compareTo(ZERO)).isEqualTo(0)

        portfolio.items[0].match( expectedInvestedAmount = 1000.00, expectedPortfolioShare =  10.00)
        portfolio.items[1].match( expectedInvestedAmount = 1000.00, expectedPortfolioShare =  10.00)
        portfolio.items[2].match( expectedInvestedAmount = 2500.00, expectedPortfolioShare =  25.00)
        portfolio.items[3].match( expectedInvestedAmount = 2500.00, expectedPortfolioShare =  25.00)
        portfolio.items[4].match( expectedInvestedAmount = 2500.00, expectedPortfolioShare =  25.00)
        portfolio.items[5].match( expectedInvestedAmount = 500.00, expectedPortfolioShare =  5.00)
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
                investmentStyleName = SECURE.name,
                investmentAmount = 10_001.00.toBigDecimal()
        )

        // then
        println(portfolio.notInvestedAmount)
        assertThat(portfolio.notInvestedAmount.compareTo(ONE)).isEqualTo(0)

        portfolio.items[0].match( expectedInvestedAmount = 1000.00, expectedPortfolioShare =  10.00)
        portfolio.items[1].match( expectedInvestedAmount = 1000.00, expectedPortfolioShare =  10.00)
        portfolio.items[2].match( expectedInvestedAmount = 2500.00, expectedPortfolioShare =  25.00)
        portfolio.items[3].match( expectedInvestedAmount = 2500.00, expectedPortfolioShare =  25.00)
        portfolio.items[4].match( expectedInvestedAmount = 2500.00, expectedPortfolioShare =  25.00)
        portfolio.items[5].match( expectedInvestedAmount = 500.00, expectedPortfolioShare =  5.00)
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
                investmentStyleName = SECURE.name,
                investmentAmount = 10_000.00.toBigDecimal()
        )

        // then
        assertThat(portfolio.notInvestedAmount.compareTo(ZERO)).isEqualTo(0)

        portfolio.items[0].match( expectedInvestedAmount = 668.00, expectedPortfolioShare =  6.68)
        portfolio.items[1].match( expectedInvestedAmount = 666.00, expectedPortfolioShare =  6.66)
        portfolio.items[2].match( expectedInvestedAmount = 666.00, expectedPortfolioShare =  6.66)
        portfolio.items[3].match( expectedInvestedAmount = 3750.00, expectedPortfolioShare =  37.50)
        portfolio.items[4].match( expectedInvestedAmount = 3750.00, expectedPortfolioShare =  37.50)
        portfolio.items[5].match( expectedInvestedAmount = 500.00, expectedPortfolioShare =  5.00)
    }

    private fun InvestmentPortfolio.Item.match(
            expectedInvestedAmount: Double,
            expectedPortfolioShare: Double) {

        assertThat(this.investedAmount.compareTo(expectedInvestedAmount.toBigDecimal())).isEqualTo(0)
        assertThat(this.portfolioShare.compareTo(expectedPortfolioShare.toBigDecimal())).isEqualTo(0)
    }

}