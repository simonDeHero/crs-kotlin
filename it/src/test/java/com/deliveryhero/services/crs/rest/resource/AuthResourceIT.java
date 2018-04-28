package com.deliveryhero.services.crs.rest.resource;

import com.deliveryhero.services.crs.core.security.CrsHmacAuthenticationScheme;
import com.deliveryhero.services.crs.core.security.CrsHmacAuthenticationScheme.Parameters;
import com.deliveryhero.services.crs.core.security.CrsHmacAuthenticationScheme.SignatureAlgorithm;
import com.deliveryhero.services.crs.rest.api.model.CurrentUser;
import com.deliveryhero.services.crs.rest.api.model.OtpLoginRequest;
import com.deliveryhero.services.crs.rest.api.model.OtpSendRequest;
import com.deliveryhero.services.crs.rest.api.model.Token;
import com.deliveryhero.services.crs.rest.api.model.UsernamePasswordCredentials;
import com.deliveryhero.services.crs.rest.api.model.restaurant.Restaurant;
import com.deliveryhero.services.crs.rest.api.resource.AuthResource;
import com.deliveryhero.services.crs.rest.api.resource.ClientRestaurantService;
import com.deliveryhero.services.crs.rest.api.resource.DeliveriesResource;
import com.deliveryhero.services.crs.rest.api.resource.RestaurantsResource;
import com.deliveryhero.services.crs.test.icash.admin.AdminRestaurant;
import com.deliveryhero.services.crs.test.rest.client.ArbitraryAuthValueAuthenticationContext;
import com.deliveryhero.services.crs.test.rest.client.CrsClient;
import com.deliveryhero.services.crs.test.rest.contract.AuthenticationFailedResponseContract;
import com.deliveryhero.services.crs.test.rest.contract.DeliveriesResponseContracts;
import com.deliveryhero.services.crs.test.rest.contract.OtpResponseContracts;
import com.deliveryhero.services.crs.test.rest.contract.RestaurantsResourceResponseContracts;
import com.deliveryhero.services.crs.test.util.TestRestaurantUtil;
import com.ninecookies.common.util.Strings;
import com.ninecookies.services.bootstrap.test.config.Credentials;
import com.ninecookies.services.bootstrap.test.rest.contract.general.GetResponseContract;
import com.ninecookies.services.bootstrap.test.rest.contract.general.ValidationErrorResponseContract;
import com.ninecookies.services.bootstrap.test.rest.contract.general.ValidationErrorResponseContract.ViolationDescriptor;
import org.apache.cxf.jaxrs.client.WebClient;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.deliveryhero.services.crs.test.rest.contract.CrsBaseResponseContract.UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

/**
 * Tests for a lot of login and token-authentication cases.
 *
 * The deliveriesResource.getIdsOfDeliveriesInStateNew() is used exemplary for the behavior of EVERY resource.
 */
public class AuthResourceIT extends AbstractRestaurantProvidingRestTest {

    private static final AuthenticationFailedResponseContract AUTH_ERROR_OCCURRED_RESPONSE_CONTRACT =
            new AuthenticationFailedResponseContract("An authentication error occured.");
    private static final GetResponseContract LOGIN_SUCCESSFUL_RESPONSE_CONTRACT = new GetResponseContract(Token.class,
            false);

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HMAC_PREFIX = "CRS-HMAC ";
    private static final String E164_PHONE_NUMBER = "+4915735983195";

    protected Token unknownUserToken; // a token in valid format, just of a NOT existing user
    protected Credentials credentials;
    protected Token validToken;
    protected ClientRestaurantService unAuthenticatedService;

    @BeforeClass
    public void setupAbstract() {

        // the pos "SESSION9C" cookie value of a non-existing operator user
        unknownUserToken = new Token("GiYg9qqysLHeNv6xcKMjhLNZ2BPwVIX0j9dwN9aQK2oOedDTS8vvgXaAOirS9fUX");
        unAuthenticatedService = createClient().getService();
        credentials = getTestRestaurantCredentials();
        validToken = unAuthenticatedService.getAuthentication().login(
                new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPassword()));
    }

    @Test
    public void testLoginPossibleWithCorrectCredentialsViaJsonBody() {

        AuthResource authResource = unAuthenticatedService.getAuthentication();
        authResource.login(new UsernamePasswordCredentials(
                credentials.getUsername(), credentials.getPassword()));

        assertResponseContract(authResource, LOGIN_SUCCESSFUL_RESPONSE_CONTRACT);
    }
//
//    @Test
//    public void testLoginPossibleWithCorrectCredentialsViaFormParams() {
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//        authResource.login(credentials.getUsername(), credentials.getPassword());
//
//        assertResponseContract(authResource, LOGIN_SUCCESSFUL_RESPONSE_CONTRACT);
//    }
//
//    @DataProvider
//    public Object[][] wrongUsernamePasswordCredentials() {
//        return new Object[][] {
//                new Object[] { new UsernamePasswordCredentials("foo", "bar") },
//                new Object[] { new UsernamePasswordCredentials(credentials.getUsername(), "bar") },
//                new Object[] { new UsernamePasswordCredentials("foo", credentials.getPassword()) }
//        };
//    }
//
//    @Test(dataProvider = "wrongUsernamePasswordCredentials")
//    public void testWrongUsernameOrPasswordResultsInUnauthorized(UsernamePasswordCredentials wrongCredentials) {
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//
//        assertThrows(() -> authResource.login(wrongCredentials));
//        assertResponseContract(authResource, AUTH_ERROR_OCCURRED_RESPONSE_CONTRACT);
//    }
//
//    @DataProvider
//    public Object[][] nullEmptyUsernamePasswordCredentials() {
//        return new Object[][] {
//                { new UsernamePasswordCredentials(null, null), new ViolationDescriptor[] {
//                        ViolationDescriptor.notEmpty("username"), ViolationDescriptor.notEmpty("password") } },
//                { new UsernamePasswordCredentials(credentials.getUsername(), null), new ViolationDescriptor[] {
//                        ViolationDescriptor.notEmpty("password") } },
//                { new UsernamePasswordCredentials(null, credentials.getPassword()), new ViolationDescriptor[] {
//                        ViolationDescriptor.notEmpty("username") } },
//                { new UsernamePasswordCredentials("", ""), new ViolationDescriptor[] {
//                        ViolationDescriptor.notEmpty("username", ""), ViolationDescriptor.notEmpty("password", "") } },
//                { new UsernamePasswordCredentials(credentials.getUsername(), ""), new ViolationDescriptor[] {
//                        ViolationDescriptor.notEmpty("password", "") } },
//                { new UsernamePasswordCredentials("", credentials.getPassword()), new ViolationDescriptor[] {
//                        ViolationDescriptor.notEmpty("username", "") } }
//        };
//    }
//
//    @Test(dataProvider = "nullEmptyUsernamePasswordCredentials")
//    public void testNoUsernameOrPasswordResultsInModelValidationError(UsernamePasswordCredentials nullCredentials,
//            ViolationDescriptor[] expectedViolations) {
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//
//        assertThrows(() -> authResource.login(nullCredentials));
//        assertModelViolationResponse(authResource, expectedViolations);
//    }
//
//    @Test(dataProvider = "nullEmptyUsernamePasswordCredentials")
//    public void testFormLoginWithNoUsernameOrPassword(UsernamePasswordCredentials nullCredentials,
//            ViolationDescriptor[] expectedViolations) {
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//
//        int i = 0;
//        // we don't have special null/empty cases like in the dataprovider, so simply override them here
//        if (Strings.isNullOrEmpty(nullCredentials.username)) {
//            expectedViolations[i++] = ViolationDescriptor.notEmpty("username");
//        }
//        if (Strings.isNullOrEmpty(nullCredentials.password)) {
//            expectedViolations[i++] = ViolationDescriptor.notEmpty("password");
//        }
//
//        assertThrows(() -> authResource.login(nullCredentials.username, nullCredentials.password));
//        assertFormParamViolationResponse(authResource, expectedViolations);
//    }
//
//    @Test
//    public void testNoAuthorizationHeaderResultsInUnauthorized() {
//
//        DeliveriesResource deliveriesResource = unAuthenticatedService.getDeliveries();
//
//        assertThrows(deliveriesResource::getIdsOfDeliveriesInStateNew);
//        assertResponseContract(deliveriesResource, UNAUTHORIZED);
//    }
//
//    @Test
//    public void testWrongAuthPrefixResultsInUnauthorized() {
//        // use wrong prefix instead of Bearer
//        assertUnauthorizedOnDeliveryAccessUsingAuthHeader("wrong-prefix " + validToken.getToken());
//    }
//
//    @Test
//    public void testNoTokenResultsInUnauthorized() {
//        // Authorization header has correct 'Bearer ' prefix, but empty token
//        assertUnauthorizedOnDeliveryAccessUsingAuthHeader(BEARER_PREFIX);
//    }
//
//    @Test
//    public void testWrongTokenFormatResultsInUnauthorized() {
//        // whatever the token format usually is, adding "foo" breaks it
//        assertUnauthorizedOnDeliveryAccessUsingAuthHeader(BEARER_PREFIX + validToken.getToken() + "foo");
//    }
//
//    @Test
//    public void testUnknownUserTokenResultsInUnauthorized() {
//
//        // use a token in correct format, only from a user, which is unknown (e.g. deleted or so)
//        ClientRestaurantService service = buildAuthenticatedService(BEARER_PREFIX +
//                unknownUserToken.getToken());
//
//        DeliveriesResource deliveriesResource = service.getDeliveries();
//
//        assertThrows(deliveriesResource::getIdsOfDeliveriesInStateNew);
//        assertResponseContract(deliveriesResource, UNAUTHORIZED);
//    }
//
//    @Test
//    public void testCurrentUserOk() {
//
//        ClientRestaurantService service = buildAuthenticatedService(BEARER_PREFIX +
//                validToken.getToken());
//
//        CurrentUser user = service.getAuthentication().currentUser();
//
//        Assert.assertNotNull(user.name);
//        Assert.assertNotNull(user.getId());
//    }
//
//    @Test
//    public void testCurrentUserNotFound() {
//
//        ClientRestaurantService service = buildAuthenticatedService(BEARER_PREFIX +
//                unknownUserToken.getToken());
//
//        AuthResource authentication = service.getAuthentication();
//
//        assertThrows(authentication::currentUser);
//        assertResponseContract(authentication, UNAUTHORIZED);
//    }
//
//    /*
//     * HMAC tests
//     */
//
//    @Test
//    public void testHmacValidAuth() {
//
//        String authzHeaderValue = createValidNewDeliveriesAuthzHeaderValue();
//        ClientRestaurantService service = buildAuthenticatedService(authzHeaderValue);
//
//        DeliveriesResource deliveriesResource = service.getDeliveries();
//
//        deliveriesResource.getIdsOfDeliveriesInStateNew();
//        assertResponseContract(deliveriesResource, DeliveriesResponseContracts.DELIVERIES_NEW);
//    }
//
//    @Test
//    public void testHmacNoParametersResultsInUnauthorized() {
//        assertUnauthorizedOnDeliveryAccessUsingAuthHeader(HMAC_PREFIX);
//    }
//
//    @Test(dataProvider = "getHmacParameterNames")
//    public void testHmacMissingParameters(String hmacParameterName) {
//
//        String authzHeaderValue = createValidNewDeliveriesAuthzHeaderValue();
//        authzHeaderValue = removeHmacParameter(authzHeaderValue, hmacParameterName);
//
//        assertUnauthorizedOnDeliveryAccessUsingAuthHeader(authzHeaderValue);
//    }
//
//    @Test
//    public void testHmacBrokenSignature() {
//
//        String authzHeaderValue = createValidNewDeliveriesAuthzHeaderValue();
//        authzHeaderValue = replaceHmacParameter(authzHeaderValue, Parameters.SIGNATURE, "brokensignature");
//
//        assertUnauthorizedOnDeliveryAccessUsingAuthHeader(authzHeaderValue);
//    }
//
//    @Test
//    public void testHmacUnknownClientId() {
//
//        String authzHeaderValue = createValidNewDeliveriesAuthzHeaderValue();
//        authzHeaderValue = replaceHmacParameter(authzHeaderValue, Parameters.CLIENT_ID, "nobody");
//
//        assertUnauthorizedOnDeliveryAccessUsingAuthHeader(authzHeaderValue);
//    }
//
//    @Test
//    public void testHmacBrokenTimestamp() {
//
//        String authzHeaderValue = createValidNewDeliveriesAuthzHeaderValue();
//        authzHeaderValue = replaceHmacParameter(authzHeaderValue, Parameters.TIMESTAMP, "brokentimestamp");
//
//        assertUnauthorizedOnDeliveryAccessUsingAuthHeader(authzHeaderValue);
//    }
//
//    @Test
//    public void testHmacOutdatedTimestamp() {
//
//        Instant outdatedTimestamp = Instant.now().plusSeconds(getConfig().getLong(
//                "service.accounts.timestamp.tolerance.seconds") + 1);
//
//        String authzHeaderValue = createValidNewDeliveriesAuthzHeaderValue();
//        authzHeaderValue = replaceHmacParameter(authzHeaderValue, Parameters.TIMESTAMP, outdatedTimestamp.toString());
//
//        assertUnauthorizedOnDeliveryAccessUsingAuthHeader(authzHeaderValue);
//    }
//
//    @Test
//    public void testHmacRestaurantInfo() {
//
//        ClientRestaurantService service = createDefaultServiceClient(getTestRestaurantId()).getService();
//        RestaurantsResource restaurantsResource = service.getRestaurants();
//
//        List<Restaurant> restaurants = restaurantsResource.list();
//
//        assertThat(restaurants).isNotNull().hasSize(1);
//        assertResponseContract(restaurantsResource, RestaurantsResourceResponseContracts.LIST);
//
//        Restaurant restaurant = restaurants.get(0);
//        assertEquals(restaurant.getId(), getTestRestaurantId());
//    }
//
//    @Test
//    public void testHmacCurrentUser() {
//
//        ClientRestaurantService service = createDefaultServiceClient(getTestRestaurantId()).getService();
//
//        CurrentUser user = service.getAuthentication().currentUser();
//
//        Assert.assertNotNull(user.name);
//        Assert.assertNotNull(user.getId());
//    }
//
//    @Test
//    public void testRequestOtpSuccessfully() throws Exception {
//
//        String phoneNumber = TestRestaurantUtil.generateMobilePhoneNumber();
//
//        setRestaurantPhoneNumber(phoneNumber);
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//        authResource.requestOtp(otpViaSmsRequest(phoneNumber));
//
//        assertResponseStatus(authResource, Response.Status.NO_CONTENT);
//    }
//
//    private void setRestaurantPhoneNumber(String phoneNumber) throws Exception {
//        Set<AdminRestaurant> restaurants = getIcashAdminApi().getRestaurants();
//        AdminRestaurant restaurant = restaurants.iterator().next();
//        restaurant.getRestaurantAddress().setPhone(phoneNumber);
//        getIcashAdminApi().updateRestaurant(restaurant);
//    }
//
//    @DataProvider
//    public static Object[][] invalidOtpRequests() {
//        return new Object[][] {
//                { otpViaSmsRequest(null), new ViolationDescriptor("channelAddress", "not-null",
//                        "null", "'channelAddress' may not be null\\.") },
//                { otpViaSmsRequest(""), new ViolationDescriptor("channelAddress", "E164PhoneNumber",
//                        "", "is not a valid ITU-T E.164 phone number") },
//                { otpViaSmsRequest(E164_PHONE_NUMBER + "a"), new ViolationDescriptor("channelAddress",
//                        "E164PhoneNumber", E164_PHONE_NUMBER + "a", "is not a valid ITU-T E.164 phone number") }
//        };
//    }
//
//    private static OtpSendRequest otpViaSmsRequest(String smsPhoneNumber) {
//        return new OtpSendRequest(smsPhoneNumber, AuthResource.OtpChannelType.SMS);
//    }
//
//    @Test(dataProvider = "invalidOtpRequests")
//    public void testRequestOtpWithInvalidRequest(OtpSendRequest invalidOtpSendRequest,
//            ViolationDescriptor violationDescriptor) {
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//        assertThrows(BadRequestException.class, () -> authResource.requestOtp(invalidOtpSendRequest));
//
//        assertModelViolationResponse(authResource, violationDescriptor);
//    }
//
//    @Test
//    public void testRequestOtpWithNotExistingPhoneNumber() {
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//        assertThrows(BadRequestException.class,
//                () -> authResource.requestOtp(otpViaSmsRequest(E164_PHONE_NUMBER + "8")));
//
//        assertResponseContract(authResource, OtpResponseContracts.REQUEST_AND_NO_RESTAURANT_FOUND);
//    }
//
//    @Test
//    public void testRequestOtpWithInvalidChannelType() {
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//
//        WebClient webClient = toWebClient(authResource);
//        Response response = webClient.path("otp/request").post(
//                "{\"channelAddress\":\"" + E164_PHONE_NUMBER + "\",\"channelType\":\"invalid\"}");
//
//        assertResponseContract(response, new ValidationErrorResponseContract("json-processing-error",
//                ".*\\\"invalid\\\": value not one of declared Enum instance names:.*"));
//    }
//
//    @DataProvider
//    public static Object[][] invalidOtpLoginRequests() {
//        return new Object[][] {
//                { otpViaSmsLogin(null, E164_PHONE_NUMBER), new ViolationDescriptor("otp", "not-empty",
//                        "null", "'otp' may not be empty\\.") },
//                { otpViaSmsLogin("", E164_PHONE_NUMBER), new ViolationDescriptor("otp", "not-empty",
//                        "", "'otp' may not be empty\\.") },
//                { otpViaSmsLogin("foo", null), new ViolationDescriptor("channelAddress", "not-null",
//                        "null", "'channelAddress' may not be null\\.") },
//                { otpViaSmsLogin("foo", ""), new ViolationDescriptor("channelAddress", "E164PhoneNumber",
//                        "", "is not a valid ITU-T E.164 phone number") },
//                { otpViaSmsLogin("foo", "a"), new ViolationDescriptor("channelAddress", "E164PhoneNumber",
//                        "a", "is not a valid ITU-T E.164 phone number") },
//                { new OtpLoginRequest("foo", E164_PHONE_NUMBER, null), new ViolationDescriptor("channelType",
//                        "not-null", "null", "'channelType' may not be null\\.") },
//        };
//    }
//
//    private static OtpLoginRequest otpViaSmsLogin(String otp, String smsPhoneNumber) {
//        return new OtpLoginRequest(otp, smsPhoneNumber, AuthResource.OtpChannelType.SMS);
//    }
//
//    @Test(dataProvider = "invalidOtpLoginRequests")
//    public void testLoginOtpWithInvalidRequest(OtpLoginRequest invalidOtpLoginRequest,
//            ViolationDescriptor violationDescriptor) {
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//        assertThrows(BadRequestException.class, () -> authResource.login(invalidOtpLoginRequest));
//
//        assertModelViolationResponse(authResource, violationDescriptor);
//    }
//
//    @Test
//    public void testLoginOtpWithInvalidChannelType() {
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//
//        WebClient webClient = toWebClient(authResource);
//        Response response = webClient.path("otp").post(
//                "{\"otp\":\"123456\",\"channelAddress\":\"" + E164_PHONE_NUMBER + "\",\"channelType\":\"invalid\"}");
//
//        assertResponseContract(response, new ValidationErrorResponseContract("json-processing-error",
//                ".*\\\"invalid\\\": value not one of declared Enum instance names:.*"));
//    }
//
//    @Test
//    public void testLoginOtpWithNotExistingPhoneNumber() {
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//        assertThrows(NotAuthorizedException.class, () -> authResource.login(
//                otpViaSmsLogin("foo", E164_PHONE_NUMBER + "8")));
//
//        assertResponseContract(authResource, OtpResponseContracts.LOGIN_AND_UNAUTHENTICATED);
//    }
//
//    @Test
//    public void testLoginWithInvalidOtp() throws Exception {
//
//        String phoneNumber = TestRestaurantUtil.generateMobilePhoneNumber();
//
//        setRestaurantPhoneNumber(phoneNumber);
//
//        AuthResource authResource = unAuthenticatedService.getAuthentication();
//        assertThrows(NotAuthorizedException.class, () -> authResource.login(
//                otpViaSmsLogin("654321", phoneNumber)));
//
//        assertResponseContract(authResource, OtpResponseContracts.LOGIN_AND_UNAUTHENTICATED);
//    }
//
//    // TODO complete walkthrough has to be done manually, as CRS test cannot receive SMS. same for test for expired otp.
//
//    /*
//     * privates below.
//     */
//
//    @DataProvider
//    private Object[][] getHmacParameterNames() {
//        return new Object[][] {
//                { Parameters.ALGORITHM },
//                { Parameters.CLIENT_ID },
//                { Parameters.RESTAURANT_ID },
//                { Parameters.SIGNATURE },
//                { Parameters.TIMESTAMP }
//        };
//    }
//
//    private static String removeHmacParameter(String authzHeaderValue, String parameter) {
//        authzHeaderValue = authzHeaderValue + " ";
//        return authzHeaderValue.replaceAll(parameter + "=[a-zA-Z0-9:\\-._]* ", "").trim();
//    }
//
//    private static String replaceHmacParameter(String authzHeaderValue, String parameter, String newValue) {
//        authzHeaderValue = authzHeaderValue + " ";
//        return authzHeaderValue.replaceAll(parameter + "=[a-zA-Z0-9:\\-._]* ", parameter + "=" + newValue + " ")
//                .trim();
//    }
//
//    private void assertUnauthorizedOnDeliveryAccessUsingAuthHeader(String authorizationHeader) {
//        ClientRestaurantService service = buildAuthenticatedService(authorizationHeader);
//
//        DeliveriesResource deliveriesResource = service.getDeliveries();
//
//        assertThrows(deliveriesResource::getIdsOfDeliveriesInStateNew);
//        assertResponseContract(deliveriesResource, UNAUTHORIZED);
//    }
//
//    private ClientRestaurantService buildAuthenticatedService(String authValue) {
//        CrsClient<ArbitraryAuthValueAuthenticationContext> client = createClient();
//        client.getAuthenticationContext().setAuthValue(authValue);
//        return client.getService();
//    }
//
//    private String createValidNewDeliveriesAuthzHeaderValue() {
//        return CrsHmacAuthenticationScheme.createAuthorizationHeaderValue("GET", URI.create(getTestConfig()
//                .getContextRootUrl() + "api/1/deliveries/ids/new"), Instant.now(), getTestConfig()
//                        .getServiceAccountCredentials().getUsername(),
//                getTestRestaurantId(),
//                getTestConfig().getServiceAccountCredentials().getPassword(), SignatureAlgorithm.SHA256);
//    }
}
