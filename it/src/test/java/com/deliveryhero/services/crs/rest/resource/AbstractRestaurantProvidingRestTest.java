package com.deliveryhero.services.crs.rest.resource;

import com.deliveryhero.services.crs.test.rest.AbstractRestTest;
import com.deliveryhero.services.crs.test.util.TestRestaurantUtil;
import com.ninecookies.services.bootstrap.test.config.Credentials;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * A class, which creates a test restaurant and afterwards provides its information and username and password of the
 * created operator to concrete test-sub-classes. After the tests have finished, the restaurant is deleted incl.
 * operator and orders. This allows for faster testing than using a single dedicated restaurant, where the number of
 * orders will rise by each test run.
 */
public abstract class AbstractRestaurantProvidingRestTest extends AbstractRestTest {

    /*
     * For the first instantiated test-sub-class these values will be filled because of the @BeforeSuite. Any following
     * test class will have a "own" AbstractRestaurantProvidingRestTest super-class. Therefore those values will be
     * null. In order to prevent that and "keep the state", they must be "static".
     */
    private static TestRestaurantUtil.OperatorRestaurantCreation operatorAndRestaurant;

    @BeforeSuite
    public void setupRestaurant() throws Exception {

        operatorAndRestaurant = getRestaurantUtil().createOperatorAndRestaurant();

        getRestaurantUtil().createVeryFirstOrderAndWaitUntilAvailable(getTestConfig().getTransmissionCredentials(),
                createClient(operatorAndRestaurant.credentials).getService(),
                operatorAndRestaurant.externalRestaurantId);
    }

    @AfterSuite(alwaysRun = true)
    public void teardownRestaurant() throws Exception {

        if (operatorAndRestaurant != null) {
            getRestaurantUtil().deleteOperatorAndRestaurant(operatorAndRestaurant.operatorId);
        }
    }

    protected String getTestRestaurantExternalId() {
        return operatorAndRestaurant.externalRestaurantId;
    }
    
    protected String getTestRestaurantId() {
        return operatorAndRestaurant.restaurantId;
    }    

    protected Credentials getTestRestaurantCredentials() {
        return operatorAndRestaurant.credentials;
    }
}
