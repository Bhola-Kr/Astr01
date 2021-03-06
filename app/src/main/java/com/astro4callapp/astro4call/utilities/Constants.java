package com.astro4callapp.astro4call.utilities;

import java.util.HashMap;

public class Constants {

    public static final String KEY_COLLECTIONS_USERS = "users";
    public static final String KEY_WALLET_AMOUNT = "amount";
    public static final String KEY_WALLET_FLOAT_AMOUNT = "amount";
    public static final String KEY_WALLET_FLOAT_UPDATED_AMOUNT = "amount";

    public static final String KEY_ASTRO_USERS = "astro";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_ASTROLOGER = "astro";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_REG_ID1 = "reg_id";

    public static final String KEY_USER_REG_ID1_ASTRO= "current_astro_id";

    public static final String KEY_USERS_ASTRO_CHATID = "chat_id";

    public static final String KEY_US_REG_ID = "reg_id";


    public static final String KEY_FCM_TOKEN = "fcm_token";


    public static final String KEY_PREFERENCE_NAME = "videoMeetingPreference";
    public static final String KEY_IS_SIGNED = "isSigned";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITOR_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String REMOTE_MSG_INITIATION_RESPONSE = "invitationResponse";

    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";


    public static HashMap<String,String> getRemoteMessageHeaders(){
        HashMap<String,String> headers=new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key=AAAAGz1GG1M:APA91bFR0AMnAJk6XX9aLf3d5kKa61X_3xj0k149FIEQChUJYtWjjAUq5xy6qv3alDxT0Y9s_BcT1YnRyNSUo2zb-T_Kip-udOAIl1pk3dbMn9Q8zJogd15IWuMnwQSBN6JA6BZk7Q5k"
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }
}
