package com.korea.style1;

import com.korea.style1.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class Style1ApplicationTests {

	@Autowired
	private ProductService productService;


	@Test
	void testJpa() {

	}


}
