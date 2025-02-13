package com.example.trainingsapp.general.exception;

import java.util.List;
import java.util.Map;

public interface ErrorStrategy {
    String returnExceptionMessage(String message);
    List<String> returnExceptionMessageList(List<String> messageList);
    String returnExceptionDescription(String description);
    List<String> returnExceptionDescriptionList(List<String> descriptionList);
}
