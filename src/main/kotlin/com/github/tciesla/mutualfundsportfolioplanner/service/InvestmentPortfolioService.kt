package com.github.tciesla.mutualfundsportfolioplanner.service

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentPortfolio
import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentStyle
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import com.github.tciesla.mutualfundsportfolioplanner.repository.InvestmentStyleRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
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
                .also { println("investedCapital: $it") }

        val remainingCapital: Long = (availableCapital - investedCapital)
                .also { println("remainingCapital: $it") }

        val portfolioItems: List<InvestmentPortfolio.Item> = createPortfolioItems(
                selectedMutualFunds, investedCapital, investedCapitalPerMutualFundType)
                .also { println("portfolioItems: $it") }

        return InvestmentPortfolio(remainingCapital, portfolioItems)
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