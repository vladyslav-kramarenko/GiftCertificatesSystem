package com.epam.esm.core.service.impl.auth.auth0;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.UserInfo;
import com.auth0.net.Response;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.service.AuthService;
import com.epam.esm.core.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

import static com.epam.esm.core.util.Auth0Constants.*;

@Service
public class Auth0AuthServiceImpl implements AuthService {
    private static final String HTTPS = "https://";
    @Value("${auth0.app.clientId}")
    private String appClientId;
    @Value("${auth0.app.clientSecret}")
    private String appClientSecret;
    @Value("${auth0.domain}")
    private String auth0Domain;
    @Value("${auth0.apiAudience}")
    private String apiAudience;
    @Value("${auth0.scope}")
    private String scope;
    private final RestTemplate restTemplate;
    private final UserService userService;

    @Autowired
    public Auth0AuthServiceImpl(UserService userService) {
        this.userService = Objects.requireNonNull(userService);
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> authenticateUser(String email, String password) throws JSONException, Auth0Exception {
        String url = HTTPS + auth0Domain + "/oauth/token";
        JSONObject body = createAuth0AuthenticationObject(email, password);
        HttpEntity<String> requestEntity = createHttpEntity(body, null);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        User user = checkUserInOurDb(response);
        if (user != null) {
            JSONObject responseBody = new JSONObject(response.getBody());
            response = new ResponseEntity<>(responseBody.toString(), response.getHeaders(), response.getStatusCode());
        }
        return response;
    }

    public User registerUser(String email, String password, String firstName, String lastName) throws JSONException {
        String managementApiAccessToken = getManagementApiAccessToken();
        String createUserUrl = HTTPS + auth0Domain + "/api/v2/users";
        JSONObject body = createAuth0UserObject(email, password, firstName, lastName);
        HttpEntity<String> requestEntity = createHttpEntity(body, managementApiAccessToken);
        JsonNode responseJsonNode = restTemplate.postForObject(createUserUrl, requestEntity, JsonNode.class);
        String auth0UserId = responseJsonNode.get(AUTH0_USER_ID).asText();
        try {
            return userService.createUser(auth0UserId, email, firstName, lastName);
        } catch (Exception e) {
            if (!auth0UserId.isEmpty()) deleteUser(auth0UserId);
            throw new IllegalStateException("Failed to create user");
        }
    }

    private JSONObject createAuth0AuthenticationObject(String email, String password) throws JSONException {
        JSONObject body = new JSONObject();
        body.put(AUTH0_USERNAME, email);
        body.put(AUTH0_PASSWORD, password);
        body.put(AUTH0_GRANT_TYPE, AUTH0_PASSWORD);
        body.put(AUTH0_CLIENT_ID, appClientId);
        body.put(AUTH0_CLIENT_SECRET, appClientSecret);
        body.put(AUTH0_AUDIENCE, apiAudience);
        body.put(AUTH0_SCOPE, scope);
        return body;
    }

    private JSONObject createAuth0UserObject(String email, String password, String firstName, String lastName) throws JSONException {
        JSONObject body = new JSONObject();
        body.put(AUTH0_EMAIL, email);
        body.put(AUTH0_PASSWORD, password);
        body.put(AUTH0_CONNECTION, "Username-Password-Authentication");
        body.put(AUTH0_EMAIL_VERIFIED, false);
        body.put(AUTH0_VERIFY_EMAIL, true);
        body.put(AUTH0_GIVEN_NAME, firstName);
        body.put(AUTH0_FAMILY_NAME, lastName);
        return body;
    }

    private HttpEntity<String> createHttpEntity(JSONObject body, String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (bearerToken != null) {
            headers.setBearerAuth(bearerToken);
        }
        return new HttpEntity<>(body.toString(), headers);
    }

    private User checkUserInOurDb(ResponseEntity<String> response) throws JSONException, Auth0Exception {
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String accessToken = jsonResponse.getString(AUTH0_ACCESS_TOKEN);
            AuthAPI auth = AuthAPI.newBuilder(auth0Domain, appClientId)
                    .withClientSecret(appClientSecret)
                    .build();
            Response<UserInfo> apiResponse = auth.userInfo(accessToken).execute();
            UserInfo info = apiResponse.getBody();
            String auth0UserId = info.getValues().get("sub").toString();
            Optional<User> user = userService.findByAuth0UserId(auth0UserId);
            if (user.isEmpty()) {
                return createUserFromUserInfo(info, auth0UserId);
            }
            return user.get();
        }
        return null;
    }

    private User createUserFromUserInfo(UserInfo info, String auth0UserId) {
        String emailUser = info.getValues().get(AUTH0_EMAIL).toString();
        String firstName = info.getValues().get(AUTH0_GIVEN_NAME).toString();
        String lastName = info.getValues().get(AUTH0_FAMILY_NAME).toString();
        return userService.createUser(auth0UserId, emailUser, firstName, lastName);
    }

    private String getManagementApiAccessToken() throws JSONException {
        String url = HTTPS + auth0Domain + "/oauth/token";
        JSONObject body = new JSONObject();
        body.put(AUTH0_GRANT_TYPE, "client_credentials");
        body.put(AUTH0_CLIENT_ID, appClientId);
        body.put(AUTH0_CLIENT_SECRET, appClientSecret);
        body.put(AUTH0_AUDIENCE, HTTPS + auth0Domain + "/api/v2/");
        HttpEntity<String> requestEntity = createHttpEntity(body, null);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        return getAccessToken(response);
    }

    private String getAccessToken(ResponseEntity<String> response) {
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            try {
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.getString(AUTH0_ACCESS_TOKEN);
            } catch (JSONException e) {
                throw new IllegalStateException("Failed to parse access token from response: " + responseBody, e);
            }
        }
        throw new IllegalStateException("Failed to obtain access token: " + response);
    }

    private void deleteUser(String auth0UserId) throws JSONException {
        String managementApiAccessToken = getManagementApiAccessToken();
        String deleteUserUrl = HTTPS + auth0Domain + "/api/v2/users/" + auth0UserId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(managementApiAccessToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        restTemplate.exchange(deleteUserUrl, HttpMethod.DELETE, requestEntity, Void.class);
    }
}

