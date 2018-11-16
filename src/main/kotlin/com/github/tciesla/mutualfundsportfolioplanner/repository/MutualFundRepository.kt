package com.github.tciesla.mutualfundsportfolioplanner.repository

import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.FOREIGN
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.MONEY
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund.Type.POLISH
import org.springframework.stereotype.Repository

@Repository
class MutualFundRepository {

    private val mutualFunds = mapOf(
            1L to MutualFund(id = 1L, name = "Fundusz Polski 1", type = POLISH),
            2L to MutualFund(id = 2L, name = "Fundusz Polski 2", type = POLISH),
            3L to MutualFund(id = 3L, name = "Fundusz Zagraniczny 1", type = FOREIGN),
            4L to MutualFund(id = 4L, name = "Fundusz Zagraniczny 2", type = FOREIGN),
            5L to MutualFund(id = 5L, name = "Fundusz Zagraniczny 3", type = FOREIGN),
            6L to MutualFund(id = 6L, name = "Fundusz Pieniężny 1", type = MONEY)
    )

    fun findAll() : List<MutualFund> = mutualFunds.values.toList()

    fun findByIds(mutualFundIds : List<Long>) : List<MutualFund> = mutualFundIds.map {
        mutualFunds[it] ?: throw IllegalArgumentException("mutual fund with id $it not found")
    }

    fun findByType(type: MutualFund.Type) : List<MutualFund> = mutualFunds.values.filter { it.type == type }

}