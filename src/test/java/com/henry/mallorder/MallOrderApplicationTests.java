package com.henry.mallorder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class MallOrderApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void helloReturnsExpectedMessage() throws Exception {
		mockMvc.perform(get("/hello"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.message").value("success"))
				.andExpect(jsonPath("$.data").value("Hello Mall Order"));
	}

	@Test
	void productListReturnsExpectedMessage() throws Exception {
		mockMvc.perform(get("/product/list"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.message").value("success"))
				.andExpect(jsonPath("$.data").isArray());
	}

	@Test
	void getProductReturnsBusinessErrorWhenProductNotExist() throws Exception {
		mockMvc.perform(get("/product/999999"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4001))
				.andExpect(jsonPath("$.message").value("商品不存在"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	void createOrderReturnsBusinessErrorWhenProductNotExists() throws Exception {
		String requestBody = """
				{
					"userId": 1001,
					"productId": 999999,
					"quantity": 1
					}
				""";
		mockMvc.perform(post("/order/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4001))
				.andExpect(jsonPath("$.message").value("商品不存在"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	void createOrderReturnsBuinessErrorWhenStockNotEnough() throws Exception {
		String requestBody = """
				{
					"userId": 1001,
					"productId": 4,
					"quantity": 999999
				}	
		""";

		mockMvc.perform(post("/order/create")
		.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4002))
				.andExpect(jsonPath("$.message").value("库存不足或商品已下架"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	void getOrderReturnsBusinessErrorWhenOrderNotExist() throws Exception {
		mockMvc.perform(get("/order/OD_NOT_EXIST"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4003))
				.andExpect(jsonPath("$.message").value("订单不存在"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

}
