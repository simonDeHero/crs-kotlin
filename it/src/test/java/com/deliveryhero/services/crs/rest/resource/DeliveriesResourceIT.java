package com.deliveryhero.services.crs.rest.resource;

import com.deliveryhero.services.crs.rest.api.model.Delivery;
import com.deliveryhero.services.crs.rest.api.model.Map;
import com.deliveryhero.services.crs.rest.api.model.Tax;
import com.deliveryhero.services.crs.rest.api.resource.DeliveryResource;
import com.deliveryhero.services.crs.test.rest.contract.DeliveriesResponseContracts;
import com.deliveryhero.services.crs.test.util.DeliveryModelTestFixtures;
import com.google.common.collect.ImmutableList;
import org.apache.cxf.jaxrs.client.WebClient;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

/**
 * Provides general tests regarding deliveries.
 *
 * @author vguna
 */
public class DeliveriesResourceIT extends AbstractDeliveriesResourceIT {

    // TODO newly added
    @Test
    public void testGetDeliveryByIdEtag() {

        Delivery delivery = DeliveryModelTestFixtures.buildExpectedPickupDelivery();
        delivery = createDeliveryInICash(delivery);

        DeliveryResource deliveryResource = getDefaultService().getDeliveries().getDeliveryById(delivery.getId());

        assertEtag(deliveryResource, Function.identity());
    }

    // TODO newly added
    @Test
    public void testNewDeliveryIdsEtag() {

        Delivery delivery = DeliveryModelTestFixtures.buildExpectedPickupDelivery();
        createDeliveryInICash(delivery);

        WebClient webClient = toWebClient(getDefaultService().getDeliveries()).path("ids/new");

        assertEtag(webClient, Function.identity());
    }

//    @Test
//    public void testGetDeliveryById() {
//
//        Delivery delivery = DeliveryModelTestFixtures.buildExpectedPickupDelivery();
//        delivery.corporate = true;
//        delivery.shortCode = "666";
//        delivery = createDeliveryInICash(delivery);
//
//        DeliveryResource deliveryResource = getDefaultService().getDeliveries().getDeliveryById(delivery.getId());
//        Delivery readDelivery = deliveryResource.get(true);
//
//        assertResponseContract(deliveryResource, DeliveriesResponseContracts.DELIVERY_GET);
//        assertEquals(readDelivery.corporate, delivery.corporate);
//        assertEquals(readDelivery.shortCode, delivery.shortCode);
//    }
//
//    @Test
//    public void testGetDeliveryByIdUnknown() {
//
//        DeliveryResource deliveryResource = getDefaultService().getDeliveries().getDeliveryById("unknown");
//
//        assertThrows(() -> deliveryResource.get(true));
//        assertResponseContract(deliveryResource, DeliveriesResponseContracts.NOT_FOUND);
//    }
//
//    @Test
//    public void testTaxes() {
//
//        Delivery deliveryWithTaxes = DeliveryModelTestFixtures.buildExpectedPickupDelivery();
//        Tax tax = new Tax();
//        tax.includedInPrice = false;
//        tax.name = "foo";
//        tax.value = BigDecimal.TEN;
//        Tax includedTax = new Tax();
//        includedTax.includedInPrice = true;
//        includedTax.name = "bar";
//        includedTax.value = BigDecimal.ONE;
//        deliveryWithTaxes.taxes = ImmutableList.of(tax, includedTax);
//        Delivery retrievedDelivery = createDeliveryInICash(deliveryWithTaxes);
//
//        assertEquals(retrievedDelivery.taxes, deliveryWithTaxes.taxes);
//    }
//
//    @Test
//    public void testMapUrlContainsKeyButNoSignature() throws Exception {
//
//        Delivery delivery = createDeliveryInICash(DeliveryModelTestFixtures.buildExpectedPickupDelivery());
//
//        Map map = getDefaultService().getDeliveries().getDeliveryById(delivery.getId()).getMap(null, null, null,
//                null, null, null, null);
//
//        assertNotNull(map.url);
//        assertFalse(map.url.contains("signature"));
//        assertTrue(Pattern.compile(".+&key=.+").matcher(map.url).matches());
//    }
}
