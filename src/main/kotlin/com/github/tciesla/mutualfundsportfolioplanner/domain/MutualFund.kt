package com.github.tciesla.mutualfundsportfolioplanner.domain

data class MutualFund(val id: Long, val name: String, val type: Type) {

    enum class Type { POLISH, FOREIGN, MONEY }
}
