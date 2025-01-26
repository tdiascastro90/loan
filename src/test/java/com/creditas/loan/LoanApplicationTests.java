package com.creditas.loan;

import com.creditas.loan.request.LoanSimulationRequest;
import com.creditas.loan.response.LoanSimulationResponse;
import com.creditas.loan.service.LoanSimulateService;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoanApplicationTests {

	@LocalServerPort
	private int port;

	@BeforeEach
	public void setup() {
		RestAssured.port = port;
		JsonConfig jsonConfig = JsonConfig.jsonConfig()
				.numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE);

		RestAssured.config = RestAssured.config()
				.jsonConfig(jsonConfig);
	}

	@MockitoBean
	private LoanSimulateService loanSimulateService;

	@Test
	public void testSimulateLoans() {
		List<LoanSimulationRequest> requestList = List.of(
				new LoanSimulationRequest(10000.0, LocalDate.of(1990, 1, 1).toString(), 12),
				new LoanSimulationRequest(20000.0, LocalDate.of(1980, 5, 10).toString(), 24),
				new LoanSimulationRequest(15000.0, LocalDate.of(1995, 3, 15).toString(), 18)
		);
		List<LoanSimulationResponse> mockResponseList = List.of(
				new LoanSimulationResponse(10163.24, 846.94, 163.24),
				new LoanSimulationResponse(20419.33, 850.81, 419.33),
				new LoanSimulationResponse(15358.77, 853.27, 358.77)
		);

		when(loanSimulateService.simulateLoans(anyList())).thenReturn(mockResponseList);

		given()
				.contentType(ContentType.JSON)
				.body(requestList)
				.when()
				.post("/api/loan/simulate")
				.then()
				.statusCode(HttpStatus.OK.value())
				.contentType(ContentType.JSON)
				.body("size()", is(3))
				.body("[0].totalPayment", Matchers.equalTo(10163.24))
				.body("[0].monthlyPayment", Matchers.equalTo(846.94))
				.body("[0].totalInterest", Matchers.equalTo(163.24))
				.body("[1].totalPayment", Matchers.equalTo(20419.33))
				.body("[1].monthlyPayment", Matchers.equalTo(850.81))
				.body("[1].totalInterest", Matchers.equalTo(419.33))
				.body("[2].totalPayment", Matchers.equalTo(15358.77))
				.body("[2].monthlyPayment", Matchers.equalTo(853.27))
				.body("[2].totalInterest", Matchers.equalTo(358.77));
	}

	@Test
	public void testSimulateLoansInvalidAmount() {
		LoanSimulationRequest invalidRequest = new LoanSimulationRequest(-10000.0, LocalDate.of(1990, 1, 1).toString(), 12);

		List<LoanSimulationRequest> requestPayload = List.of(invalidRequest);

		given()
				.contentType(ContentType.JSON)
				.body(requestPayload)
				.when()
				.post("/api/loan/simulate")
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void testSimulateLoansInvalidDate() {
		LoanSimulationRequest invalidRequest = new LoanSimulationRequest(10000.0, "01/01/2025", 12);

		List<LoanSimulationRequest> requestPayload = List.of(invalidRequest);

		given()
				.contentType(ContentType.JSON)
				.body(requestPayload)
				.when()
				.post("/api/loan/simulate")
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void testSimulateLoansEmptyPayload() {
		given()
				.contentType(ContentType.JSON)
				.body(List.of())
				.when()
				.post("/api/loan/simulate")
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void testControllerPerformance() throws InterruptedException {
		int totalRequests = 40000;
		int threads = 10;

		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Runnable> tasks = new ArrayList<>();

		for (int i = 0; i < totalRequests; i++) {
			tasks.add(() -> {
				List<LoanSimulationRequest> requestPayload = List.of(
						new LoanSimulationRequest(10000.0, LocalDate.of(1990, 1, 1).toString(), 12),
						new LoanSimulationRequest(15000.0, LocalDate.of(1995, 3, 15).toString(), 18)
				);

				given()
						.contentType(ContentType.JSON)
						.body(requestPayload)
						.when()
						.post("/api/loan/simulate")
						.then()
						.statusCode(HttpStatus.OK.value())
						.contentType(ContentType.JSON)
						.body("size()", is(2));
			});
		}

		for (Runnable task : tasks) {
			executor.submit(task);
		}

		executor.shutdown();
		boolean finished = executor.awaitTermination(2, TimeUnit.MINUTES);

		if (!finished) {
			throw new RuntimeException("Performance test did not complete in time.");
		}
	}

}