package com.example.trainingsapp.general.exception;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DetailedErrorStrategy implements ErrorStrategy {
    @Override
    public String returnExceptionMessage(String message) {
        return message;
    }

    @Override
    public List<String> returnExceptionMessageList(List<String> messageList) {
        return messageList;
    }

    @Override
    public String returnExceptionDescription(String description) {
        return description;
    }

    @Override
    public List<String> returnExceptionDescriptionList(List<String> descriptionList) {
        return descriptionList;
    }
}
