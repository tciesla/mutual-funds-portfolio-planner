package com.github.tciesla.mutualfundsportfolioplanner.service

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentPortfolio
import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import java.math.RoundingMode
import kotlin.math.max

class InvestmentPortfolioService {

    fun createPortfolio(
            selectedMutualFunds: List<MutualFund>,
            investmentStyle: InvestmentStyle,
            availableCapital: Long
    ) : InvestmentPortfolio {

        validateSelectedMutualFunds(selectedMutualFunds, investmentStyle)

        val investedCapitalPerMutualFundType: Map<MutualFund.Type, Long> = splitAvailableCapitalPerMutualFundType(
                selectedMutualFunds, availableCapital, investmentStyle)

        val investedCapital: Long = investedCapitalPerMutualFundType.values.sum()
                .also { println("investedCapital: $it") }

        val remainingCapital: Long = (availableCapital - investedCapital)
                .also { println("remainingCapital: $it") }

        val portfolioItems: List<InvestmentPortfolio.Item> = createPortfolioItems(
                selectedMutualFunds, investedCapital, investedCapitalPerMutualFundType)
                .also { println("portfolioItems:\n${it.joinToString("\n")}") }

        return InvestmentPortfolio(remainingCapital, portfolioItems)
    }

    private fun validateSelectedMutualFunds(selectedMutualFunds: List<MutualFund>, investmentStyle: InvestmentStyle) {
        if (selectedMutualFunds.isEmpty()) {
            throw IllegalArgumentException("at least one mutual fund must be selected")
        }

        if (!selectedMutualFunds.map { it.type }.containsAll(investmentStyle.mutualFundMixture.keys)) {
            throw IllegalArgumentException("at least one mutual fund with type required by investment style must be selected")
        }
    }

    private fun splitAvailableCapitalPerMutualFundType(
            mutualFunds: List<MutualFund>,
            availableCapital: Long,
            investmentStyle: InvestmentStyle
    ): Map<MutualFund.Type, Long> {

        var latestMatchedMutualFundTypeBuckets: Map<MutualFund.Type, Long> = mutualFunds.map { it.type to 0L }.toMap()

        val targetMutualFundTypeBucketsShares: Map<MutualFund.Type, Int> = investmentStyle.mutualFundMixture

        val mutualFundTypeBuckets: MutableMap<MutualFund.Type, Long> = latestMatchedMutualFundTypeBuckets.toMutableMap()

        for (capital in 0L..availableCapital) {

            val spentCapital: Double = max(mutualFundTypeBuckets.values.sum().toDouble(), 1.0)

            val bucketsSharesInPortfolio: Map<MutualFund.Type, Double> = mutualFunds
                    .map { it.type to (mutualFundTypeBuckets[it.type]!!.toDouble() / spentCapital) * 100.0 }
                    .toMap()

            if (bucketsSharesInPortfolio.all { it.value.toBigDecimal().compareTo(targetMutualFundTypeBucketsShares[it.key]!!.toBigDecimal()) == 0 }) {
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

    private fun createPortfolioItems(
            mutualFunds: List<MutualFund>,
            investedCapital: Long,
            mutualFundTypeBuckets: Map<MutualFund.Type, Long>
    ): List<InvestmentPortfolio.Item> {

        val splitCapitalPerMutualFund: MutableMap<MutualFund, Long> = mutualFunds.map { mutualFund ->
            mutualFund to mutualFundTypeBuckets[mutualFund.type]!! / mutualFunds.count { it.type == mutualFund.type }
        }.toMap().toMutableMap()


        mutualFundTypeBuckets.map { (mutualFundType, capitalInBucket) ->
            mutualFundType to capitalInBucket - splitCapitalPerMutualFund.filterKeys { it.type == mutualFundType }.values.sum()
        }.forEach { (mutualFundType, remainingCapital) ->
            val firstMutualFundWithType = splitCapitalPerMutualFund.filterKeys { it.type == mutualFundType }.entries.first()
            splitCapitalPerMutualFund[firstMutualFundWithType.key] = firstMutualFundWithType.value + remainingCapital
        }

        return splitCapitalPerMutualFund.map { (mutualFund, capitalInvestedInMutualFund) -> InvestmentPortfolio.Item(
                mutualFund = mutualFund,
                investedCapital = capitalInvestedInMutualFund,
                portfolioShare = ((capitalInvestedInMutualFund.toDouble() / investedCapital.toDouble()) * 100.0).round())
        }

    }

    private fun Double.round(): Double = this.toBigDecimal().setScale(5, RoundingMode.HALF_EVEN).toDouble()

}