package com.github.tciesla.mutualfundsportfolioplanner.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MutualFundTest {

    @Test
    fun `should create mutual fund`() {
        // when
        val mutualFund = MutualFund(expectedId, expectedName, expectedType)

        // then
        assertThat(mutualFund.id).isEqualTo(expectedId)
        assertThat(mutualFund.name).isEqualTo(expectedName)
        assertThat(mutualFund.type).isEqualTo(expectedType)
    }

    @Test
    fun `three different mutual fund types should be available`() {
        // when
        val mutualFundTypes = MutualFund.Type.values()

        // then
        assertThat(mutualFundTypes).hasSize(3)
    }

    companion object {
        const val expectedId = 25L
        const val expectedName = "Polish Fund $expectedId"
        val expectedType = MutualFund.Type.POLISH
    }

}