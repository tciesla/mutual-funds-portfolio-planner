package com.github.tciesla.mutualfundsportfolioplanner.domain

import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.POLISH
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MutualFundTest {

    @Test
    fun `should create example mutual fund`() {
        // when
        val mutualFund = MutualFund(ID, NAME, type = POLISH)

        // then
        assertThat(mutualFund.id).isEqualTo(ID)
        assertThat(mutualFund.name).isEqualTo(NAME)
        assertThat(mutualFund.type).isEqualTo(POLISH)
    }

    @Test
    fun `three different mutual fund types should be available`() {
        // then
        assertThat(MutualFund.Type.values()).hasSize(3)
    }

    companion object {
        const val ID = 25L
        const val NAME = "Polish Fund"
    }

}