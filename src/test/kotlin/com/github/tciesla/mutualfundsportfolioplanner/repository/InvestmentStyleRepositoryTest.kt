package com.github.tciesla.mutualfundsportfolioplanner.repository

import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InvestmentStyleRepositoryTest {

    private val investmentStyleRepository = InvestmentStyleRepository()

    @Test
    fun `should three different investment styles be available`() {
        // when
        val investmentStyles = investmentStyleRepository.findAll()

        // then
        assertThat(investmentStyles).hasSize(3)
    }

    @Test
    fun `should return predefined secure investment style`() {
        // when
        val investmentStyle = investmentStyleRepository.findByName(
                InvestmentStyleRepository.Predefined.SECURE.name)

        // then
        assertThat(investmentStyle).isNotNull
        assertThat(investmentStyle!!.mutualFundMixture[MutualFund.Type.POLISH]).isEqualTo(20.toBigDecimal())
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.FOREIGN]).isEqualTo(75.toBigDecimal())
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.MONEY]).isEqualTo(5.toBigDecimal())
    }

    @Test
    fun `should return predefined balanced investment style`() {
        // when
        val investmentStyle = investmentStyleRepository.findByName(
                InvestmentStyleRepository.Predefined.BALANCED.name)

        // then
        assertThat(investmentStyle).isNotNull
        assertThat(investmentStyle!!.mutualFundMixture[MutualFund.Type.POLISH]).isEqualTo(30.toBigDecimal())
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.FOREIGN]).isEqualTo(60.toBigDecimal())
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.MONEY]).isEqualTo(10.toBigDecimal())
    }

    @Test
    fun `should return predefined aggressive investment style`() {
        // when
        val investmentStyle = investmentStyleRepository.findByName(
                InvestmentStyleRepository.Predefined.AGGRESSIVE.name)

        // then
        assertThat(investmentStyle).isNotNull
        assertThat(investmentStyle!!.mutualFundMixture[MutualFund.Type.POLISH]).isEqualTo(40.toBigDecimal())
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.FOREIGN]).isEqualTo(20.toBigDecimal())
        assertThat(investmentStyle.mutualFundMixture[MutualFund.Type.MONEY]).isEqualTo(40.toBigDecimal())
    }

}