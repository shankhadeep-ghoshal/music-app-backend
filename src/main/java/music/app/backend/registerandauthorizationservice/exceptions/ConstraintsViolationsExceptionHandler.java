package music.app.backend.registerandauthorizationservice.exceptions;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import music.app.backend.responses.ErrorResponsesConcrete;

import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

@Produces
@Singleton
//@Requires(classes = {ExceptionHandler.class ,ConstraintViolationException.class})
public class ConstraintsViolationsExceptionHandler
        implements ExceptionHandler<ConstraintViolationException, HttpResponse> {

    @Override
    public HttpResponse
    handle(HttpRequest request, ConstraintViolationException exception) {
        final Set<ConstraintViolation<?>> consViol = exception.getConstraintViolations();

        final String collect = consViol
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","));

        return HttpResponse
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponsesConcrete<>(400,
                        "Wrong data used",
                        collect));
    }
}