package com.deliveryhero.services.crs.api.delivery

import javax.validation.constraints.NotNull

data class DeliveryState(
        val state: DeliveryStateType,
        val deliveryTime: Int?,

        /*
        somehow, when a Double (maybe also Int, etc) is sent as "null", jackson or kotlin sets it to 0. and then the
        kotlin not-null type does not have an effect. so the client sends "null" or the attribute not at all, but for
        the server the value is filled with java default :-)
         */
        val latitude: Double, //in bytecode, this is a java native double, so springfox does NOT render it as required!
        //WRONG! this is a bug, i found. when there is a @PostMapping (so not only GET) involved, then the not-nullable
        //types are documented as optional

        @get:NotNull val longitude: Double, // using @get:NotNull, springfox renders it as required
        val reason: RejectReasonType?,
        val comment: String?
)