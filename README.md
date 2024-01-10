# Keycloak - Attribute based - Custom Authenticator
This module implements a Custom Authenticator for Keycloak.
It is used to realize attribute based authorization, depending on the target Sevice Provider.

## Build and Install Module
```shell script
mvn clean package
cp target/*.jar $KEYCLOAK_HOME/standalone/deployments/
```

Afterwards build Keycloak

```shell script
kc.sh build
```


## Authenticator Configuration

1. Go to Authentication menu
2. Create o edit a custom flow
3. Add execution
4. Pick up SLSKey Authenticator from the list the Custom Authenticator
5. Add config in the following format
```
{
    "<client-name>": {
        "<saml-attribute-friendly-name>": "<value>",
    }
    "<client-name>": {
        "<saml-attribute-friendly-name>": "<value>",
        "<saml-attribute-friendly-name>": "<value>",
    }
    [...]
}
```