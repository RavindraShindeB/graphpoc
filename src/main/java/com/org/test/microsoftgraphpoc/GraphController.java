package com.org.test.microsoftgraphpoc;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;

@RestController
public class GraphController {
	
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		
		String[] scopesStr = new String[] {".default"};
		List<String> scopes = Arrays.asList(scopesStr);
		
		final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
		        .clientId("")
		        .clientSecret("")
		        .tenantId("")
		        .build();

		final TokenCredentialAuthProvider tokenCredentialAuthProvider = 
				new TokenCredentialAuthProvider(scopes, clientSecretCredential);

		final GraphServiceClient graphClient =
		  GraphServiceClient
		    .builder()
		    .authenticationProvider(tokenCredentialAuthProvider)
		    .buildClient();
		
		final User user = graphClient.users("test3@jdttestlab1b2c.onmicrosoft.com")
					.buildRequest()
					.select("displayName,mailNickname,extension_0c4c48ffaa3d43afaabb4a349cede6ae_test")
					.get();
		

		System.out.println("******");
		System.out.println(user.displayName);
		System.out.println(user.mailNickname);
		System.out.println(user.additionalDataManager().get("extension_0c4c48ffaa3d43afaabb4a349cede6ae_test"));
		System.out.println("******");
		return String.format("Hello %s!", name);
	}

}
