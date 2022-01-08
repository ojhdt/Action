package com.ojhdtapp.action.logic.model

data class LocationResponse(
    val info: String,
    val infocode: String,
    val regeocode: Regeocode,
    val status: String
) {
    data class Regeocode(
        val aois: List<Aoi>,
        val formatted_address: String,
        val pois: List<Poi>,
        val roadinters: List<Roadinter>,
        val roads: List<Road>
    ) {
        data class AddressComponent(
            val adcode: String,
            val building: Building,
            val businessAreas: List<BusinessArea>,
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
                val type: String
            )

            data class BusinessArea(
                val id: String,
                val location: String,
            )

            data class Neighborhood(
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

        data class Aoi(
            val adcode: String,
            val area: String,
            val distance: String,
            val id: String,
            val location: String,
            val name: String,
            val type: String
        )

        data class Poi(
            val address: String,
            val businessarea: String,
            val direction: String,
            val distance: String,
            val id: String,
            val location: String,
            val name: String,
            val poiweight: String,
            val tel: Any,
            val type: String
        )

        data class Roadinter(
            val direction: String,
            val distance: String,
            val first_id: String,
            val first_name: String,
            val location: String,
            val second_id: String,
            val second_name: String
        )

        data class Road(
            val direction: String,
            val distance: String,
            val id: String,
            val location: String,
            val name: String
        )
    }
}