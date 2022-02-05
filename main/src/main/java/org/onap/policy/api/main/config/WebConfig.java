/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Bell Canada. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.api.main.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;
import java.util.Arrays;
import java.util.List;
import org.onap.policy.api.main.config.converter.StringToEnumConverter;
import org.onap.policy.api.main.config.converter.YamlHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.spring.web.json.Json;

/**
 * Register custom converters to Spring configuration.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEnumConverter());
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        YamlHttpMessageConverter yamlConverter = new YamlHttpMessageConverter();
        yamlConverter.setSupportedMediaTypes(Arrays.asList(MediaType.parseMediaType("application/yaml")));
        converters.add(yamlConverter);

        GsonHttpMessageConverter converter = buildGsonConverter();
        converters.removeIf(c -> c instanceof GsonHttpMessageConverter);
        converters.add(0, converter);
    }

    /**
     * Swagger uses {{@link springfox.documentation.spring.web.json.Json}} which leads to Gson serialization errors.
     * Hence, we customize a typeAdapter on the Gson bean in the Gson http message converter.
     *
     * @return customised GSON HttpMessageConverter instance.
     */
    private GsonHttpMessageConverter buildGsonConverter() {
        JsonSerializer<Json> serializer = (json, type, jsonSerializationContext) ->
            JsonParser.parseString(json.value());
        Gson gson = new GsonBuilder().registerTypeAdapter(Json.class, serializer).create();
        return new GsonHttpMessageConverter(gson);
    }
}