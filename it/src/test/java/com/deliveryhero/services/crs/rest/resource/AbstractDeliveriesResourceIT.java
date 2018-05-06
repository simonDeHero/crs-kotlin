package com.deliveryhero.services.crs.rest.resource;

import com.deliveryhero.services.crs.rest.api.model.Delivery;
import com.deliveryhero.services.crs.rest.api.resource.ClientRestaurantService;
import com.deliveryhero.services.crs.test.icash.transmission.DeliveryRequest;
import com.deliveryhero.services.crs.test.icash.transmission.ICashTransmissionApi;
import com.deliveryhero.services.crs.test.util.CrsClientHelper;
import com.deliveryhero.services.crs.test.util.DeliveryModelTestFixtures;
import com.ninecookies.services.bootstrap.test.config.Credentials;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class that provides some helpers for ICash related delivery ITs. It offers methods for creating ICash
 * deliveries and cleans up automatically on tear down.
 *
 * @author vguna
 */
public abstract class AbstractDeliveriesResourceIT extends AbstractRestaurantProvidingRestTest {

    private ClientRestaurantService crsService;
    private ICashTransmissionApi iCashTransmissionApi;

    private Set<String> createdDeliveryIds = new HashSet<>();

    @BeforeClass
    protected void setUpBeforeClass() throws Exception {
        crsService = createClient(getTestRestaurantCredentials()).getService();
        iCashTransmissionApi = new ICashTransmissionApi(getTestConfig().getTransmissionContextRootUrl(),
                getTestConfig().getTransmissionCredentials().getUsername(),
                getTestConfig().getTransmissionCredentials().getPassword(),
                getTestConfig().getTransmissionLogMaxBytes());
    }

    @AfterMethod(alwaysRun = true)
    protected void tearDown() throws Exception {
        for (String id : createdDeliveryIds) {
            try {
                CrsClientHelper.rejectDelivery(crsService, id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the {@link ClientRestaurantService} initialized using {@link #createClient(Credentials)}.
     *
     * @return the {@link ClientRestaurantService}.
     */
    protected ClientRestaurantService getDefaultService() {
        return crsService;
    }

    /**
     * Creates the given {@link Delivery} in ICash, reads back the delivery by its given {@code externalId} and returns
     * it as a {@link Delivery} for further processing.
     *
     * @param delivery the {@link Delivery} to create in ICash.
     * @return the created and re-read {@link Delivery}.
     */
    protected Delivery createDeliveryInICash(Delivery delivery) {
        delivery.externalRestaurantId = getTestRestaurantExternalId();
        DeliveryRequest icashDelivery = DeliveryModelTestFixtures.buildICashTestDelivery(delivery);
        iCashTransmissionApi.create(icashDelivery);
        delivery = CrsClientHelper.getDeliveryByExternalId(crsService, delivery.externalId);
        createdDeliveryIds.add(delivery.getId());
        return delivery;
    }
}
