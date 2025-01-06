package ru.nsu.concert_mate.user_service.api;

public enum ApiResponseStatusCode {
    SUCCESS,
    INTERNAL_ERROR,
    USER_ALREADY_EXISTS,
    USER_NOT_FOUND,
    CITY_ALREADY_ADDED,
    CITY_NOT_ADDED,
    INVALID_CITY,
    FUZZY_CITY,
    TRACK_LIST_ALREADY_ADDED,
    TRACK_LIST_NOT_ADDED,
    INVALID_TRACK_LIST,
    INVALID_COORDS
}