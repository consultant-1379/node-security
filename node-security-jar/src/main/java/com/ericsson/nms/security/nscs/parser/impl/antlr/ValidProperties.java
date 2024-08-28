package com.ericsson.nms.security.nscs.parser.impl.antlr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <b>Class used by the command parser to store expected command properties.</b>
 * Created by emaynes on 28/05/2014.
 */
/*
* Be aware that this class is used by src/main/g4/com/ericsson/nms/security/nscs/parser/SecCommandBaseParser.g4 file
* careful is required when changing it.
* */
public class ValidProperties {

    private final Map<String, String> aliasToArg = new HashMap<>();
    private final Map<String, String> types = new HashMap<>();

    public boolean contains(final String propertyOrAlias) {
        return aliasToArg.containsKey(propertyOrAlias);
    }

    public String getPropertyType(final String propertyOrAlias) {
        return types.get(propertyOrAlias);
    }

    public String getTargetProperty(final String propertyOrAlias) {
        return aliasToArg.get(propertyOrAlias);
    }

    public Set<String> getValidPropertyOrAliases(){
        return aliasToArg.keySet();
    }

    public static ValidProperties fromArgSpec(final Object ... spec) {
        if ( spec == null ) {
            return null;
        }

        final ValidProperties properties = new ValidProperties();
        Collection<?> argAndAlises;
        for (Object arg : spec) {
            if ( arg instanceof Collection ) {
                Property mainArg = null;
                Property candidate = null;
                argAndAlises = (Collection<?>) arg;
                for (Object argOrAlias : argAndAlises) {
                    candidate = addAndGetProperty(argOrAlias.toString(), mainArg, properties);
                    if ( mainArg == null ) {
                        mainArg = candidate;
                    }
                }
            } else {
                addAndGetProperty(arg.toString(), null, properties);
            }
        }

        return properties;
    }

    private static Property addAndGetProperty(final String argText, final Property mainProp, final ValidProperties properties) {
        final String [] parts = argText.split(":");
        final Property prop = new Property();
        prop.name = parts[0];
        prop.type = parts.length > 1 ? parts[1] : null;

        if ( ! prop.name.startsWith("-") ) {
            throw new IllegalArgumentException(String.format("Illegal name '%s', it should start with '-'", prop.name));
        }

        final String defaultType = mainProp == null ? null : mainProp.type;
        final String destArg = mainProp == null ? null : mainProp.name;

        properties.types.put(prop.name, prop.type == null ? defaultType : prop.type);
        properties.aliasToArg.put(prop.name, destArg == null ? prop.name : destArg);

        return prop;
    }

    private static class Property{
        public String name;
        public String type;
    }
}
