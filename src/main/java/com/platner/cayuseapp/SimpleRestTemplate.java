package com.platner.cayuseapp;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SimpleRestTemplate
{
    private RestTemplate rest;
    private HttpHeaders headers;
    private HttpStatus status;


    public SimpleRestTemplate()
    {
        this.rest = new RestTemplate();
        this.headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
    }

    public String get(String uri)
    {
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.GET, requestEntity, String.class);
        this.status = responseEntity.getStatusCode();
        return responseEntity.getBody();
    }

    public HttpStatus getStatus()
    {
        return status;
    }
}