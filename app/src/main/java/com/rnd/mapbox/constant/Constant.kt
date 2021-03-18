package com.rnd.mapbox.constant

object Constant {
    const val DATABASE_NAME: String = "navigation_db"
    const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    const val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    const val REQUEST_CODE_LOCATION_PERMISSION = 11

    const val BASE_URL = "https://jumpcab.dinetest.com/webservice/driverapi/driver/"

    const val CHANGE_DRIVER_STATUS = "user/change-driver-status"
    const val UPDATE_DRIVER_LOCATION = "update-driver-location"
    const val GET_BASIC_DATA = "get-basicdata"
    const val SUBMIT_RATING = "ride/submit-rating"
    const val RIDE_OPERATION = "ride/ride-operaion"
    const val GET_SUMMARY = "ride/get-summary"
    const val ACCEPT_QUEUE_RIDE = "ride/accept-queue-ride"
    const val GET_SINGLE_RIDE = "get-single-ride"
    const val CHECK_LOGIN = "user/check-login"
    const val GET_RIDES = "ride/get-rides"
    const val GET_QUEUE_RIDES = "ride/get-queue-rides"

    const val DEVICE_INFO = "https://jumpcab.dinetest.com/webservice/external/driver/deviceinfo"
    const val VERSION =
        "https://jumpcab.dinetest.com/webservice/external/driver/version?_=1614684330987"


}