package com.github.tciesla.mutualfundsportfolioplanner.service

import com.github.tciesla.mutualfundsportfolioplanner.domain.InvestmentPortfolio
import com.github.tciesla.mutualfundsportfolioplanner.domain.MutualFund
import com.github.tciesla.mutualfundsportfolioplanner.repository.InvestmentStyleRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class InvestmentPortfolioService(val investmentStyleRepository: InvestmentStyleRepository) {

    fun createPortfolio(
            selectedMutualFunds: List<MutualFund>,
            investmentStyleName: String,
            investmentAmount: BigDecimal
    ) : InvestmentPortfolio {

        val investmentStyle = investmentStyleRepository.findByName(investmentStyleName)
            ?: throw IllegalArgumentException("investment style with name $investmentStyleName not found")

        val bucketsAmount = investmentStyle.mutualFundMixture.size
        var bucketTypes = investmentStyle.mutualFundMixture.map { it.key }.toTypedArray()
        var targetBucketShares = investmentStyle.mutualFundMixture.map { it.value.toDouble() }.toDoubleArray()
        var lastSuccessBucketsMatch = investmentStyle.mutualFundMixture.map { 0L }.toLongArray()
        var currentBucketsInvestment = lastSuccessBucketsMatch.copyOf()

        for (i in 1L..(investmentAmount.toLong() + 1L)) {

            // calculate shares array
            val shares = arrayOfNulls<Double>(bucketsAmount)
            for (bucket in 0..(bucketsAmount - 1)) {
                shares[bucket] = (currentBucketsInvestment[bucket] / (if ( i - 1L == 0L) 1 else i - 1).toDouble()) * 100.0
            }

            // check whether bucket shares matches model
            var matches = true
            for (bucket in 0..(bucketsAmount - 1)) {
                if (targetBucketShares[bucket].toBigDecimal().compareTo(shares[bucket]!!.toBigDecimal()) != 0) {
                    matches = false
                    break
                }
            }

            if (matches) {
                println("match: $i, ${currentBucketsInvestment.toList()}")
                for (bucket in 0..(bucketsAmount - 1)) {
                    lastSuccessBucketsMatch[bucket] = currentBucketsInvestment[bucket]
                }
            }
            // save last success if matched

            // calculate distances array
            val distances = arrayOfNulls<Double>(bucketsAmount)
            for (bucket in 0..(bucketsAmount - 1)) {
                distances[bucket] = targetBucketShares[bucket] - shares[bucket]!!
            }

            // find highest distance to target
            var maxDistanceBucket = 0
            var maxDistance = distances[0]
            for (bucket in 0..(bucketsAmount - 1)) {
                if (distances[bucket]!! > maxDistance!!) {
                    maxDistance = distances[bucket]
                    maxDistanceBucket = bucket
                }
            }

            // increase given bucket
            if (i.toBigDecimal() <= investmentAmount) currentBucketsInvestment[maxDistanceBucket]++
        }

        val investedAmount = lastSuccessBucketsMatch.sum().toBigDecimal()
        val notInvestedAmount = investmentAmount - investedAmount

        var items = listOf<InvestmentPortfolio.Item>()

        val result : Map<MutualFund, Long>  = selectedMutualFunds.map {
            it to 0L
        }.toMap()

        var list = mutableListOf<InvestmentPortfolio.Item>()

        for (bucket in 0..(bucketsAmount - 1)) {
            val mutualF = selectedMutualFunds
                    .filter { it.type == bucketTypes[bucket] }

            val mutualFundsInvestments = mutualF
                    .map { lastSuccessBucketsMatch[bucket] / mutualF.size }
                    .toLongArray()

            val remaining = lastSuccessBucketsMatch[bucket] - mutualFundsInvestments.sum()
            mutualFundsInvestments[0] += remaining

            for (x in 0..(mutualF.size-1)) {
                list.add(InvestmentPortfolio.Item(mutualF[x],
                        mutualFundsInvestments[x].toBigDecimal(),
                        mutualFundsInvestments[x].toBigDecimal().multiply(100.toBigDecimal()).divide(investedAmount)))
            }


        }

        println("${lastSuccessBucketsMatch.toList()}")

        return InvestmentPortfolio(
                investedAmount,
                notInvestedAmount,
                investmentStyle,
                list)
    }

}