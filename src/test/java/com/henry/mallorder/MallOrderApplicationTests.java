package com.henry.mallorder;

import com.henry.mallorder.product.entity.Product;
import com.henry.mallorder.product.mapper.ProductMapper;
import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
class MallOrderApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductMapper productMapper;

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
	void createOrderReturnsBusinessErrorWhenStockNotEnough() throws Exception {
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

	@Test
	void createOrderReturnsParamErrorWhenQuantityInvalid() throws Exception {
		String requestBody = """
				{
					"userId": 1001,
					"productId": 4,
					"quantity": 0
					}
		""";

		mockMvc.perform(post("/order/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4000))
				.andExpect(jsonPath("$.message").value("购买数量必须大于0"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@Transactional
	void createOrderReturnsOrderNoWhenProductNotExists() throws Exception {
		Product product = new Product();
		product.setProductName("测试下单商品");
		product.setPrice(new BigDecimal("10.00"));
		product.setStock(10);
		product.setStatus(1);
		productMapper.insert(product);

		String requestBody = """
				{
					"userId": 1001,
					"productId": %d,
					"quantity": 2
					}
		""".formatted(product.getId());

		mockMvc.perform(post("/order/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.message").value("success"))
				.andExpect(jsonPath("$.data").isString());
	}

	@Test
	@Transactional
	void cancelOrderReturnsSuccessWhenProductExists() throws Exception {
		Product product = new Product();
		product.setProductName("测试取消订单商品");
		product.setPrice(new BigDecimal("10.00"));
		product.setStock(10);
		product.setStatus(1);
		productMapper.insert(product);

		String requestBody = """
				{
					"userId": 1001,
					"productId": %d,
					"quantity": 2
					}
		""".formatted(product.getId());

		MvcResult createResult = mockMvc.perform(post("/order/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andReturn();

		String responseBody = createResult.getResponse().getContentAsString();
		String orderNo = JsonPath.read(responseBody, "$.data");

		mockMvc.perform(post("/order/cancel/{orderNo}" , orderNo))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.data").value(true));

		mockMvc.perform(get("/order/{orderNo}" , orderNo))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.data.orderNo").value(orderNo))
				.andExpect(jsonPath("$.data.status").value(2));
	}

	@Test
	@Transactional
	void cancelOrderReturnsBusinessErrorWhenOrderAlreadyCancelled() throws Exception {
		Product product = new Product();
		product.setProductName("测试重复取消订单商品");
		product.setPrice(new BigDecimal("10.00"));
		product.setStock(10);
		product.setStatus(1);
		productMapper.insert(product);

		String requestBody = """
				{
					"userId": 1001,
					"productId": %d,
					"quantity": 2
					}
		""".formatted(product.getId());

		MvcResult createResult =mockMvc.perform(post("/order/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andReturn();

		String responseBody = createResult.getResponse().getContentAsString();
		String orderNo = JsonPath.read(responseBody, "$.data");

		mockMvc.perform(post("/order/cancel/{orderNo}" , orderNo))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.data").value(true));

		mockMvc.perform(post("/order/cancel/{orderNo}" , orderNo))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4004))
				.andExpect(jsonPath("$.message").value("订单已取消"))
				.andExpect(jsonPath("$.data").isEmpty());
	}
}
