package cdp;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v94.fetch.Fetch;
import org.openqa.selenium.devtools.v94.fetch.model.RequestPattern;
import org.openqa.selenium.devtools.v94.network.model.ErrorReason;
import org.testng.annotations.Test;

public class failTheApiRequest {

	// why?
	/*
	 * sometimes, we need to automate the Test scenario on UI of
	 * " what if the API gives an error", the UI should throw a particular Toaster
	 * Error
	 * 
	 * ex: Network Errors: Error Submit Request. Please try again! These kinds of
	 * error comes only when there is some problem from the API side
	 * 
	 */

	@Test
	public void failAPIcalls() throws InterruptedException {
		// Initialize Driver Instance
		System.setProperty("webdriver.chrome.driver", "chromedriver3");
		ChromeDriver driver = new ChromeDriver();

		// Initialize Chrome Dev Tool Instance
		DevTools chromeDevTools = driver.getDevTools();
		chromeDevTools.createSession();

		/*
		 * Step1: CREATE CUSTOM PATTERN 
		 * 
		 * Optional.empty() will apply for all the calls
		 * 
		 * Creating special pattern to fetch a particular Call/request
		 */

		Optional<List<RequestPattern>> patterns = Optional
				.of(Arrays.asList(new RequestPattern(Optional.of("*recaptcha*"), Optional.empty(), Optional.empty())));

		// Step2: Enable the Fetch Domain with the "custom pattern" created in Step1

		chromeDevTools.send(Fetch.enable(patterns, Optional.empty()));

		/*
		 * STEP3:  Add Listener to the event -->when this event named 'RequestPaused()'
		 * happened, it emits an object of type 'request' and we catch that object and
		 * apply the manipulation using LAMBDA expression
		 * 
		 * 
		 */
		chromeDevTools.addListener(Fetch.requestPaused(), request -> {
			// we dont need to put condition coz already pattern is created

			System.out.println("Listening to the Event RequestPaused" + "emited  /n" + request);
			//Fail the request intensionally
			chromeDevTools.send(Fetch.failRequest(request.getRequestId(), ErrorReason.FAILED));
		});

		// Test Flow
		driver.get("https://www.rahulshettyacademy.com/AutomationPractice/");
		Thread.sleep(5000);
		driver.manage().window().maximize();
		System.out.println("Test execution completed!");
		driver.quit();

	}

}
