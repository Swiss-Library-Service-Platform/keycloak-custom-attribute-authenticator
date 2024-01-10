package com.identicum.keycloak;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlskeyAuthenticatorFactory implements AuthenticatorFactory, ConfigurableAuthenticatorFactory {

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<ProviderConfigProperty>();


    static {
        ProviderConfigProperty property;
        property = new ProviderConfigProperty();
        property.setName("authorizationMapping");
        property.setLabel("Mapping of Client IDs and neccessary eduPersonEntitlement values");
		property.setHelpText("Define a mapping of Client IDs and neccessary eduPersonEntitlement values. The format is: { '<client_id_1>''='<eduPersonEntitlement_value1>', '<client_id_2>''='<eduPersonEntitlement_value2>'}");
		property.setType(ProviderConfigProperty.SCRIPT_TYPE);
        configProperties.add(property);		
    }


	public static final String PROVIDER_ID = "slskey-authenticator";
	private static final SlskeyAuthenticator SINGLETON = new SlskeyAuthenticator();

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public String getDisplayType() {
		return "SLSKey Authenticator";
	}

	@Override
	public String getReferenceCategory() {
		return null;
	}

	@Override
	public boolean isConfigurable() {
		return true;
	}

	@Override
	public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
		return Arrays.asList(AuthenticationExecutionModel.Requirement.REQUIRED).toArray(new AuthenticationExecutionModel.Requirement[0]);
	}

	@Override
	public boolean isUserSetupAllowed() {
		return true;
	}

	@Override
	public String getHelpText() {
		return null;
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return configProperties;
	}

	@Override
	public void init(Config.Scope scope) {

	}

	@Override
	public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

	}

	@Override
	public void close() {

	}

	@Override
	public Authenticator create(KeycloakSession session) {
		return SINGLETON;
	}
}
