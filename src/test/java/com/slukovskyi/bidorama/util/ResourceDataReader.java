package com.slukovskyi.bidorama.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

@UtilityClass
@Slf4j
public class ResourceDataReader {

    static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T asObject(Resource resource, Class<T> contentClass) {
        try {
            log.debug("Reading data from resource: {} into {} class", resource, contentClass);
            return objectMapper.readValue(ResourceReader.asString(resource), contentClass);
        } catch (JsonProcessingException e) {
            log.error("Exception {} occurred while reading a resource {}", e, resource);
            return null;
        }
    }
}
