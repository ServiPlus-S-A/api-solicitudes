package com.traceability.solicitudes.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class ApiVersionsTest {

    @Test
    void shouldVerifyConstants() {
        assertThat(ApiVersions.V1).isEqualTo("v1");
    }

    @Test
    void shouldCoverPrivateConstructor() throws Exception {
        Constructor<ApiVersions> constructor = ApiVersions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ApiVersions instance = constructor.newInstance();

        assertThat(instance).isNotNull();
    }
}