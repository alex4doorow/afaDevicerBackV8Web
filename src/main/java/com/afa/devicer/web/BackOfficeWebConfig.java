package com.afa.devicer.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class BackOfficeWebConfig {

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Get rid of all leading & trailing spaces from JSON String fields
        final SimpleModule moduleStringWithoutSpace = new SimpleModule();
        moduleStringWithoutSpace.addDeserializer(String.class, new StringWithoutSpaceDeserializer(String.class));
        objectMapper.registerModule(moduleStringWithoutSpace);

        return objectMapper;
    }

    public static class StringWithoutSpaceDeserializer extends StdDeserializer<String> {
        protected StringWithoutSpaceDeserializer(final Class<String> vc) {
            super(vc);
        }

        @Override
        public String deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
            return parser.getText() == null
                    ? null
                    : parser.getText().trim().replaceAll(" +", " ");
        }
    }
}
