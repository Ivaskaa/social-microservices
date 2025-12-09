package com.example.postservice.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

                    addSimpleResponse(operation, "401", "Authentication required");
                    addSimpleResponse(operation, "403", "Access denied");
                    addSimpleResponse(operation, "404", "Resource not found");
                    addSimpleResponse(operation, "500", "Internal server error");
                })
        );
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

}
