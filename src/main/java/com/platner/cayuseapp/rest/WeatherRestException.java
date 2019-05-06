package com.platner.cayuseapp.rest;

import org.springframework.http.ResponseEntity;

class WeatherRestException extends Exception
{
    private static final long serialVersionUID = 1L;
    private ResponseEntity<?> internalEntity;

    WeatherRestException(ResponseEntity<?> internalEntity)
    {
        this.internalEntity = internalEntity;
    }

    ResponseEntity<?> getInternalEntity()
    {
        return internalEntity;
    }
}
