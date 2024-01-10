package com.identicum.keycloak;

import java.util.stream.Stream;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SlskeyAuthenticator implements Authenticator {

	private static final Logger log = Logger.getLogger(SlskeyAuthenticator.class);

	@Override
	public void close() {
		return;
	}

	@Override
	public void authenticate(AuthenticationFlowContext context) {

		// Client info
		ClientModel client = context.getAuthenticationSession().getClient();
		String clientId = client.getClientId();

		// User info
		UserModel user = context.getUser();

		// Authorization mapping config
		AuthenticatorConfigModel config = context.getAuthenticatorConfig();
		String authorizationMappingString = config.getConfig().get("authorizationMapping");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode authorizationMappingNode;
		try {
			authorizationMappingNode = mapper.readTree(authorizationMappingString);
		} catch (Exception e) {
			this.redirectToErrorPage(context, "Invalid configuration on SLSP AuthProxy");
			return;
		}
		
		// Check for required value for this clientId
		JsonNode requiredValueNode = authorizationMappingNode.get(clientId);
		if (requiredValueNode == null) {
			this.redirectToErrorPage(context, "No authorization mapping found for " + clientId);
			return;
		}

		// Check if requiredValueNode is an object
		if (!requiredValueNode.isObject()) {
			this.redirectToErrorPage(context, "Invalid authorization mapping for " + clientId);
			return;
		}

		// Check for each key value pair in requiredValueNode, if user has required value
		boolean[] authorized = {false}; // Using an array to effectively make it 'final' for lamda expression
		requiredValueNode.fields().forEachRemaining(entry -> {
			String requiredAttribute = entry.getKey();
			String requiredValue = entry.getValue().asText();
			Stream<String> userAttributes = user.getAttributeStream(requiredAttribute);
			// check if user has attribute, otherwise continue to next key value pair
			if (userAttributes == null) {
				return;
			}
			authorized[0] = userAttributes.anyMatch(s -> requiredValue.equals(s));
		});

		if (!authorized[0]) {
			this.redirectToErrorPage(context, "You are not authorized to access " + clientId);
			return;
		}

		context.success();
	}

	public void redirectToErrorPage(AuthenticationFlowContext context, String errorMessage) {
		jakarta.ws.rs.core.Response challengeResponse = context.form().setError(errorMessage).createErrorPage(jakarta.ws.rs.core.Response.Status.FORBIDDEN);
		context.failureChallenge(AuthenticationFlowError.IDENTITY_PROVIDER_ERROR, challengeResponse);
		return;
	}

	@Override
	public void action(AuthenticationFlowContext context) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'action'");
	}

	@Override
	public boolean requiresUser() {
		return true;
	}

	@Override
	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		return true;
	}

	@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'setRequiredActions'");
	}

}
