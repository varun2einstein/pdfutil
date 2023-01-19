package com.var.pdfutil;

import com.var.pdfutil.config.AppConfig;
import com.var.pdfutil.config.GCPConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		AppConfig.class,
		GCPConfig.class
})
public class PdfutilApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfutilApplication.class, args);
	}

}
