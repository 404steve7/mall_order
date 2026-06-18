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
import java.util.UUID;

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
		String token = registerAndLogin();

		String requestBody = """
				{
					"userId": 1001,
					"productId": 999999,
					"quantity": 1
					}
				""";
		mockMvc.perform(post("/order/create")
						.header("X-Token", token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4001))
				.andExpect(jsonPath("$.message").value("商品不存在"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	void createOrderReturnsBusinessErrorWhenStockNotEnough() throws Exception {

		String token = registerAndLogin();

		String requestBody = """
				{
					"userId": 1001,
					"productId": 4,
					"quantity": 999999
				}	
		""";

		mockMvc.perform(post("/order/create")
						.header("X-Token", token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4002))
				.andExpect(jsonPath("$.message").value("库存不足或商品已下架"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	void getOrderReturnsBusinessErrorWhenOrderNotExist() throws Exception {

		String token = registerAndLogin();

		mockMvc.perform(get("/order/OD_NOT_EXIST")
						.header("X-Token", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4003))
				.andExpect(jsonPath("$.message").value("订单不存在"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	void createOrderReturnsParamErrorWhenQuantityInvalid() throws Exception {

		String token = registerAndLogin();

		String requestBody = """
				{
					"userId": 1001,
					"productId": 4,
					"quantity": 0
					}
		""";

		mockMvc.perform(post("/order/create")
						.header("X-Token", token)
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

		String token = registerAndLogin();

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
						.header("X-Token", token)
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

		String token = registerAndLogin();

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
						.header("X-Token", token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andReturn();

		String responseBody = createResult.getResponse().getContentAsString();
		String orderNo = JsonPath.read(responseBody, "$.data");

		mockMvc.perform(post("/order/cancel/{orderNo}" , orderNo)
						.header("X-Token", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.data").value(true));

		mockMvc.perform(get("/order/{orderNo}" , orderNo)
						.header("X-Token", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.data.orderNo").value(orderNo))
				.andExpect(jsonPath("$.data.status").value(2));
	}

	@Test
	@Transactional
	void cancelOrderReturnsBusinessErrorWhenOrderAlreadyCancelled() throws Exception {

		String token = registerAndLogin();

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
						.header("X-Token", token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andReturn();

		String responseBody = createResult.getResponse().getContentAsString();
		String orderNo = JsonPath.read(responseBody, "$.data");

		mockMvc.perform(post("/order/cancel/{orderNo}" , orderNo)
						.header("X-Token", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.data").value(true));

		mockMvc.perform(post("/order/cancel/{orderNo}" , orderNo)
						.header("X-Token", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4004))
				.andExpect(jsonPath("$.message").value("订单已取消"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@Transactional
	void registerUserReturnsUserIdWhenUsernameNotExists() throws Exception {

		String username = "test_user_" + UUID.randomUUID();
		String requestBody = """
				{
					"username": "%s",
					"password": "123456",
					"nickname": "测试用户"
					}
		""".formatted(username);

		mockMvc.perform(post("/user/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.message").value("success"))
				.andExpect(jsonPath("$.data").isNumber());
	}

	@Test
	@Transactional
	void registerUserReturnsBusinessErrorWhenUsernameAlreadyExists() throws Exception {
		String username = "test_user_" + UUID.randomUUID();

		String requestBody = """
				{
					"username": "%s",
					"password": "123456",
					"nickname": "测试用户"
					}
		""".formatted(username);

		mockMvc.perform(post("/user/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0));

		mockMvc.perform(post("/user/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4012))
				.andExpect(jsonPath("$.message").value("用户名已存在"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@Transactional
	void loginReturnsTokenWhenUsernameAndPasswordCorrect() throws Exception {
		String username = "test_user_" + UUID.randomUUID();
		String registerBody = """
				{
					"username": "%s",
					"password": "123456",
					"nickname": "测试用户"
					}
		""".formatted(username);

		mockMvc.perform(post("/user/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(registerBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0));

		String loginBody = """
				{
					"username": "%s",
					"password": "123456"
					}
		""".formatted(username);

		mockMvc.perform(post("/user/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(loginBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.message").value("success"))
				.andExpect(jsonPath("$.data").isString());

	}

	@Test
	@Transactional
	void loginReturnsBusinessErrorWhenPasswordWrong() throws Exception {
		String username = "test_user_" + UUID.randomUUID();

		String registerBody = """
				{
					"username": "%s",
					"password": "123456",
					"nickname": "测试用户"
					}
		""".formatted(username);

		mockMvc.perform(post("/user/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(registerBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0));

		String loginBody = """
				{
					"username": "%s",
					"password": "wrong_password"
					}
		""".formatted(username);

		mockMvc.perform(post("/user/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(loginBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4011))
				.andExpect(jsonPath("$.message").value("用户名或密码错误"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@Transactional
	void getCurrentUserReturnsUserInfoWhenTokenValid() throws Exception {
		String username = "test_user_" + UUID.randomUUID();
		String registerBody = """
				{
				"username": "%s",
				"password": "123456",
				"nickname": "测试用户"
				}
		""".formatted(username);

		mockMvc.perform(post("/user/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(registerBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0));

		String loginBody = """
				{
				"username": "%s",
				"password": "123456"
				}
				""".formatted(username);
		MvcResult loginResult = mockMvc.perform(post("/user/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(loginBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andReturn();

		String responseBody = loginResult.getResponse().getContentAsString();
		String token = JsonPath.read(responseBody, "$.data");

		mockMvc.perform(get("/user/me")
					.header("X-Token", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andExpect(jsonPath("$.data.username").value(username))
				.andExpect(jsonPath("$.data.password").isEmpty());
	}

	@Test
	void getCurrentUserReturnsBusinessErrorWhenTokenMissing() throws Exception {

		mockMvc.perform(get("/user/me"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4010))
				.andExpect(jsonPath("$.message").value("未登录"))
				.andExpect(jsonPath("$.data").isEmpty());
	}
	@Test
	void createOrderReturnsErrorWhenTokenMissing() throws Exception {
		String requestBody = """
				{
					"username": "%s",
					"password": "123456"
					"quantity": 1
					}
		""";

		mockMvc.perform(post("/order/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(4010))
				.andExpect(jsonPath("$.message").value("未登录"))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	private String registerAndLogin() throws Exception {
		String username = "test_user_" + UUID.randomUUID();
		String registerBody = """
				{
				"username": "%s",
				"password": "123456",
				"nickname": "测试用户"
				}
		""".formatted(username);

		mockMvc.perform(post("/user/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(registerBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0));

		String loginBody = """
				{
				"username": "%s",
				"password": "123456"
				}
		""".formatted(username);

		MvcResult loginResult = mockMvc.perform(post("/user/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(loginBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(0))
				.andReturn();

		String responseBody = loginResult.getResponse().getContentAsString();
		return JsonPath.read(responseBody, "$.data");
	}

}
