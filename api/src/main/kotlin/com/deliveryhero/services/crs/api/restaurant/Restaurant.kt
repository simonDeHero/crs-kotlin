package com.deliveryhero.services.crs.api.restaurant

import com.deliveryhero.services.crs.api.delivery.Currency
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import java.util.*

data class Restaurant(

        @ApiModelProperty(notes = "The name of the restaurant.", required = true,
                readOnly = true, allowEmptyValue = false, example = "Luigi's Pizza")
        val name: String,

        @ApiModelProperty(notes = "The phone number of the restaurant in a non-strict format.", required = true,
                readOnly = true, allowEmptyValue = false, example = "+49 123/4567890")
        val phone: String,

        @ApiModelProperty(notes = """
            The phone number of the platform's support hotline. Can be called by the restaurant if there's a problem
            with the app/system.""", required = true, readOnly = true, allowEmptyValue = false,
                example = "+49 123/4567890")
        val supportPhone: String,

        @ApiModelProperty(notes = """
            The timezone the restaurant is configured for. The format is according to the
            <a href="https://www.iana.org/time-zones">IANA timezone standards</a> and looks like this: 'Europe/Berlin'.
            Other examples can be found at
            <a href="https://en.wikipedia.org/wiki/List_of_tz_database_time_zones">Wikipedia</a>.""", required = true,
                readOnly = true, allowEmptyValue = false, example = "Europe/Berlin")
        val timezone: String,

        @ApiModelProperty(notes = "The address the restaurant is located at.", required = true,
                readOnly = true, allowEmptyValue = false)
        val address: Address,

        @ApiModelProperty(notes = "The currency settings for the restaurant.", required = true,
                readOnly = true, allowEmptyValue = false)
        val currency: Currency,

        @ApiModelProperty(notes = """
            Returns whether either the restaurant takes care of delivering orders or the platform. This is a shortcut
            instead of evaluating 'contractPlan['TRANSPORT'] == 0'. This value is read-only.""", required = true,
                readOnly = true, allowEmptyValue = false)
        @JsonProperty("isRestaurantDelivery")
        val restaurantDelivery: Boolean,

        @ApiModelProperty(notes = """
            A list of active platform restaurants this restaurant is assigned to on the different platforms.""",
                required = true, readOnly = true, allowEmptyValue = false)
        val platformRestaurants: List<PlatformRestaurant>?,

        @ApiModelProperty(notes = """
            The contract plan for the restaurant. The returned model consist of key/value pairs where the key is the
            option and the value the actual value.""", required = true, readOnly = true, allowEmptyValue = false)
        val contractPlan: Map<String, Any> = HashMap(),

        @ApiModelProperty(notes = """
            The available feature flags. The returned model consists of key/value pairs, where the key is the feature
            flag name (e.g. 'COURIER_SERVICE_TRACKING') and the value its activation status - either <'true' or
            'false'.""", required = true, readOnly = true, allowEmptyValue = false, example = "'€' or 'KD'")
        val featureFlags: Map<String, Boolean> = HashMap(),

        @ApiModelProperty(notes = "The operator code of the restaurant.", required = true, readOnly = true,
                allowEmptyValue = false, example = "de-07548-lecker-pizza")
        val operatorCode: String
)