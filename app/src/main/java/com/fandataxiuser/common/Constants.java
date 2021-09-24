package com.fandataxiuser.common;

public interface Constants {

    interface RIDE_REQUEST {
        String DEST_ADD = "d_address";
        String DEST_LAT = "d_latitude";
        String DEST_LONG = "d_longitude";
        String SRC_ADD = "s_address";
        String SRC_LAT = "s_latitude";
        String SRC_LONG = "s_longitude";
        String PAYMENT_MODE = "payment_mode";
        String CARD_ID = "card_id";
        String CARD_LAST_FOUR = "card_last_four";
        String DISTANCE_VAL = "distance";
        String device_type="device_type";
        String SERVICE_TYPE = "service_type";
        String RECIVER_NAME = "receiver_name";
        String RECIVER_PHONE = "receiver_mobile";
        String DELIVERY_ADDRESS = "delivery_address";
        String DELIVERY_DESCRIPTION = "any_instructions";
        String ITEM_NAME = "item_to_deliver";
        String DELIVERIES_DETAILS = "deliveries_detail";

    }

    interface BroadcastReceiver {
        String INTENT_FILTER = "INTENT_FILTER";
    }

    interface Language {
        String ENGLISH = "en";
        String ARABIC = "ar";
        String Bangla = "bn";
        String Pabna = "fr";
    }

    interface MeasurementType {
        String KM = "Kms";
        String MILES = "miles";
    }

    interface Status {
        String EMPTY = "EMPTY";
        String SERVICE = "SERVICE";
        String SEARCHING = "SEARCHING";
        String STARTED = "STARTED";
        String ARRIVED = "ARRIVED";
        String PICKED_UP = "PICKEDUP";
        String DROPPED = "DROPPED";
        String COMPLETED = "COMPLETED";
        String RATING = "RATING";
    }

    interface InvoiceFare {
        String MINUTE = "MIN";
        String HOUR = "HOUR";
        String DISTANCE = "DISTANCE";
        String DISTANCE_MIN = "DISTANCEMIN";
        String DISTANCE_HOUR = "DISTANCEHOUR";
    }

    interface PaymentMode {
        String CASH = "CASH";
        String CARD = "CARD";
        String PAYPAL = "PAYPAL";
        String WALLET = "WALLET";
        String BRAINTREE = "BRAINTREE";
        String PAYUMONEY = "PAYUMONEY";
        String PAYTM = "PAYTM";
    }

    interface LocationActions{
        String SELECT_SOURCE ="select_source";
        String SELECT_DESTINATION ="select_destination";
        String CHANGE_DESTINATION ="change_destination";
        String SELECT_HOME ="select_home";
        String SELECT_WORK ="select_work";
    }
}
