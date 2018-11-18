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


        val investmentStyleMixture: Map<MutualFund.Type, Int> = investmentStyle.mutualFundMixture

        var latestBucketsMatchedToInvestmentStyleMixture: Map<MutualFund.Type, Long> = mutualFunds.map { it.type to 0L }.toMap()

        val mutualFundTypeBuckets: MutableMap<MutualFund.Type, Long> = latestBucketsMatchedToInvestmentStyleMixture.toMutableMap()

        for (capital in 0L..availableCapital) {

            val spentCapital: Double = max(mutualFundTypeBuckets.values.sum().toDouble(), 1.0)

            val mutualFundTypeBucketsMixture: Map<MutualFund.Type, Double> = mutualFunds
                    .map { it.type to (mutualFundTypeBuckets[it.type]!!.toDouble() / spentCapital) * 100.0 }
                    .toMap()

            if (mutualFundTypeBucketsMixture.all { bucketMixtureMatchesTargetMixture(it, investmentStyleMixture) }) {
                latestBucketsMatchedToInvestmentStyleMixture = mutualFundTypeBuckets.toMap()
            }

            val bucketsMixtureDistancesToTargetMixture: Map<MutualFund.Type, Double> = mutualFunds
                    .map { it.type to investmentStyleMixture[it.type]!!.toDouble() - mutualFundTypeBucketsMixture[it.type]!! }
                    .toMap()

            val farthestMutualFundTypeFromTargetMixture: MutualFund.Type = bucketsMixtureDistancesToTargetMixture.maxBy { it.value }!!.key
            val capitalInFarthestMutualFundTypeBucket = mutualFundTypeBuckets[farthestMutualFundTypeFromTargetMixture]!!
            mutualFundTypeBuckets[farthestMutualFundTypeFromTargetMixture] = capitalInFarthestMutualFundTypeBucket + 1
        }

        return latestBucketsMatchedToInvestmentStyleMixture
    }

    private fun bucketMixtureMatchesTargetMixture(it: Map.Entry<MutualFund.Type, Double>, targetMutualFundTypeBucketsShares: Map<MutualFund.Type, Int>) =
            it.value.toBigDecimal().compareTo(targetMutualFundTypeBucketsShares[it.key]!!.toBigDecimal()) == 0

    private fun createPortfolioItems(
            mutualFunds: List<MutualFund>,
            investedCapital: Long,
            mutualFundTypeBuckets: Map<MutualFund.Type, Long>
    ): List<InvestmentPortfolio.Item> {

        val mutualFundInvestments: MutableMap<MutualFund, Long> = splitCapitalPerMutualFund(
                mutualFunds, mutualFundTypeBuckets).toMutableMap()

        appendRemainingCapitalToFirstMutualFundWithType(mutualFundTypeBuckets, mutualFundInvestments)

        return mutualFundInvestments.map { (mutualFund, capitalInvestedInMutualFund) -> InvestmentPortfolio.Item(
                mutualFund = mutualFund,
                investedCapital = capitalInvestedInMutualFund,
                portfolioShare = ((capitalInvestedInMutualFund.toDouble() / investedCapital.toDouble()) * 100.0).round())
        }

    }

    private fun splitCapitalPerMutualFund(
            mutualFunds: List<MutualFund>,
            investedCapitalPerMutualFundType: Map<MutualFund.Type, Long>
    ): Map<MutualFund, Long> = mutualFunds.map { mutualFund ->
        val mutualFundsCountWithType: Int = mutualFunds.count { it.type == mutualFund.type }
        val investedCapitalInMutualFundType = investedCapitalPerMutualFundType[mutualFund.type]!!
        mutualFund to investedCapitalInMutualFundType / mutualFundsCountWithType
    }.toMap()

    private fun appendRemainingCapitalToFirstMutualFundWithType(
            investedCapitalPerMutualFundType: Map<MutualFund.Type, Long>,
            mutualFundInvestments: MutableMap<MutualFund, Long>
    ) {

        investedCapitalPerMutualFundType.map { (mutualFundType, investedCapitalInType) ->
            mutualFundType to investedCapitalInType - mutualFundInvestments.filterKeys { it.type == mutualFundType }.values.sum()
        }.forEach { (mutualFundType, remainingCapital) ->
            val firstMutualFundWithType = mutualFundInvestments.filterKeys { it.type == mutualFundType }.entries.first()
            mutualFundInvestments[firstMutualFundWithType.key] = firstMutualFundWithType.value + remainingCapital
        }
    }

    private fun Double.round(): Double = this.toBigDecimal().setScale(5, RoundingMode.HALF_EVEN).toDouble()

}