package com.eric.webflux.sec04;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "sec=sec04",
        "logging.level.org.springframework.r2dbc=INFO"
})
public abstract class AbstractTest {
}
