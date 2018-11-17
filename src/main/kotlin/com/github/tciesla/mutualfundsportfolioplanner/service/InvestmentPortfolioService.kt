package com.github.tciesla.mutualfundsportfolioplanner.service

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentPortfolio
import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import com.github.tciesla.mutualfundsportfolioplanner.repository.InvestmentStyleRepository
import org.springframework.stereotype.Service

@Service
class InvestmentPortfolioService(val investmentStyleRepository: InvestmentStyleRepository) {

    fun createPortfolio(
            selectedMutualFunds: List<MutualFund>,
            investmentStyleName: String,
            availableCapital: Long
    ) : InvestmentPortfolio {

        val investmentStyle = investmentStyleRepository.findByName(investmentStyleName)
            ?: throw IllegalArgumentException("investment style with name $investmentStyleName not found")

        val investedCapitalPerMutualFundType: Map<MutualFund.Type, Long> = splitAvailableCapitalByMutualFundType(
                selectedMutualFunds, availableCapital, investmentStyle)

        val investedCapital: Long = investedCapitalPerMutualFundType.values.sum()

        val remainingCapital: Long = availableCapital - investedCapital

        val portfolioItems: List<InvestmentPortfolio.Item> = createPortfolioItems(
                selectedMutualFunds, investedCapital, investedCapitalPerMutualFundType)

        return InvestmentPortfolio(
                investedCapital.toBigDecimal(),
                remainingCapital.toBigDecimal(),
                investmentStyle,
                portfolioItems)
    }

    private fun splitAvailableCapitalByMutualFundType(
            mutualFunds: List<MutualFund>,
            availableCapital: Long,
            investmentStyle: InvestmentStyle
    ): Map<MutualFund.Type, Long> {

        var latestMatchedMutualFundTypeBuckets: Map<MutualFund.Type, Long> = mutualFunds.map { it.type to 0L }.toMap()

        val currentMutualFundTypeBuckets: MutableMap<MutualFund.Type, Long> = latestMatchedMutualFundTypeBuckets.toMutableMap()

        for (capital in 1L..(availableCapital + 1L)) {

            // calculates current portfolio shares
            val shares2 = mutualFunds
                    .map { it.type to currentMutualFundTypeBuckets[it.type]!!.toDouble() / ((if (capital - 1L == 0L) 1 else capital - 1).toDouble()) * 100.0 }
                    .toMap()

            // check whether bucket shares matches model
            if (shares2.all { it.value.toBigDecimal().compareTo(investmentStyle.mutualFundMixture[it.key]) == 0 }) {
                latestMatchedMutualFundTypeBuckets = currentMutualFundTypeBuckets.toMutableMap()
            }

            // save last success if matched

            // calculate distances array
            var distances2 = mutualFunds
                    .map { it.type to investmentStyle.mutualFundMixture[it.type]!!.toDouble() - shares2[it.type]!! }
                    .toMap()

            // find highest distance to target
            // increase given bucket
            if (capital <= availableCapital) {
                val bigDecimal = currentMutualFundTypeBuckets[distances2.maxBy { it.value }!!.key]
                currentMutualFundTypeBuckets[distances2.maxBy { it.value }!!.key] = bigDecimal!! + 1
            }
        }
        return latestMatchedMutualFundTypeBuckets
    }

    private fun createPortfolioItems(selectedMutualFunds: List<MutualFund>, investedAmount: Long, mutualFundTypeBuckets: Map<MutualFund.Type, Long>): List<InvestmentPortfolio.Item> {
        val mapX = selectedMutualFunds.map {
            val mutualFundType = it.type
            it to mutualFundTypeBuckets[mutualFundType]!! / selectedMutualFunds.filter { it.type == mutualFundType }.count()
        }.toMap().toMutableMap()

        mutualFundTypeBuckets
                .map {
                    val type = it.key
                    val toBigDecimal = mapX.filter { it.key.type == type }.values.sum()
                    it.key to it.value - toBigDecimal
                }.filter { it.second > 0L }
                .forEach {
                    val type = it.first
                    val first = mapX.filter { it.key.type == type }.entries.first()
                    mapX[first.key] = mapX[first.key]!! + it.second
                }

        val portfolioItems = mapX.map {
            InvestmentPortfolio.Item(it.key, it.value.toBigDecimal(), ((it.value.toBigDecimal().divide(investedAmount.toBigDecimal())) * 100.00.toBigDecimal()))
        }
        return portfolioItems
    }

}