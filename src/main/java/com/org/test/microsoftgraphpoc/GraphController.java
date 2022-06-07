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
	
    private static String authority;
    private static String clientId;
    private static String scope;
    private static String keyPath;
    private static String certPath;
	
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
	
	@PostMapping("/users/add")
	public String addUser() throws IOException {
		System.out.println("******inside post start******");
		setUpSampleData();
		String[] scopesStr = new String[] {".default"};
		List<String> scopes = Arrays.asList(scopesStr);
		
		final OnBehalfOfCredential onBehalfOfCredential = new OnBehalfOfCredentialBuilder()
		        .clientId("0c4c48ff-aa3d-43af-aabb-4a349cede6ae")
		        .pemCertificate(certPath)
		        //.pfxCertificate(certPath)
		        .tenantId("32af3a3d-6490-4e85-92de-7ed6985aa7ff")
		        .build();

		final TokenCredentialAuthProvider tokenCredentialAuthProvider = 
				new TokenCredentialAuthProvider(scopes, onBehalfOfCredential);

		final GraphServiceClient<Request> graphClient =
		  GraphServiceClient
		    .builder()
		    .authenticationProvider(tokenCredentialAuthProvider)
		    .buildClient();
		
		User user = new User();
		user.accountEnabled = true;
		user.displayName = "test user";
		user.mailNickname = "testuser";
		user.userPrincipalName = "testuser123@jdttestlab1b2c.onmicrosoft.com";
		com.microsoft.graph.models.PasswordProfile passwordProfile = new com.microsoft.graph.models.PasswordProfile();
		passwordProfile.password = "TestUser@123";
		passwordProfile.forceChangePasswordNextSignIn = false;
		user.passwordProfile = passwordProfile;
		user.passwordPolicies = "DisablePasswordExpiration";
		
		Extension extension = new Extension();
		extension.additionalDataManager().put("extension_0c4c48ffaa3d43afaabb4a349cede6ae_test", new JsonPrimitive("9988"));
		
		graphClient.users().buildRequest().post(user);
		System.out.println("******inside post end******");
				
		return String.format("Hello %s!", "test create user");
	}
	
	@GetMapping("/helloCert")
	public String helloCert(@RequestParam(value = "name", defaultValue = "World") String name) throws Exception {
		ClientCredentialGrant clientCredentialGrant = new ClientCredentialGrant();
		clientCredentialGrant.graphCert();
		return String.format("Hello %s!", name);
	}
	
	private void setUpSampleData() throws IOException {
	        // Load properties file and set properties used throughout the sample
	        Properties properties = new Properties();
	        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
	        authority = properties.getProperty("AUTHORITY");
	        clientId = properties.getProperty("CLIENT_ID");
	        keyPath = properties.getProperty("KEY_PATH");
	        certPath = properties.getProperty("CERT_PATH");
	        scope = properties.getProperty("SCOPE");
	        System.out.println(keyPath +" : " + certPath);
	    }

}
