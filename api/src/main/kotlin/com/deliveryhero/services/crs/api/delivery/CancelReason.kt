package com.deliveryhero.services.crs.api.delivery

data class CancelReason(
        val name: String?,
        val isCustom: Boolean = false,
        val key: RejectReasonType = RejectReasonType.OTHER
)