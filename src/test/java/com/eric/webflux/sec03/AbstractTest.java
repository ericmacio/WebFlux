package com.eric.webflux.sec03;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "sec=sec03",
        "logging.level.org.springframework.r2dbc=INFO"
})
public abstract class AbstractTest {
}
