package com.eric.webflux.sec02;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "sec=sec02",
        "logging.level.org.springframework.r2dbc=INFO"
})
public abstract class AbstractTest {
}
