package com.github.tciesla.mutualfundsportfolioplanner.service

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentPortfolio
import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import com.github.tciesla.mutualfundsportfolioplanner.repository.InvestmentStyleRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import kotlin.math.max

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

        val targetMutualFundTypeBucketsShares: Map<MutualFund.Type, BigDecimal> = investmentStyle.mutualFundMixture

        val mutualFundTypeBuckets: MutableMap<MutualFund.Type, Long> = latestMatchedMutualFundTypeBuckets.toMutableMap()

        for (capital in 0L..availableCapital) {

            val spentCapital: Double = max(mutualFundTypeBuckets.values.sum().toDouble(), 1.0)

            val bucketsSharesInPortfolio: Map<MutualFund.Type, Double> = mutualFunds
                    .map { it.type to (mutualFundTypeBuckets[it.type]!!.toDouble() / spentCapital) * 100.0 }
                    .toMap()

            if (bucketsSharesInPortfolio.all { it.value.toBigDecimal().compareTo(targetMutualFundTypeBucketsShares[it.key]) == 0 }) {
                latestMatchedMutualFundTypeBuckets = mutualFundTypeBuckets.also { println("matched: $it") }.toMap()
            }

            val bucketsSharesDistancesToTargetShares: Map<MutualFund.Type, Double> = mutualFunds
                    .map { it.type to targetMutualFundTypeBucketsShares[it.type]!!.toDouble() - bucketsSharesInPortfolio[it.type]!! }
                    .toMap()

            val farthestMutualFundTypeFromTargetShare: MutualFund.Type = bucketsSharesDistancesToTargetShares.maxBy { it.value }!!.key
            val capitalInFarthestMutualFundTypeBucket = mutualFundTypeBuckets[farthestMutualFundTypeFromTargetShare]!!
            mutualFundTypeBuckets[farthestMutualFundTypeFromTargetShare] = capitalInFarthestMutualFundTypeBucket + 1
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