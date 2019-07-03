package com.oracle.labs.mlrg.olcut.config.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.oracle.labs.mlrg.olcut.provenance.io.FlatMarshalledProvenance;
import com.oracle.labs.mlrg.olcut.provenance.io.ListMarshalledProvenance;
import com.oracle.labs.mlrg.olcut.provenance.io.MapMarshalledProvenance;
import com.oracle.labs.mlrg.olcut.provenance.io.MarshalledProvenance;
import com.oracle.labs.mlrg.olcut.provenance.io.ObjectMarshalledProvenance;
import com.oracle.labs.mlrg.olcut.provenance.io.SimpleMarshalledProvenance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 */
public class JsonProvenanceDeserializer extends StdDeserializer<MarshalledProvenance> {

    public JsonProvenanceDeserializer(Class<? extends MarshalledProvenance> provClass) {
        super(provClass);
    }

    @Override
    public MarshalledProvenance deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();

        JsonNode node = oc.readTree(jsonParser);

        JsonNode classNode = node.get(JsonProvenanceModule.MARSHALLED_CLASS);

        if (classNode != null) {
            String className = classNode.textValue();
            if (ListMarshalledProvenance.class.getName().equals(className)) {
                JsonNode arrayNode = node.get(JsonProvenanceModule.LIST);
                if (arrayNode != null) {
                    ArrayList<FlatMarshalledProvenance> list = new ArrayList<>();
                    for (JsonNode n : arrayNode) {
                        FlatMarshalledProvenance fmp = (FlatMarshalledProvenance) oc.treeToValue(n, MarshalledProvenance.class);
                        list.add(fmp);
                    }
                    return new ListMarshalledProvenance(list);
                } else {
                    throw new JsonParseException(jsonParser, JsonProvenanceModule.LIST + " field not found in ListMarshalledProvenance.");
                }
            } else if (MapMarshalledProvenance.class.getName().equals(className)) {
                JsonNode objectNode = node.get(JsonProvenanceModule.MAP);
                if (objectNode != null) {
                    Map<String, FlatMarshalledProvenance> map = new HashMap<>();
                    for (Iterator<Entry<String, JsonNode>> it = objectNode.fields(); it.hasNext(); ) {
                        Entry<String, JsonNode> n = it.next();
                        map.put(n.getKey(), (FlatMarshalledProvenance) oc.treeToValue(n.getValue(), MarshalledProvenance.class));
                    }
                    return new MapMarshalledProvenance(map);
                } else {
                    throw new JsonParseException(jsonParser, JsonProvenanceModule.MAP + " field not found in MapMarshalledProvenance.");
                }
            } else if (ObjectMarshalledProvenance.class.getName().equals(className)) {
                if (node.has(JsonProvenanceModule.OBJECT_NAME) && node.has(JsonProvenanceModule.OBJECT_CLASS_NAME) &&
                        node.has(JsonProvenanceModule.PROVENANCE_CLASS) && node.has(JsonProvenanceModule.MAP)) {
                    String objectName = node.get(JsonProvenanceModule.OBJECT_NAME).textValue();
                    String objectClassName = node.get(JsonProvenanceModule.OBJECT_CLASS_NAME).textValue();
                    String provenanceClass = node.get(JsonProvenanceModule.PROVENANCE_CLASS).textValue();
                    JsonNode objectNode = node.get(JsonProvenanceModule.MAP);
                    Map<String, FlatMarshalledProvenance> map = new HashMap<>();
                    for (Iterator<Entry<String, JsonNode>> it = objectNode.fields(); it.hasNext(); ) {
                        Entry<String, JsonNode> n = it.next();
                        map.put(n.getKey(), (FlatMarshalledProvenance) oc.treeToValue(n.getValue(), MarshalledProvenance.class));
                    }
                    return new ObjectMarshalledProvenance(objectName,map,objectClassName,provenanceClass);
                } else {
                    throw new JsonParseException(jsonParser,"ObjectMarshalledProvenance was missing a required field.");
                }
            } else if (SimpleMarshalledProvenance.class.getName().equals(className)) {
                if (node.has(JsonProvenanceModule.KEY) && node.has(JsonProvenanceModule.VALUE) &&
                        node.has(JsonProvenanceModule.PROVENANCE_CLASS) && node.has(JsonProvenanceModule.ADDITIONAL) &&
                        node.has(JsonProvenanceModule.IS_REFERENCE)) {
                    String key = node.get(JsonProvenanceModule.KEY).textValue();
                    String value = node.get(JsonProvenanceModule.VALUE).textValue();
                    String provenanceClass = node.get(JsonProvenanceModule.PROVENANCE_CLASS).textValue();
                    String additional = node.get(JsonProvenanceModule.ADDITIONAL).textValue();
                    boolean isReference  = node.get(JsonProvenanceModule.IS_REFERENCE).asBoolean();
                    return new SimpleMarshalledProvenance(key,value,provenanceClass,isReference,additional);
                } else {
                    throw new JsonParseException(jsonParser,"SimpleMarshalledProvenance was missing a required field.");
                }
            } else {
                throw new JsonParseException(jsonParser,"Unexpected marshalled provenance class, found " + className);
            }
        } else {
            throw new JsonParseException(jsonParser,"Marshalled provenance json did not contain subclass name.");
        }
    }
}
