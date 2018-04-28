package com.deliveryhero.services.crs.api.delivery

import java.time.Instant
import java.util.LinkedHashSet

data class Delivery(
        val timestamp: Instant,
        val state: DeliveryStateType,
        val dispatchStateType: DispatchStateType = DispatchStateType.UNDEFINED,
        val trackingStateType: TrackingStateType = TrackingStateType.NOT_TRACKED,
        val restaurantId: String,
        val deliveryPlatform: String,
        val externalRestaurantId: String,
        val externalId: String,
        val test: Boolean = false,
        val preorder: Boolean,
        val guaranteed: Boolean,
        val transport: Transport,
        val allowedAcceptTimes: LinkedHashSet<Int>?,
        val seenAt: Instant? = null,
        val deliverAt: Instant,
        val expiresAt: Instant,
        val acceptedAt: Instant? = null,
        val customer: Customer,
        val address: DeliveryAddress,
        val payment: Payment,
        val comment: String? = null,
        val items: List<OrderItem>,
        val fees: List<PriceModifier>? = null,
        val discounts: List<PriceModifier>? = null,
        val taxes: List<PriceModifier>? = null,
        val rejectReason: RejectReason? = null,
        val cancelReason: CancelReason? = null,
        val canVoid: Boolean,
        val deliverAtBeforeDelay: Instant? = null,
        val canDelay: Boolean,
        val smsSent: Sms? = null,
        val corporate: Boolean,
        val shortCode: String
)