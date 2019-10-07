package com.pinit.pinitmobile;


import org.springframework.http.HttpStatus;

public interface Globals {

    String EVENT_TYPE = "eventType";
    String SELECTED_GROUPS = "selectedGroups";
    String SELECTED_TYPES = "selectedTypes";
    String COMMENT_PHOTO_DIR = "comments_commentPhotos";
    String COMMENT_USER_DIR = "comments_userPhotos";
    String USERS_PHOTO_DIR = "usersPhoto";
    String USER_PHOTO_BIG = "userPhotoBig";
    String User_PHOTO_SMALL = "userPhotoSmall";
    String LOGGED_USER = "loggedUser";

    //String SERVER_URL = "http://192.168.0.45:8080/PinItServer/";
    String SERVER_URL = "http://192.168.137.134:8080/PinItServer/";
    String GET_PUBLIC_EVENTS_URL = "events/getActivePublicEvents/";
    String REGISTER_URL = "auth/register";
    String INTERNAL_LOGIN_URL = "auth/login";
    String FACEBOOK_LOGIN_URL = "auth/facebook/";
    String GET_USERS_GROUPS_WITH_EVENTS = "users/getUserGroups/";
    String LOGOUT_URL = "auth/logout/";
    String CREATE_EVENT_URL = "events/createEvent/";
    String GET_TYPES_URL = "events/getEventTypes/";
    String GET_PLUS_MINUS_URL_1 = "events/";
    String GET_PLUS_MINUS_URL_2 = "getEventUserRating/";
    String PUT_PLUS_MINUS_URL_1 = "events/";
    String PUT_PLUS_MINUS_URL_2 = "rateEvent/";
    String CREATE_GROUP_URL = "groups/createGroup/";
    String SIGNIN_TO_GROUP_URL_1 = "groups/";
    String SIGNIN_TO_GROUP_URL_2 = "addUserToGroupSubmissions/";
    String SIGNOUT_FROM_GROUP_URL_1 = "groups/";
    String SIGNOUT_FROM_GROUP_URL_2 = "removeUserFromGroup/";
    String GET_USERS_LIST_URL_1 = "groups/";
    String GET_USERS_LIST_URL_2 = "getGroupSubmissions/";
    String UPDATE_GROUP_URL = "groups/updateGroupBasicData/";
    String ACCEPT_TO_GROUP_URL_1 = "groups/";
    String ACCEPT_TO_GROUP_URL_2 = "addUserToGroup/";
    String DECLINE_USER_URL_1 = "groups/";
    String DECLINE_USER_URL_2 = "removeUserFromGroupSubmissions/";
    String GET_USER_BASIC_DATA_URL = "users/getUserBasicData/";
    String GET_USER_PHOTO_BIG_URL = "users/getUserPhotoBig/";
    String UPDATE_USER_URL = "users/updateUserBasicData/";
    String UPDATE_PHOTO_URL = "users/updateUserPhotos/";
    String CHANGE_PASSWORD_URL = "users/updateUserPassword/";
    String CHAT_URL = "ws://mamkwadrat.pl:8080/PinItServer-0.7/chat/";


    int EXECUTE_SUCCESS = HttpStatus.OK.value();
    int EXECUTE_CREATED = HttpStatus.CREATED.value();
}
