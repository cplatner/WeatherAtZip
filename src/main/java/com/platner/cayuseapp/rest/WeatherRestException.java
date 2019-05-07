package com.platner.cayuseapp.rest;

import org.springframework.http.ResponseEntity;

class WeatherRestException extends Exception
{
    private static final long serialVersionUID = 1L;
    private final ResponseEntity<?> internalEntity;

    WeatherRestException(ResponseEntity<?> internalEntity)
    {
        this.internalEntity = internalEntity;
    }

    /**
     * If an exception needs to be thrown, then include the status
     * and any data or message.
     */
    ResponseEntity<?> getInternalEntity()
    {
        return internalEntity;
    }
}
