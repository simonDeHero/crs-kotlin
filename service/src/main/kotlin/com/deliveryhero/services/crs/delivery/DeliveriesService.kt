package com.deliveryhero.services.crs.delivery

import com.deliveryhero.services.crs.api.delivery.Delivery
import com.deliveryhero.services.crs.api.delivery.DeliveryState
import com.deliveryhero.services.crs.api.delivery.DeliveryStateType
import com.deliveryhero.services.crs.api.delivery.RejectReasonType
import com.deliveryhero.services.crs.webkick.WebkickApiFactory
import com.deliveryhero.services.legacy.webkick.api.*
import com.deliveryhero.services.legacy.webkick.api.action.Accept
import com.deliveryhero.services.legacy.webkick.api.action.Reject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import javax.ws.rs.BadRequestException
import javax.ws.rs.NotFoundException

@Service
class DeliveriesService(webkickApiFactory: WebkickApiFactory) {

    private var operatorApi: WebkickOperatorApi = webkickApiFactory.operatorApi

    companion object {
        private const val STATUS_INVALID_REQUEST = "INVALID_REQUEST"
        private const val STATUS_REQUEST_DENIED = "REQUEST_DENIED"
        private const val STATUS_CONFLICT = "CONFLICT"
        private const val STATUS_NOT_FOUND = "NOT_FOUND"
        private val LOG = LoggerFactory.getLogger(DeliveriesService::class.java)
    }

    fun getById(id: String): Delivery {

        val deliveries: List<LegacyOrder>?
        try {
            deliveries = operatorApi.findDeliveryById(null, id)
        } catch (e: BadRequestException) {
            // currently only the case when the given id is not a UUID. then Icash cannot parse it.
            throw NotFoundException()
        }

        // TODO check if "null" can be returned in the first place
        if (deliveries == null || deliveries.isEmpty()) {
            throw NotFoundException()
        }

        val deliveryDetails = operatorApi.findDeliveryDetails(null, id)

        return LegacyOrder2DeliveryMapper.toDelivery(deliveries[0], deliveryDetails)
    }

    fun getNewDeliveriesIds(): List<String> {

        val newDeliveryResult = operatorApi.checkForDeliveries(null)
        return if (newDeliveryResult == null) {
            listOf()
        } else {
            extractNewDeliveryIds(newDeliveryResult)
        }
    }

    private fun extractNewDeliveryIds(newDeliveryResult: NewDeliveryResult): List<String> {

        if (newDeliveryResult.isEmpty) {
            return ArrayList()
        }

        val ids = ArrayList<String>()
        if (newDeliveryResult.deliveryIds != null) {
            ids.addAll(newDeliveryResult.deliveryIds)
        }
        if (newDeliveryResult.expiringDeliveryIds != null) {
            ids.addAll(newDeliveryResult.expiringDeliveryIds)
        }

        return ids
    }

    fun changeState(id: String, deliveryState: DeliveryState): Delivery {

        assertUUIDOrNotFoundException(id)

        return when (deliveryState.state) {

            DeliveryStateType.ACCEPTED -> accept(id, deliveryState)
            DeliveryStateType.REJECTED -> performActionWithReason(id, deliveryState, DeliveryStateType.REJECTED)
                { Reject(mapReason(deliveryState.reason!!), deliveryState.comment) }
                //TODO
//                else -> throw ValidationException(DELIVERY_VALIDATION_ERROR,
//                        SimpleConstraintViolation.custom(VALID_CHANGE_STATE, deliveryState, "state", deliveryState.state))
            else -> throw IllegalArgumentException("replace with validationException")
        }
    }

    private fun assertUUIDOrNotFoundException(id: String) =
            try {
                UUID.fromString(id)
            } catch (e: IllegalArgumentException) {
                throw NotFoundException()
            }

    private fun accept(deliveryId: String, deliveryState: DeliveryState): Delivery {

        // deliveryTime is only mandatory for pickup orders
        if (deliveryState.deliveryTime != null && deliveryState.deliveryTime!! < 0) {
            //TODO
//            throw ValidationException(DELIVERY_VALIDATION_ERROR,
//                    SimpleConstraintViolation.notNegative(deliveryState, "deliveryTime", deliveryState.deliveryTime))
        }

        val action = Accept(deliveryState.deliveryTime)

        val status = changeStateAndHandleError(deliveryId, action, DeliveryStateType.ACCEPTED)
        if (STATUS_INVALID_REQUEST == status.status && status.message.contains("deliveryTime")
                && status.message.contains("null")) {
            //TODO
//            throw ValidationException(DELIVERY_VALIDATION_ERROR,
//                    SimpleConstraintViolation.notNull(deliveryState, "deliveryTime"))
        }

        return LegacyOrder2DeliveryMapper.toDelivery(status.delivery)
    }

    private fun performActionWithReason(deliveryId: String, deliveryState: DeliveryState,
        deliveryStateType: DeliveryStateType, reasonBasedActionSupplier: (Unit) -> Action) : Delivery {

        if (deliveryState.reason == null) {
            //TODO
//            throw ValidationException(DELIVERY_VALIDATION_ERROR,
//                    SimpleConstraintViolation.notNull(deliveryState, "reason"))
        }

        val action = reasonBasedActionSupplier(Unit)

        val status = changeStateAndHandleError(deliveryId, action, deliveryStateType)

        return LegacyOrder2DeliveryMapper.toDelivery(status.delivery)
    }

    private fun changeStateAndHandleError(id: String, action: Action, state: DeliveryStateType) =
            applyActionAndHandleError(id, action, "changing order $id to state $state")

    private fun applyActionAndHandleError(id: String, action: Action, errorMessagePart: String): ChangeStateOperationStatus {

        val status: ChangeStateOperationStatus

        try {
            status = operatorApi.changeState(null, id, action)
        } catch (t: Throwable) {
            val message = "an error occurred in the backend while $errorMessagePart"
            LOG.error(message, t)
            //TODO
//            throw ExternalServiceException(DELIVERY_CHANGE_ERROR, BACKEND_ERROR, message)
            throw IllegalStateException("replace with externalServiceException")
        }

        val isRequestDenied = STATUS_REQUEST_DENIED == status.status
        val isConflict = STATUS_CONFLICT == status.status
        val isNotFound = STATUS_NOT_FOUND == status.status
        if (isRequestDenied || isConflict) {

            var message = "$errorMessagePart not allowed "
            message += if (isRequestDenied) {
                "due to backend configuration."
            } else {
                "due to invalid state transition."
            }

            LOG.info("$message orderId: {},  status: {}", id, status)
            //TODO
//            throw ConflictException(DELIVERY_CHANGE_ERROR, STATE_CHANGE_NOT_ALLOWED, message)
        }
        if (isNotFound) {
            throw NotFoundException()
        }

        return status
    }

    private fun mapReason(reason: RejectReasonType) =
            try {
                LegacyDeliveryRejectReason.valueOf(reason.name)
            } catch (e: IllegalArgumentException) {
                LOG.error("Unknown RejectReasonType: $reason")
                LegacyDeliveryRejectReason.OTHER
            }
}