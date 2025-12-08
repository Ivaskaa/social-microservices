package com.example.authservice.config.swagger;

import com.example.authservice.utils.errors_validation.model.ValidationError;
import com.example.authservice.utils.swagger_custom_error.CustomApiError;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Social app API")
                        .version("1.0.0"));
    }

    @Bean
    public OpenApiCustomizer addGlobalResponses() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                    ensureResponses(operation);

                    addValidationError(httpMethod, operation);
                    addSimpleResponse(operation, "401", "Authentication required");
                    addSimpleResponse(operation, "403", "Access denied");
                    addSimpleResponse(operation, "404", "Resource not found");
                    addSimpleResponse(operation, "500", "Internal server error");
                })
        );
    }

    @Bean
    public OperationCustomizer customApiErrorCustomizer() {
        return (operation, handlerMethod) -> {
            Method implMethod = handlerMethod.getMethod();

            List<CustomApiError> allErrors = new ArrayList<>();
            Collections.addAll(allErrors, implMethod.getAnnotationsByType(CustomApiError.class));

            Class<?> declaringClass = implMethod.getDeclaringClass();
            for (Class<?> iface : declaringClass.getInterfaces()) {
                Method ifaceMethod = findMatchingMethod(iface, implMethod);
                if (ifaceMethod != null) {
                    Collections.addAll(allErrors, ifaceMethod.getAnnotationsByType(CustomApiError.class));
                }
            }

            for (CustomApiError err : allErrors) {
                Schema<?> schema = getSchema(err.schema());
                ApiResponse apiResponse = createApiResponse(err.description(), schema);
                operation.getResponses().addApiResponse(err.code(), apiResponse);
            }

            return operation;
        };
    }

    private Method findMatchingMethod(Class<?> type, Method implMethod) {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(implMethod.getName())
                    && Arrays.equals(method.getParameterTypes(), implMethod.getParameterTypes())) {
                return method;
            }
        }
        return null;
    }

    private void addValidationError(PathItem.HttpMethod method, Operation operation) {
        if (!"GET".equalsIgnoreCase(method.toString())) {
            Schema<?> itemSchema = getSchema(ValidationError.class);
            ArraySchema arraySchema = new ArraySchema().items(itemSchema);
            ApiResponse apiResponse = createApiResponse("Validation error", arraySchema);

            operation.getResponses().put("400", apiResponse);
        }
    }

    private void addSimpleResponse(Operation operation, String status, String description) {
        if (!operation.getResponses().containsKey(status)) {
            operation.getResponses().put(status, new ApiResponse().description(description));
        }
    }

    private void ensureResponses(Operation operation) {
        if (operation.getResponses() == null) {
            operation.setResponses(new ApiResponses());
        }
    }

    private Schema<?> getSchema(Class<?> clazz) {
        return ModelConverters.getInstance()
                .read(clazz)
                .values()
                .stream()
                .findFirst()
                .orElse(new Schema<>());
    }

    private ApiResponse createApiResponse(String description, Schema<?> schema) {
        return new ApiResponse()
                .description(description)
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(schema)
                        )
                );
    }

}
