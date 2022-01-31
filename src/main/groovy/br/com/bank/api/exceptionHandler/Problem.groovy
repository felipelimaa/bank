package br.com.bank.api.exceptionHandler

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.builder.Builder

import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
class Problem {

    Integer status

    String type

    String title

    String detail

    LocalDateTime timestamp

    String uiMessage

    List<Object> objects

    @Builder
    static class Object {
        String name

        String uiMessage
    }

}
