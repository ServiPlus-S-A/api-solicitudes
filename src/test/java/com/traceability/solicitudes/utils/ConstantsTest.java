package com.traceability.solicitudes.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void shouldVerifyConstants() {
        assertThat(Constants.API_BASE_PATH).isEqualTo("/api/v1");
        assertThat(Constants.DEFAULT_PAGE_SIZE).isEqualTo(10);
    }

    @Test
    void shouldCoverPrivateConstructor() throws Exception {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Constants instance = constructor.newInstance();

        assertThat(instance).isNotNull();
    }
}