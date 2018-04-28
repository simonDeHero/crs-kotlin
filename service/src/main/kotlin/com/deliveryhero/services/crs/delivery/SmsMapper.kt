package com.deliveryhero.services.crs.delivery

import com.deliveryhero.services.crs.api.delivery.Sms
import com.deliveryhero.services.crs.api.delivery.SmsType
import com.deliveryhero.services.legacy.webkick.api.LegacySmsEvent
import com.deliveryhero.services.legacy.webkick.api.LegacySmsNotificationType
import com.google.common.collect.ImmutableSet
import org.slf4j.LoggerFactory
import java.util.*

/**
 * A mapper for information regarding SMS.
 */
class SmsMapper protected constructor() {
    companion object {

        private val LOGGER = LoggerFactory.getLogger(SmsMapper::class.java)

        private val LEGACY_SMS_DELIVERY_STARTED_TYPES = ImmutableSet.of(
                LegacySmsNotificationType.CUSTOMER_DELIVERY_STARTED_LT,
                LegacySmsNotificationType.CUSTOMER_DELIVERY_STARTED_NO_LT,
                LegacySmsNotificationType.CUSTOMER_DELIVERY_STARTED_TRANSPORT)
        private val LEGACY_SMS_DELAYED_TYPES = ImmutableSet
                .of(LegacySmsNotificationType.DELAYED)

        /**
         * Maps a list of legacy SMS events to 1 CRS SMS. If the given `smsEvents` is `null`, then `null`
         * is returned. Only sms are mapped for the following legacy types/type-classes, and the first one found in this
         * order is returned. If none for these types/type-classes is found, `null` is returned.
         *
         *
         *  * [LegacySmsNotificationType.CUSTOMER_DELIVERY_STARTED_LT],
         * [LegacySmsNotificationType.CUSTOMER_DELIVERY_STARTED_NO_LT],
         * [LegacySmsNotificationType.CUSTOMER_DELIVERY_STARTED_TRANSPORT] will be mapped to
         * [SmsType.DELIVERY_STARTED]. the `eta` and `timestamp` of first one found
         * will be used.
         *  * [LegacySmsNotificationType.DELAYED] will be mapped to [SmsType.DELAYED]
         *
         *
         * @param smsEvents the legacy sms events to be mapped
         *
         * @return the most important mapped sms, according to prioritization above
         */
        fun mapToMostImportantOneOrNull(smsEvents: List<LegacySmsEvent>?): Sms? {

            if (smsEvents == null) {
                return null
            }

            val smsEvent = findFirstByTypes(smsEvents, LEGACY_SMS_DELIVERY_STARTED_TYPES)
                    .orElse(findFirstByTypes(smsEvents, LEGACY_SMS_DELAYED_TYPES)
                            .orElse(null)) ?: return null

            return mapOrNull(smsEvent)
        }

        private fun findFirstByTypes(smsEvents: List<LegacySmsEvent>,
                                     types: Collection<LegacySmsNotificationType>): Optional<LegacySmsEvent> {
            return smsEvents.stream().filter { smsEvent -> types.contains(smsEvent.event) }.findFirst()
        }

        /**
         * Maps the `smsEvent` to a [Sms]. If its `event` is not mappable by [SmsType], then
         * `null` is returned.
         *
         * @param smsEvent the sms event to be mapped
         *
         * @return a [Sms] if it can be mapped by event/type, otherwise `null`
         */
        private fun mapOrNull(smsEvent: LegacySmsEvent): Sms? {

            val type = smsEvent.event
            val smsType = mapOrNull(type)
            if (smsType == null) {
                LOGGER.warn("unsupported sms event type $type. skipping this sms event: $smsEvent")
                return null
            }

            return Sms(
                    smsEvent.timestamp,
                    smsType,
                    smsEvent.eta)
        }

        /**
         * Maps legacy sms types to CRS. If the given `type` is `null`, then `null` is returned. If the
         * given `type` cannot be mapped, then `null` is returned.
         *
         * @param type the type to be mapped
         *
         * @return the mapped [SmsType] or null
         */
        private fun mapOrNull(type: LegacySmsNotificationType?): SmsType? {

            if (type == null) {
                return null
            }

            return when (type) {
                LegacySmsNotificationType.CUSTOMER_DELIVERY_STARTED_LT,
                LegacySmsNotificationType.CUSTOMER_DELIVERY_STARTED_NO_LT,
                LegacySmsNotificationType.CUSTOMER_DELIVERY_STARTED_TRANSPORT -> SmsType.DELIVERY_STARTED

                LegacySmsNotificationType.DELAYED -> SmsType.DELAYED

                else -> null
            }
        }
    }
}
