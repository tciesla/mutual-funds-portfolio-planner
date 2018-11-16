package com.github.tciesla.mutualfundsportfolioplanner.repository

import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MutualFundRepositoryTest {

    private val mutualFundRepository: MutualFundRepository = MutualFundRepository()

    @Test
    fun `should return six mutual funds`() {
        // when
        val mutualFunds = mutualFundRepository.findAll()

        // then
        assertThat(mutualFunds).hasSize(6)
    }

    @Test
    fun `should return mutual funds with given ids`() {
        // when
        val mutualFunds = mutualFundRepository.findByIds(listOf(1L, 3L))

        // then
        assertThat(mutualFunds).hasSize(2)
        assertThat(mutualFunds[0].id).isEqualTo(1L)
        assertThat(mutualFunds[1].id).isEqualTo(3L)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when mutual fund with given id not found`() {
        // given
        val notExistingMutualFundId = 12L

        // when
        mutualFundRepository.findByIds(listOf(1L, 6L, notExistingMutualFundId))
    }

    @Test
    fun `should return two polish mutual funds`() {
        // when
        val mutualFunds = mutualFundRepository.findByType(MutualFund.Type.POLISH)

        // then
        assertThat(mutualFunds).hasSize(2)
        assertThat(mutualFunds.map { it.type }).containsOnly(MutualFund.Type.POLISH)
    }

    @Test
    fun `should return three foreign mutual funds`() {
        // when
        val mutualFunds = mutualFundRepository.findByType(MutualFund.Type.FOREIGN)

        // then
        assertThat(mutualFunds).hasSize(3)
        assertThat(mutualFunds.map { it.type }).containsOnly(MutualFund.Type.FOREIGN)
    }

    @Test
    fun `should return one mutual fund`() {
        // when
        val mutualFunds = mutualFundRepository.findByType(MutualFund.Type.MONEY)

        // then
        assertThat(mutualFunds).hasSize(1)
        assertThat(mutualFunds.first().type).isEqualTo(MutualFund.Type.MONEY)
    }

}