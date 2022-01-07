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
            val businessAreas: List<List<Any>>,
            val city: String,
            val citycode: String,
            val country: String,
            val district: String,
            val neighborhood: Neighborhood,
            val province: String,
            val towncode: String,
            val township: String
        ) {
            data class Building(
                val name: List<Any>,
                val type: List<Any>
            )

            data class Neighborhood(
                val name: List<Any>,
                val type: List<Any>
            )
        }
    }
}