package com.backandwhite.core;

import org.springframework.boot.SpringApplication;

public class TestMicCoreserviceApplication {

	public static void main(String[] args) {
		SpringApplication.from(MicCoreserviceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
