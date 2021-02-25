package com.josephm101.pricecalc

class QuickMath {
    companion object {
        fun map(x: Long, in_min: Long, in_max: Long, out_min: Long, out_max: Long): Long {
            return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min
        }
    }
}