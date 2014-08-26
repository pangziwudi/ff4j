package org.ff4j.test.utils;

/*
 * #%L
 * ff4j-web
 * %%
 * Copyright (C) 2013 Ff4J
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

import java.io.ByteArrayOutputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.ff4j.FF4j;
import org.ff4j.core.Feature;
import org.ff4j.test.TestConstantsFF4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for JSON producer and consumer : {@link FeatureJsonMarshaller}
 * 
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class FeatureJsonMarshallTest implements TestConstantsFF4j {

    /** Jackson Mapper. */
    protected ObjectMapper mapper = new ObjectMapper();
    
    /** Sample in MempryStore. */
    private final FF4j ff4j = new FF4j("ff4j.xml");

    /** current feature. */
    private Feature f1 = null;

    /** current feature. */
    private Feature f2 = null;

    /** current feature. */
    private Feature f3 = null;

    /** current feature. */
    private Feature f4 = null;

    /**
     * Initi features before starting.
     */
    @Before
    public void init() {
        f1 = ff4j.getFeature(F1);
        f2 = ff4j.getFeature(F2);
        f3 = ff4j.getFeature(F3);
        f4 = ff4j.getFeature(F4);
    }

    /**
     * Feature still serializable
     */
    @Test
    public void testFeatureIsSerializable() {
        Assert.assertTrue(mapper.canSerialize(Feature.class));
    }
    
    /**
     * TDD.
     * 
     * @throws Exception
     */
    @Test
    public void testMarshaller() throws Exception {
        assertMarshalling(f1);
        assertMarshalling(f2);
        assertMarshalling(f3);
        assertMarshalling(f4);
    }
    
    /**
     * Check cutom (fast) serialization against Jackson.
     * 
     * @param f
     *            current feature
     * @return feature serialized as JSON
     * @throws Exception
     *             error occured
     */
    private String marshallWithJackson(Feature f) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mapper.writeValue(baos, f);
        return new StringBuilder().append(baos).toString();
    }
    
    /**
     * Check serialized string against json serializer.
     * 
     * @param json
     *            json value
     * @param feat
     *            feature
     **/
    private void assertMarshalling(Feature feat) throws Exception {
        Assert.assertEquals(marshallWithJackson(feat), feat.toJson());
    }    

}
