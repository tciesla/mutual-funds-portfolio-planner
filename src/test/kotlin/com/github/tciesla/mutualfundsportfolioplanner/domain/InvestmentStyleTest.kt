package com.github.tciesla.mutualfundsportfolioplanner.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal

class InvestmentStyleTest {

    @Test
    fun `should create investment style with example mixture`() {
        // given
        val secureMutualFundMixture = mapOf(
                MutualFund.Type.POLISH to 20.toBigDecimal(),
                MutualFund.Type.FOREIGN to 75.toBigDecimal(),
                MutualFund.Type.MONEY to 5.toBigDecimal())

        // when
        val investmentStyle = InvestmentStyle(name = "secure", mutualFundMixture = secureMutualFundMixture)

        // then
        assertThat(investmentStyle.name).isEqualTo("secure")
        assertThat(investmentStyle.mutualFundMixture).isSameAs(secureMutualFundMixture)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when mutual fund mixture is empty`() {
        // when
        InvestmentStyle(name = "impossible", mutualFundMixture = mapOf())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when mutual fund mixture sum up to 95%`() {
        // when
        InvestmentStyle(name = "impossible", mutualFundMixture = mapOf(
                MutualFund.Type.POLISH to 50.toBigDecimal(),
                MutualFund.Type.FOREIGN to 45.toBigDecimal()
        ))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when mutual fund mixture contains negative number`() {
        // when
        InvestmentStyle(name = "impossible", mutualFundMixture = mapOf(
                MutualFund.Type.POLISH to (-50).toBigDecimal(),
                MutualFund.Type.FOREIGN to 50.toBigDecimal(),
                MutualFund.Type.MONEY to 100.toBigDecimal()
        ))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when mutual fund mixture contains zero number`() {
        // when
        InvestmentStyle(name = "impossible", mutualFundMixture = mapOf(
                MutualFund.Type.POLISH to 50.toBigDecimal(),
                MutualFund.Type.FOREIGN to BigDecimal.ZERO,
                MutualFund.Type.MONEY to 50.toBigDecimal()
        ))
    }

    @Test
    fun `should create investment style when mutual fund mixture sum up to 100%`() {
        // when
        InvestmentStyle(name = "international", mutualFundMixture = mapOf(
                MutualFund.Type.FOREIGN to 95.50.toBigDecimal(),
                MutualFund.Type.MONEY to 4.50.toBigDecimal()
        ))
    }

}