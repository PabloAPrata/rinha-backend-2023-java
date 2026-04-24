package com.fast.crud.api.infrastructure.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.type.LogicalType;

@Configuration
public class JacksonCoercionConfig {

    @Bean
    public JsonMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            // Regra: Se o destino for TEXTO (String) e a entrada for NÚMERO (Integer), FALHE.
            builder.withCoercionConfig(LogicalType.Textual, config ->
                    config.setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                            .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
                            .setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail)
            );

            // Regra: Se o destino for uma COLEÇÃO (List<String>) e houver números nela, FALHE.
            builder.withCoercionConfig(LogicalType.Collection, config ->
                    config.setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                            .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
            );
        };
    }
}