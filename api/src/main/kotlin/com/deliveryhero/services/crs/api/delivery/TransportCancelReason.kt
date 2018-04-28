package com.deliveryhero.services.crs.api.delivery

import java.time.Instant

data class TransportCancelReason(
        val name: String? = null,
        val isCustom: Boolean = false,
        val comment: String?,
        val time: Instant,
        val key: TransportCancelReasonType = TransportCancelReasonType.OTHER
)