package com.example.demo;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class DemoApplicationTests {

	@SneakyThrows
	@Test
	void contextLoads() {

		HttpUrlConnectionExample example = new HttpUrlConnectionExample();
		example.main();

	}
}
