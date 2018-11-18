package com.github.tciesla.mutualfundsportfolioplanner.domain

import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.FOREIGN
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.MONEY
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.POLISH
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InvestmentStyleTest {

    @Test
    fun `should three different investment styles be available`() {
        // when
        val investmentStyles = InvestmentStyle.values()

        // then
        assertThat(investmentStyles).hasSize(3)
    }

    @Test
    fun `should return secure investment style`() {
        // when
        val investmentStyle = InvestmentStyle.SECURE

        // then
        val mixture: Map<MutualFund.Type, Int> = investmentStyle.mutualFundMixture
        assertThat(mixture[POLISH]).isEqualTo(20)
        assertThat(mixture[FOREIGN]).isEqualTo(75)
        assertThat(mixture[MONEY]).isEqualTo(5)
    }

    @Test
    fun `should return balanced investment style`() {
        // when
        val investmentStyle = InvestmentStyle.BALANCED

        // then
        val mixture = investmentStyle.mutualFundMixture
        assertThat(mixture[POLISH]).isEqualTo(30)
        assertThat(mixture[FOREIGN]).isEqualTo(60)
        assertThat(mixture[MONEY]).isEqualTo(10)
    }

    @Test
    fun `should return aggressive investment style`() {
        // when
        val investmentStyle = InvestmentStyle.AGGRESSIVE

        // then
        val mixture = investmentStyle.mutualFundMixture
        assertThat(mixture[POLISH]).isEqualTo(40)
        assertThat(mixture[FOREIGN]).isEqualTo(20)
        assertThat(mixture[MONEY]).isEqualTo(40)
    }

}