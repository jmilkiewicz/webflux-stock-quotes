package io.spring.workshop.stockquotes;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StockQuotesApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void fetchQuotes() {
		webTestClient
				// We then create a GET request to test an endpoint
				.get().uri("/initialQuotes?size=20")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				// and use the dedicated DSL to test assertions against the response
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Quote.class)
				.hasSize(20)
				// this check is inappropriate on END2END tests
				//.consumeWith(allQuotes ->
				//		assertThat(allQuotes).asList().allSatisfy(quote -> assertThat(((Quote) quote).getPrice()).isPositive()))
		;
	}
}
