package com.cointracker.transactions;

import com.cointracker.transactions.client.BlockchainClient;
import com.cointracker.transactions.model.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class TransactionsApplicationTests {

	@Container
	@ServiceConnection
	static MongoDBContainer container = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

	@LocalServerPort
	private int port;

	@MockBean
	private BlockchainClient blockchainClient;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void syncTransactions() {
		String address = "test-address";
		var url = "http://localhost:" + port + "/transactions/" + address;

		var tx = new Transaction();
		tx.setAddress(address);
		tx.setHash("tx-hash");
		var response = List.of(tx);

		when(blockchainClient.fetchTransactions(address, 0L)).thenReturn(response);
		restTemplate.postForObject(url, null, String.class);

		await().pollDelay(Duration.ofSeconds(1))
				.until(() -> {
					var apiResponse = restTemplate.getForObject(url, String.class);
					Assertions.assertTrue(apiResponse.contains("\"address\":\"test-address\""));
					Assertions.assertTrue(apiResponse.contains("\"hash\":\"tx-hash\""));
					return true;
				});
	}

}
