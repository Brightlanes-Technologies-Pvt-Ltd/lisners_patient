package com.lisners.patient.utils;

public class URLs {

        private static final String DOMAIN_URL = "https://admin.lisners.com/api/v1/" ; // "http://13.235.77.46";
        public static final String ROOT_URL = DOMAIN_URL+"patient/";

        //AUTH
        public static final String SEND_LOGIN = ROOT_URL+"login" ;
        public static final String SEND_OTP_SIGN_UP = ROOT_URL+"signup" ;
        public static final String SEND_VERIFY_SIGN_UP = ROOT_URL+"signup-otp" ;
        public static final String SIGNUP_UPDATE = ROOT_URL+"signup-update" ;
        public static final String FORGOT_CHECK_MOBILE =ROOT_URL+ "check-mobile" ;
        public static final String FORGOT_VERIFY =ROOT_URL+ "login-with-otp" ;
        public static final String NEW_PASSWORD =ROOT_URL+ "new-password" ;
        public static final String GET_PROFILE =  ROOT_URL+"get-profile" ;
        public static final String LOGOUT =  ROOT_URL+"logout" ;
        public static final String URL_FAQ =  DOMAIN_URL+"faq" ;
        public static final String GET_PAGES =  DOMAIN_URL+"page-content" ;

        //PROFILE
        public static final String SET_PROFILE =  ROOT_URL+"profile-image-update" ;
        public static final String GET_LANGUAGE =  DOMAIN_URL+"languages" ;
        public static final String GET_NOTIFICATION =  ROOT_URL+"notifications" ;
        public static final String GET_UPDATE =  ROOT_URL+"profile-update" ;
        public static final String GET_FAV_LIST =  ROOT_URL+"get-favorite-counselor" ;
        public static final String GET_DELETE_FAV =  ROOT_URL+"delete-save-counselor" ;
        public static final String GET__FAV_SAVE =  ROOT_URL+"save-counselor" ;
        public static final String UPDATE_PASSWORD =  ROOT_URL+"update-password" ;
        public static final String STORE_CALL_NOW =  ROOT_URL+"c" ;
        public static final String STORE_CALL_APPOINTMENT =  ROOT_URL+"user-appointment" ;
        public static final String GET_SETTING=  DOMAIN_URL+"settings" ;

    // public static final String ADDVANCE

       // HOME
        public static final String GET_SEARCH_APP =  ROOT_URL+"search" ;
        public static final String GET_SEARCH_ADVANCE =  ROOT_URL+"advance-search" ;
        public static final String GET_SPECIALIZATION = DOMAIN_URL+"specializations" ;
        public static final String GET_USER_SPECIALIZATION =  ROOT_URL+"get-user-specialization/" ;
        public static final String GET_COMPETE_USER_APPOINTMENT =  ROOT_URL+"get-complete-user-appointment" ;
        public static final String CONNECT_CALL =  ROOT_URL+"call-start" ;

        //APPOINTMENT
        public static final String GET_APPOINTMENT =  ROOT_URL+"get-appointments" ;
        public static final String GET_APPOINTMENT_DATE =  ROOT_URL+"user-appointment-date" ;
        public static final String GET_APPOINTMENT_SAVE =  ROOT_URL+"user-appointment-save" ;
        public static final String GET_PENDING_USER_APPOINTMENT =  ROOT_URL+"get-pending-user-appointment" ;
        public static final String GET_APPOINTMENT_REVIEW=  ROOT_URL+"rating" ;
        public static final String GET_BOOK_APPOINTMENT_DETAILS=  ROOT_URL+"get-bookappointment" ;




    //WALLET
        public static final String GET_TRANSACTION =  ROOT_URL+"get-transaction-list?page=" ;
        public static final String ADD_PAYMENT =  ROOT_URL+"add-payment" ;
        public static final String GET_WALLET =  ROOT_URL+"get-wallet" ;
        public static final String WITHDRAW_REQUEST = ROOT_URL +"withdraw-requests";


}
