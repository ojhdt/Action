package com.ojhdtapp.action.logic.model

data class LocationResponse(
    val info: String,
    val infocode: String,
    val regeocode: Regeocode,
    val status: String
) {
    data class Regeocode(
        val addressComponent: AddressComponent,
        val formatted_address: String
    ) {
        data class AddressComponent(
            val adcode: String,
            val building: Building,
            val businessAreas: List<BusinessArea>,
            val city: List<Any>,
            val citycode: String,
            val country: String,
            val district: String,
            val neighborhood: Neighborhood,
            val province: String,
            val streetNumber: StreetNumber,
            val towncode: String,
            val township: String
        ) {
            data class Building(
                val name: String,
                val type: String
            )

            data class BusinessArea(
                val id: String,
                val location: String,
                val name: String
            )

            data class Neighborhood(
                val name: String,
                val type: String
            )

            data class StreetNumber(
                val direction: String,
                val distance: String,
                val location: String,
                val number: String,
                val street: String
            )
        }
    }
}