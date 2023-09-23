package com.littlelemon.myapplication

import kotlin.random.Random

class Christmas {
    companion object {
        private var discount = 0.0
        private const val SPIN_TIMES_DEFAULT = 3
        fun reset() {
            discount = 0.0
        }
        fun spinDiscountWheel(
            int: Int = SPIN_TIMES_DEFAULT,
            handleTempResult: (String) -> Unit
        ): Double {
            repeat(int) {
                val tempDiscount = Random.nextInt(10).toDouble()
                if (tempDiscount > discount) {
                    discount = tempDiscount
                }
                handleTempResult("You got $tempDiscount discount in the attempt ${it+1}!\nThe best  discount is $discount")
            }
            return discount
        }
    }
}