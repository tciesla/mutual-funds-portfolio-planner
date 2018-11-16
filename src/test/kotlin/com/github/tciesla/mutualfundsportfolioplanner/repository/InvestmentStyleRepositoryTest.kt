package com.github.tciesla.mutualfundsportfolioplanner.repository

import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InvestmentStyleRepositoryTest {

    private val investmentStyleRepository = InvestmentStyleRepository()

    @Test
    fun `should return secure investment style`() {
        // when
        val investmentStyle = investmentStyleRepository.findByName(name = "secure")

        // then
        assertThat(investmentStyle).isNotNull
        assertThat(investmentStyle!!.mutualFundMixture[MutualFund.Type.POLISH]).isEqualTo(20.00)
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.FOREIGN]).isEqualTo(75.00)
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.MONEY]).isEqualTo(5.00)
    }

    @Test
    fun `should return balanced investment style`() {
        // when
        val investmentStyle = investmentStyleRepository.findByName(name = "balanced")

        // then
        assertThat(investmentStyle).isNotNull
        assertThat(investmentStyle!!.mutualFundMixture[MutualFund.Type.POLISH]).isEqualTo(30.00)
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.FOREIGN]).isEqualTo(60.00)
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.MONEY]).isEqualTo(10.00)
    }

    @Test
    fun `should return aggressive investment style`() {
        // when
        val investmentStyle = investmentStyleRepository.findByName(name = "aggressive")

        // then
        assertThat(investmentStyle).isNotNull
        assertThat(investmentStyle!!.mutualFundMixture[MutualFund.Type.POLISH]).isEqualTo(40.00)
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.FOREIGN]).isEqualTo(20.00)
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.MONEY]).isEqualTo(40.00)
    }

}