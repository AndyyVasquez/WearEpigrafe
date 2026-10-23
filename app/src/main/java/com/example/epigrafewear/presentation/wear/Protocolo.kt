package com.example.epigrafewear.presentation.wear

object DataLayerPaths {
    const val COMMAND = "/watchcontrol/command"
    const val RESPONSE = "/watchcontrol/response"
    const val RECORD_PREFIX = "/fitsync/record/"
    const val KEY_JSON = "json"
    const val KEY_SENT_AT = "sentAt"
}

object ProtocoloEndpoint {
    const val BASE_URL = "https://epigrafe.onrender.com/api"
    const val ENDPOINT_PEDIDOS = "$BASE_URL/pedidos"
}