package br.com.bank.api.exceptionHandler

import br.com.bank.domain.exception.AccountsDocumentNumberExistsException
import br.com.bank.domain.exception.AccountsNotFoundException
import br.com.bank.domain.exception.AccountsWithoutDocumentNumberException
import br.com.bank.domain.exception.InvalidTransactionsTypesException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.lang.Nullable
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

import java.time.LocalDateTime

@ControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Value('${bank.exception.uri-base}')
    String URI_BASE_EXCEPTION

    public static String MSG_ERRO_GENERICO = "Ocorreu um erro interno inesperado no sistema. Tente novamente e se o " +
            "problema persistir, entre em contato com o administrador e sistema."

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        if (body == null){
            body = Problem.builder()
                    .timestamp(LocalDateTime.now())
                    .title(status.getReasonPhrase())
                    .status(status.value())
                    .detail(e.getMessage())
                    .uiMessage(MSG_ERRO_GENERICO)
                    .build()
        } else if (body instanceof String) {
            body = Problem.builder()
                    .timestamp(LocalDateTime.now())
                    .title((String) body)
                    .status(status.value())
                    .detail(e.getMessage())
                    .uiMessage(MSG_ERRO_GENERICO)
                    .build()
        }

        return super.handleExceptionInternal(e, body, headers, status, request)
    }

    @ExceptionHandler(AccountsWithoutDocumentNumberException.class)
    ResponseEntity<?> handleAccountWithoutDocumentNumberException(
            AccountsWithoutDocumentNumberException e,
            WebRequest request
    ) {
        ProblemType problemType = ProblemType.INVALID_DATA

        Problem problem = createProblemBuilder(e.status, problemType, e.getReason(), e.getReason())

        return handleExceptionInternal(e, problem, new HttpHeaders(), e.status, request)
    }

    @ExceptionHandler(InvalidTransactionsTypesException.class)
    ResponseEntity<?> handleInvalidTransactionsTypesException(InvalidTransactionsTypesException e, WebRequest request) {
        ProblemType problemType = ProblemType.RESOURCE_NOT_FOUND

        Problem problem = createProblemBuilder(e.status, problemType, e.getReason(), e.getReason())

        return handleExceptionInternal(e, problem, new HttpHeaders(), e.status, request)
    }

    @ExceptionHandler(AccountsNotFoundException.class)
    ResponseEntity<?> handleAccountsNotFoundException(AccountsNotFoundException e, WebRequest request) {
        ProblemType problemType = ProblemType.RESOURCE_NOT_FOUND

        Problem problem = createProblemBuilder(e.status, problemType, e.getReason(), e.getReason())

        return handleExceptionInternal(e, problem, new HttpHeaders(), e.status, request)
    }

    @ExceptionHandler(AccountsDocumentNumberExistsException.class)
    ResponseEntity<?> handleAccountsDocumentNumberExistsException(
        AccountsDocumentNumberExistsException e,
        WebRequest request
    ) {
        ProblemType problemType = ProblemType.EXISTS_RESOURCE_EQUALS

        Problem problem = createProblemBuilder(e.status, problemType, e.getReason(), e.getReason())

        return handleExceptionInternal(e, problem, new HttpHeaders(), e.status, request)
    }

    private Problem createProblemBuilder(
            HttpStatus status,
            ProblemType problemType,
            String detail,
            @Nullable String uiMessage
    ) {
        if(uiMessage == null) {
            uiMessage = MSG_ERRO_GENERICO
        }

        return Problem.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .type(URI_BASE_EXCEPTION + problemType.path)
                .title(problemType.title)
                .detail(detail)
                .uiMessage(uiMessage)
                .build()
    }

    private Problem createProblemBuilder(
            HttpStatus status,
            ProblemType problemType,
            String detail,
            @Nullable String uiMessage,
            List<Problem.Object> objects
    ) {
        if(uiMessage == null) {
            uiMessage = MSG_ERRO_GENERICO
        }

        return Problem.builder()
                .timestamp(LocalDateTime.now())
            .status(status.value())
            .type(URI_BASE_EXCEPTION + problemType.path)
            .title(problemType.title)
            .detail(detail)
            .uiMessage(uiMessage)
            .objects(objects)
            .build()
    }

}
