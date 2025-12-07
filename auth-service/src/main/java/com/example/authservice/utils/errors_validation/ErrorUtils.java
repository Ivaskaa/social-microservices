package com.example.authservice.utils.errors_validation;

import com.example.authservice.utils.errors_validation.model.ValidationError;
import com.example.authservice.utils.exceptions.ValidationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ErrorUtils {
    static List<ValidationError> mapErrors(BindingResult bindingResult) {
        List<FieldError> prioritized = prioritize(bindingResult.getFieldErrors());

        return prioritized.stream().map(err -> {
            Object data = extractCustomData(err);
            return ValidationError.builder()
                    .field(err.getField())
                    .message(err.getDefaultMessage())
                    .data(data)
                    .build();
        }).toList();
    }

    static FieldError createFieldError(String field, String message){
        return new FieldError("Request", field, message);
    }

    static FieldError createFieldError(String field, String message, Object data) {
        Object[] arguments = new Object[]{ data };
        return new FieldError("Request", field, "", false, null, arguments, message);
    }

    static void validate(BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            List<ValidationError> errors = ErrorUtils.mapErrors(bindingResult);
            throw new ValidationException(errors);
        }
    }

    private static Object extractCustomData(FieldError err) {
        Object[] args = err.getArguments();
        if (args == null || args.length != 1) {
            return null;
        }
        Object candidate = args[0];

        if (candidate instanceof org.springframework.context.MessageSourceResolvable) {
            return null;
        }
        if (candidate instanceof String) {
            return null;
        }

        return candidate;
    }

    private static List<FieldError> prioritize(List<FieldError> errors) {
        Map<String, FieldError> best = new HashMap<>();
        for (FieldError error : errors) {
            best.putIfAbsent(error.getField(), error);
            best.compute(error.getField(), (f, existing) -> {
                if (existing == null) return error;
                return comparePriority(error, existing) > 0 ? error : existing;
            });
        }
        return new ArrayList<>(best.values());
    }

    private static int comparePriority(FieldError a, FieldError b) {
        int pa = getPriority(a);
        int pb = getPriority(b);
        return Integer.compare(pa, pb);
    }

    private static int getPriority(FieldError err) {
        Object data = extractCustomData(err);
        if (data != null) {
            return 100;
        }

        String code = err.getCode();
        if (code == null) {
            return 0;
        }

        return switch (code) {
            case "NotBlank" -> 90;
            case "NotEmpty" -> 80;
            case "NotNull" -> 70;
            default -> 10;
        };
    }
}