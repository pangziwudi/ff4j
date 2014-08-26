package org.ff4j.utils.json;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.ff4j.core.Feature;
import org.ff4j.core.FlippingStrategy;

/**
 * Unmarshalling data from JSON with Jackson.
 *
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class FeatureJsonParser {

    /** Jackson mapper. */
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Unmarshall {@link Feature} from json string.
     *
     * @param json
     *            json representation of feature.
     * @return feature object
     */
    @SuppressWarnings("unchecked")
    public static Feature parseFeature(String json) {
        try {
            return parseFeatureMap(objectMapper.readValue(json, HashMap.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot parse json as Feature " + json, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Feature parseFeatureMap(Map<String, Object> fMap) {
        Feature f = new Feature((String) fMap.get("uid"));
        f.setEnable((Boolean) fMap.get("enable"));
        f.setDescription((String) fMap.get("description"));
        f.setGroup((String) fMap.get("group"));
        // permissions
        List<String> perm = (ArrayList<String>) fMap.get("permissions");
        f.setPermissions((perm != null) ? new HashSet<String>(perm) : null);
        // flipping strategy
        f.setFlippingStrategy(parseFlipStrategy(f.getUid(), (LinkedHashMap<String, Object>) fMap.get("flippingStrategy")));
        return f;
    }

    /**
     * Convert feature array to json.
     *
     * @param features
     *            target features
     * @return json string
     */
    public static String featureArrayToJson(Feature[] features) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (features != null) {
            boolean first = true;
            for (Feature feature : features) {
                sb.append(first ? "" : ",");
                sb.append(feature.toJson());
                first = false;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Parse json string to get {@link FlippingStrategy}.
     * 
     * @param uid
     *            identifier
     * @param json
     *            json expression
     * @return flip strategy
     */
    @SuppressWarnings("unchecked")
    public static FlippingStrategy parseFlipStrategyAsJson(String uid, String json) {
        if (null == json || "".equals(json)) return null;
        try {
            return parseFlipStrategy(uid, (HashMap<String, Object>) objectMapper.readValue(json, HashMap.class));
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Cannot parse JSON " + json, e);
        } catch (JsonMappingException e) {
            throw new IllegalArgumentException("Cannot map JSON " + json, e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read JSON " + json, e);
        }
    }
    
    /**
     * Parse json string to get {@link FlippingStrategy}.
     * 
     * @param uid
     *            identifier
     * @param json
     *            json expression
     * @return flip strategy
     */
    @SuppressWarnings("unchecked")
    public static FlippingStrategy parseFlipStrategy(String uid, HashMap<String, Object> flipMap) {
        if (null == flipMap || flipMap.isEmpty()) return null;
        String classType = null;
        FlippingStrategy strategy = null;
        try {
            //Map<String, Object> flipMap = objectMapper.readValue(json, HashMap.class);
            classType = (String) flipMap.get("classType");
            strategy = (FlippingStrategy) Class.forName(classType).newInstance();
            HashMap<String, String> initparams = (HashMap<String, String>) flipMap.get("initParams");
            // Initialized
            strategy.init(uid, initparams);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(classType + " does not seems to have a DEFAULT constructor", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(classType + " does not seems to have a PUBLIC constructor", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(classType + " has not been found within classpath, check syntax", e);
        }
        return strategy;
    }

    /**
     * Parse the json expression as array of {@link Feature}.
     *
     * @param json
     *      json expression
     * @return
     *      array of feature
     */
    @SuppressWarnings("unchecked")
    public static Feature[] parseFeatureArray(String json) {
        if (null == json || "".equals(json))
            return null;
        try {
            List<LinkedHashMap<String, Object>> flipMap = objectMapper.readValue(json, List.class);
            Feature[] fArray = new Feature[flipMap.size()];
            int idx = 0;
            for (LinkedHashMap<String, Object> ll : flipMap) {
                fArray[idx++] = parseFeatureMap(ll);
            }
            return fArray;
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Cannot parse JSON " + json, e);
        } catch (JsonMappingException e) {
            throw new IllegalArgumentException("Cannot map JSON " + json, e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read JSON " + json, e);
        }
    }

}
