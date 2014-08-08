package org.ff4j.web.resources.it;

/*
 * #%L
 * ff4j-web
 * %%
 * Copyright (C) 2013 - 2014 Ff4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.ff4j.core.Feature;
import org.ff4j.utils.FeatureJsonMarshaller;
import org.ff4j.web.api.resources.FeatureResource;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Integration test for resources {@link FeatureResource}.
 * 
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class FeatureResource_get_TestIT extends AbstractWebResourceTestIT {

    /**
     * TDD.
     */
    @Test
    public void testGet_read() {
        // Given
        assertFF4J.assertThatFeatureExist(F4);
        // When
        WebResource wResf4 = resourceFeatures().path(F4);
        ClientResponse resHttp = wResf4.get(ClientResponse.class);
        String resEntity = resHttp.getEntity(String.class);
        // Then, HTTPResponse
        Assert.assertEquals("Expected status is 200", Status.OK.getStatusCode(), resHttp.getStatus());
        Assert.assertNotNull(resEntity);
        // Then, Entity Object
        Feature f = FeatureJsonMarshaller.unMarshallFeature(resEntity);
        Assert.assertEquals(ff4j.getFeature(F4).toString(), f.toString());
    }

    /**
     * TDD.
     */
    @Test
    public void testGet_readNotFound() {
        // Given
        assertFF4J.assertThatFeatureDoesNotExist(F_DOESNOTEXIST);
        // When
        WebResource wResf4 = resourceFeatures().path(F_DOESNOTEXIST);
        ClientResponse resHttp = wResf4.get(ClientResponse.class);
        String resEntity = resHttp.getEntity(String.class);
        // Then, HTTP Response
        Assert.assertEquals("Expected status is 404", Status.NOT_FOUND.getStatusCode(), resHttp.getStatus());
        // Then, Entity (erro message)
        Assert.assertNotNull(resEntity);
        Assert.assertTrue("Invalid error message : " + resEntity, resEntity.contains("not exist"));
    }


}
