package com.deliveryhero.services.crs.api.delivery

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Min

@ApiModel(value = "currency", description = "Represents currency information that is configured for a restaurant.")
data class Currency(

        @ApiModelProperty(
                notes = "The official symbol for the currency - e.g. € for Euro.",
                required = true,
                readOnly = true,
                example = "€")
        val symbol: String,

        @ApiModelProperty(
                notes = "The code for the currency in [ISO-4217](https://de.wikipedia.org/wiki/ISO_4217) format - e.g. "
                        + "'EUR' for Euro.",
                required = true,
                readOnly = true,
                example = "'EUR'")
        val code: String,

        @ApiModelProperty(
                notes = "The human readable name of the currency - e.g. 'Euro'",
                required = true,
                readOnly = true,
                example = "'EURO'")
        val name: String,

        @Min(0)
        @ApiModelProperty(
                notes = "The number of decimal places (after the comma) this currency has. E.g. for 'EUR' it would be "
                        + "'2'.",
                required = true,
                readOnly = true,
                example = "2")
        val fractionDigits: Int
)