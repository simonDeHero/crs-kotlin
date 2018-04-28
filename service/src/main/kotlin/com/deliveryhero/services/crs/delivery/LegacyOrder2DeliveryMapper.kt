package com.deliveryhero.services.crs.delivery

import com.deliveryhero.services.crs.api.delivery.*
import com.deliveryhero.services.legacy.webkick.api.*
import com.google.common.base.Splitter
import com.google.common.base.Strings
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors

/**
 * This class maps [LegacyOrder]s to [Delivery]s.
 *
 * @author vguna
 */
class LegacyOrder2DeliveryMapper
/**
 * Creates a default mapper instance.
 */
protected constructor() {
    companion object {

        private val LOG = LoggerFactory.getLogger(LegacyOrder2DeliveryMapper::class.java)

        private val COMMENT_ARTICLE = 9999

        private val DEFAULT_ACCEPT_TIMES: LinkedHashSet<Int>

        init {
            // CHECKSTYLE:OFF MagicNumber
            DEFAULT_ACCEPT_TIMES = LinkedHashSet()
            var i = 25
            while (i <= 90) {
                DEFAULT_ACCEPT_TIMES.add(i)
                i += 5
            }
            // CHECKSTYLE:ON MagicNumber
        }

        /**
         * Maps the given [List] of [LegacyOrder]s to [Delivery]s.
         *
         * @param orders the orders to map.
         * @return the [List] of mapped [Delivery]s.
         */
        fun toDeliveries(orders: List<LegacyOrder>): List<Delivery> {

            val deliveries = ArrayList<Delivery>()
            for (order in orders) {
                deliveries.add(toDelivery(order))
            }

            return deliveries
        }

        /**
         * Maps the given [LegacyOrder] to a [Delivery].
         *
         * @param order the [LegacyOrder] to map.
         * @return the mapped [Delivery].
         */
        fun toDelivery(order: LegacyOrder): Delivery {
            return toDelivery(order, null)
        }

        /**
         * Maps the given [LegacyOrder] and [LegacyOrderDetails] to a [Delivery].
         *
         * @param order the [LegacyOrder] to map.
         * @param details the [LegacyOrderDetails] to map. May be null.
         * @return the mapped [Delivery].
         */
        // CHECKSTYLE:OFF MethodLength
        fun toDelivery(order: LegacyOrder, details: LegacyOrderDetails?) =

                Delivery(
                        order.id,
                        order.timestamp,
                        buildState(order),
                        buildDispatchState(order),
                        buildTrackingStateType(order),
                        order.deliveryPlatform.id.toString(),
                        order.externalRestaurantId,
                        order.externalId,
                        order.__test,
                        order.preorder,
                        order.minimumAcceptTime != null && order.maximumAcceptTime != null,
                        buildTransport(order, details),
                        buildAllowedAcceptTimes(order),
                        order.deliveredToPosTime,
                        determineDeliverAt(order),
                        order.expiresAt,
                        order.acceptedTime,
                        buildCustomer(order),
                        buildDeliveryAddress(order),
                        buildPayment(order),
                        order.orderComment,
                        buildOrderItems(order.items),
                        buildFees(order.fees),
                        buildDiscounts(order.discounts),
                        buildTaxes(order),
                        buildRejectReason(order, details),
                        buildCancelReason(order, details),
                        details?.canVoid ?: false,
                        order.deliverAtBeforeDelay,
                        order.canDelay,
                        SmsMapper.mapToMostImportantOneOrNull(details?.smsEvents),
                        order.corporate,
                        order.shortCode
                )

        private fun buildCancelReason(legacyOrder: LegacyOrder, details: LegacyOrderDetails?) =
                if (details != null
                        && (legacyOrder.state == LegacyDeliveryStateType.CANCELLED
                                || legacyOrder.state == LegacyDeliveryStateType.CANCELLED_BY_PLATFORM
                                || legacyOrder.state == LegacyDeliveryStateType.CANCELLED_BY_TRANSPORT)) {
                    CancelReason(comment = details.rejectReason, time = details.rejectedTime)
                } else {
                    null
                }

        private fun buildRejectReason(legacyOrder: LegacyOrder, details: LegacyOrderDetails?) =
                if (details != null && legacyOrder.state == LegacyDeliveryStateType.REJECTED) {
                    RejectReason(comment = details.rejectReason, time = details.rejectedTime)
                } else {
                    null
                }

        private fun buildAllowedAcceptTimes(order: LegacyOrder): LinkedHashSet<Int>? {

            var acceptTimes: LinkedHashSet<Int>? = null

            if (order.state == LegacyDeliveryStateType.NEW) {
                if (order.minimumAcceptTime != null) {
                    acceptTimes = LinkedHashSet()
                    acceptTimes.add(order.minimumAcceptTime)
                    // both minimum and maximum accept time is set for guaranteed delivery
                    if (order.maximumAcceptTime != null) {
                        acceptTimes.add(order.maximumAcceptTime)
                    }
                }

                if (acceptTimes == null || acceptTimes.isEmpty()) {
                    acceptTimes = DEFAULT_ACCEPT_TIMES
                }
            }

            return acceptTimes
        }

        private fun determineDeliverAt(order: LegacyOrder) =
                if (order.state == LegacyDeliveryStateType.NEW
                        && order.minimumAcceptTime != null
                        && order.maximumAcceptTime != null) {
                    Instant.now().plus(order.maximumAcceptTime.toLong(), ChronoUnit.MINUTES)
                } else {
                    null
                }

        private fun buildTransport(legacyOrder: LegacyOrder, legacyOrderDetails: LegacyOrderDetails?): Transport {

            var transportType: TransportType? = null;

            if (legacyOrder.dispatchState != null && legacyOrder.dispatchState == LegacyDispatchStateType.PICKUP) {
                transportType = TransportType.PICKUP_CUSTOMER
            }
            if (legacyOrder.externalDelivery) {
                transportType = TransportType.PICKUP_LOGISTICS
            }
            if (!Strings.isNullOrEmpty(legacyOrder.driver)) {
                transportType = TransportType.RESTAURANT_DELIVERY
            }

            // FIXME ?
            transportType = transportType ?: TransportType.RESTAURANT_DELIVERY

            val transportCancellation = legacyOrder.cancelled
            var transportCancelReason: TransportCancelReason? = null
            if (legacyOrder.trackingState === LegacyTrackingStateType.CANCELLED && transportCancellation != null) {
                transportCancelReason = TransportCancelReason(
                        key = TransportCancelReasonType.getById(transportCancellation.reason)
                                ?: TransportCancelReasonType.OTHER,
                        comment = transportCancellation.comment,
                        time = transportCancellation.timestamp
                )
            }

            return Transport(
                    transportType,
                    legacyOrder.transportName,
                    legacyOrderDetails?.transportJobId,
                    legacyOrder.driver,
                    legacyOrder.driverName,
                    legacyOrder.transportPickupTime,
                    legacyOrder.dispatchedTime,
                    legacyOrder.deliverAt,
                    transportCancelReason
            )
        }

        private fun buildTrackingStateType(legacyOrder: LegacyOrder) =
                if (legacyOrder.trackingState == LegacyTrackingStateType.ITEMS_PURCHASED) {
                    TrackingStateType.DRIVER_PICKED_UP
                } else {
                    try {
                        TrackingStateType.valueOf(legacyOrder.trackingState.name)
                    } catch (e: IllegalArgumentException) {
                        LOG.warn("Unknown LegacyTrackingStateType encountered: {}. "
                                + "Please check for newly introduced icash tracking states.",
                                legacyOrder.trackingState)
                        TrackingStateType.UNKNOWN
                    }
                }

        private fun buildDispatchState(legacyOrder: LegacyOrder) =
                try {
                    DispatchStateType.valueOf(legacyOrder.dispatchState.name)
                } catch (e: IllegalArgumentException) {
                    LOG.warn("Unknown LegacyDispatchStateType encountered: {}. "
                            + "Please check for newly introduced icash dispatch states.",
                            legacyOrder.dispatchState)
                    DispatchStateType.UNKNOWN
                }


        private fun buildState(legacyOrder: LegacyOrder) =
                try {
                    DeliveryStateType.valueOf(legacyOrder.state.name)
                } catch (e: IllegalArgumentException) {
                    LOG.warn("Unknown LegacyDeliveryStateType encountered: {}. "
                            + "Please check for newly introduced icash delivery states.",
                            legacyOrder.state)
                    DeliveryStateType.UNKNOWN
                }


        private fun buildDeliveryAddress(legacyOrder: LegacyOrder) =
                DeliveryAddress(
                        legacyOrder.address.customerAddressId,
                        legacyOrder.contact?.company,
                        legacyOrder.address.street,
                        legacyOrder.address.zip,
                        legacyOrder.address.city,
                        legacyOrder.address.suburb,
                        legacyOrder.address.block,
                        legacyOrder.address.floor,
                        legacyOrder.address.door,
                        legacyOrder.address.building,
                        legacyOrder.address.info,
                        legacyOrder.address.latitude,
                        legacyOrder.address.longitude,
                        legacyOrder.address.distance,
                        legacyOrder.address.geocodedManually
                )

        private fun buildOrderItems(orderItems: List<LegacyOrderItem>?): List<OrderItem> {
            if (orderItems == null) {
                return listOf()
            } else {

                // TODO find out, why this has problems
//                return orderItems.stream()
//                        .map { convertTo(it, null) }
//                        .filter { it != null }
//                        .collect(Collectors.toList())

                val items = mutableListOf<OrderItem>()
                orderItems.forEach({
                    val orderItem = convertTo(it, null)
                    if (orderItem != null) {
                        items.add(orderItem)
                    }
                })
                return items;
            }
        }

        private fun buildPayment(legacyOrder: LegacyOrder) =
                Payment(
                        legacyOrder.paid,
                        legacyOrder.currency,
                        legacyOrder.currencySymbol,
                        legacyOrder.total,
                        legacyOrder.minimumOrderValue,
                        PaymentType.fromNameOrUnknown(legacyOrder.paymentType.name),
                        legacyOrder.paymentMethod
                )

        private fun buildCustomer(legacyOrder: LegacyOrder) =
                if (legacyOrder.contact == null) {
                    Customer(legacyOrder.customerId)
                } else {
                    Customer(
                            legacyOrder.customerId,
                            legacyOrder.contact.phone,
                            legacyOrder.contact.customerContactId,
                            legacyOrder.contact.firstName,
                            legacyOrder.contact.lastName,
                            null,
                            legacyOrder.contact.company,
                            legacyOrder.contact.email ?: ""
                    )
                }


        private fun buildTaxes(legacyOrder: LegacyOrder): List<PriceModifier> {
            val taxes = ArrayList<PriceModifier>()
            if (legacyOrder.taxes != null) {
                taxes.addAll(convertTaxes(legacyOrder.taxes, false))
            }
            if (legacyOrder.includedTaxes != null) {
                taxes.addAll(convertTaxes(legacyOrder.includedTaxes, true))
            }
            return taxes;
        }

        private fun convertTaxes(legacyTaxes: List<LegacyOrderTax>, isIncludedInPrice: Boolean) =
                legacyTaxes.stream()
                        .map { legacyTax -> PriceModifier(legacyTax.name, legacyTax.value, isIncludedInPrice) }
                        .collect(Collectors.toList())

        private fun buildFees(legacyTaxes: List<LegacyOrderFee>) =
                legacyTaxes.stream()
                        .map { legacyFee -> PriceModifier(legacyFee.name, legacyFee.value) }
                        .collect(Collectors.toList())

        private fun buildDiscounts(legacyTaxes: List<LegacyOrderDiscount>) =
                legacyTaxes.stream()
                        .map { legacyDiscount -> PriceModifier(legacyDiscount.name, legacyDiscount.value) }
                        .collect(Collectors.toList())


        private fun convertTo(legacyOrderItem: LegacyOrderItem, parent: OrderItem?): OrderItem? {

            if (legacyOrderItem.article == COMMENT_ARTICLE && parent != null) {
                // comments
                LOG.debug("Converting article (9999) to comment - $legacyOrderItem")
                parent.comment = legacyOrderItem.name
                return null
            }

            var name = filter(legacyOrderItem.name, "?: ")
            name = filter(name, "!: ")

            val orderItem = OrderItem(
                    legacyOrderItem.amount,
                    name,
                    legacyOrderItem.groupName,
                    legacyOrderItem.plu,
                    legacyOrderItem.itemCode,
                    null,
                    legacyOrderItem.price,
                    legacyOrderItem.total
            )

            if (parent != null) {
                if (parent.modifiers == null) {
                    parent.modifiers = mutableListOf()
                }
                parent.modifiers!!.add(orderItem)
            }

            if (legacyOrderItem.modifiers != null) {
                for (childOrderItem in legacyOrderItem.modifiers) {
                    convertTo(childOrderItem, orderItem)
                }
            }

            return orderItem
        }

        private fun filter(name: String, separator: String): String {
            var lastToken = name
            val strings = Splitter.on(separator).split(name)
            for (string in strings) {
                lastToken = string
            }

            return lastToken
        }
    }
}
