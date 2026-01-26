package com.bank.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for AccountController REST API using Spring MockMvc. This
 * test mirrors the functionality of test-api.sh but uses Spring's testing
 * framework.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring/spring-context.xml",
		"file:src/main/webapp/WEB-INF/spring/spring-rest-servlet.xml" })
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountControllerIntegrationTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testGetAccount_A123_InitialBalance() throws Exception {
		mockMvc.perform(get("/account/A123")).andExpect(status().isOk())
				.andExpect(content().contentType("application/json")).andExpect(jsonPath("$.id").value("A123"))
				.andExpect(jsonPath("$.balance").value(100.00));
	}

	@Test
	public void testGetAccount_C456_InitialBalance() throws Exception {
		mockMvc.perform(get("/account/C456")).andExpect(status().isOk())
				.andExpect(content().contentType("application/json")).andExpect(jsonPath("$.id").value("C456"))
				.andExpect(jsonPath("$.balance").value(0.00));
	}

	@Test
	public void testTransfer_A123_to_C456() throws Exception {
		mockMvc.perform(get("/account/A123/transfer/25.00/to/C456")).andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.transferAmount").value(25.00)).andExpect(jsonPath("$.feeAmount").value(5.00))
				.andExpect(jsonPath("$.finalSourceAccount.id").value("A123"))
				.andExpect(jsonPath("$.finalSourceAccount.balance").value(70.00))
				.andExpect(jsonPath("$.finalDestinationAccount.id").value("C456"))
				.andExpect(jsonPath("$.finalDestinationAccount.balance").value(25.00));
	}

	@Test
	public void testCompleteTransferFlow() throws Exception {
		mockMvc.perform(get("/account/A123")).andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(100.00));

		mockMvc.perform(get("/account/C456")).andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(0.00));

		mockMvc.perform(get("/account/A123/transfer/25.00/to/C456")).andExpect(status().isOk());

		mockMvc.perform(get("/account/A123")).andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(70.00));

		mockMvc.perform(get("/account/C456")).andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(25.00));
	}

	@Test(expected = Exception.class)
	public void testGetNonExistentAccount_ShouldFail() throws Exception {
		mockMvc.perform(get("/account/INVALID"));
	}
}
