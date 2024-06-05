package com.openclassrooms.integration.starterjwt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.SpringBootSecurityJwtApplication;

/**
 * Integration tests for verifying the Spring Boot application startup.
 */
@SpringBootTest(classes = SpringBootSecurityJwtApplication.class)
public class SpringBootSecurityJwtApplicationTests {

    /**
     * Test the main method to ensure the Spring Boot application starts up correctly.
     * This test checks that the application context loads without any issues.
     */
	@Test
    public void mainApplicationTest() {
        SpringBootSecurityJwtApplication.main(new String[] {});
    }

}
