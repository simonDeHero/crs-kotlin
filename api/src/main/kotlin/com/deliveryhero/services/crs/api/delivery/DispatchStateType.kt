package com.deliveryhero.services.crs.api.delivery

import com.deliveryhero.services.crs.api.HashableEnum

/**
 * Typical workflow is: `WAITING_FOR_DISPATCH` -&gt; `DISPATCHED` -&gt; `EN_ROUTE`
 * -&gt; `DELIVERED` -&gt; `SETTLED`.
 *
 *
 * `UNDEFINED` is a final state for deliveries before this field was introduced.
 *
 *
 * `PICKUP` is also a final state.
 */
enum class DispatchStateType : HashableEnum<DispatchStateType> {
    /**
     * Used for old deliveries, created before drivers and dispatch logic were introduced.
     * =0
     */
    UNDEFINED,
    /**
     * =1
     */
    PICKUP,
    /**
     * =2
     */
    WAITING_FOR_DISPATCH,
    /**
     * =3
     */
    DISPATCHED,
    /**
     * =4
     */
    SETTLED,
    /**
     * =5
     */
    CANCELLED,
    UNKNOWN
}
