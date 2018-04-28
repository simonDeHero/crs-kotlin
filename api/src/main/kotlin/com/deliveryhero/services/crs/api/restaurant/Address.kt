package com.deliveryhero.services.crs.api.restaurant

import com.ninecookies.common.model.Iso3166Alpha2Code
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class Address(

        @ApiModelProperty(notes = """
            The street including the street number. (deprecated, use 'streetName' and 'building' instead)""",
                required = true, readOnly = true, allowEmptyValue = false,
                example = "Block 2, Abdul Malik Bin Marwan St")
        val street: String? = null,

        @ApiModelProperty(notes = "The postal code.", required = false, readOnly = true, allowEmptyValue = true,
                example = "00965")
        val zipCode: String? = null,

        @ApiModelProperty(notes = "The city name.", required = false, readOnly = true, allowEmptyValue = true,
                example = "Abu Halifa")
        val city: String? = null,

        @ApiModelProperty(notes = "The country name in human readable format.", readOnly = true,
                allowEmptyValue = false, required = true, example = "Kuwait")
        val country: String,

        @ApiModelProperty(notes = """
            The country code in <a href="https://en.wikipedia.org/wiki/ISO_3166-2">ISO-3166-2</a> format like
            'DE' for germany.""", required = true, readOnly = true, allowEmptyValue = false, example = "KW")
        val countryCode: Iso3166Alpha2Code,

        @ApiModelProperty(notes = "The name of the street.", required = true, readOnly = true, allowEmptyValue = true,
                example = "Abdul Malik Bin Marwan St")
        val streetName: String? = null,

        @ApiModelProperty(notes = "The identifier of the building (e.g. in germany it would be the street number).",
                readOnly = true, allowEmptyValue = true, required = true, example = "Block 2")
        val building: String? = null,

        @ApiModelProperty(notes = "The geo location latitude ([-90, 90]).", required = true, readOnly = true,
                allowEmptyValue = false, example = "52.52469869999999")
        @Min(-90) @Max(90)
        val latitude: Double,

        @ApiModelProperty(notes = "The geo location longitude ([-180, 180]).", required = true, readOnly = true,
                allowEmptyValue = false, example = "13.3929251")
        @Min(-180) @Max(180)
        val longitude: Double
)