package com.deliveryhero.services.crs.api

internal interface HashableEnum<E : Enum<*>> {

    fun toHashCode(): Int {
        var result = javaClass.name.hashCode()
        val that = this as E
        result = 31 * result + that.name.hashCode()
        result = 31 * result + that.ordinal + 1
        return result
    }
}
