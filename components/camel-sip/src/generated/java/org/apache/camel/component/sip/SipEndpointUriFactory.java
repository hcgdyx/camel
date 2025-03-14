/* Generated by camel build tools - do NOT edit this file! */
package org.apache.camel.component.sip;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.camel.spi.EndpointUriFactory;

/**
 * Generated by camel build tools - do NOT edit this file!
 */
public class SipEndpointUriFactory extends org.apache.camel.support.component.EndpointUriFactorySupport implements EndpointUriFactory {

    private static final String BASE = ":uri";
    private static final String[] SCHEMES = new String[]{"sip", "sips"};

    private static final Set<String> PROPERTY_NAMES;
    private static final Set<String> SECRET_PROPERTY_NAMES;
    private static final Set<String> MULTI_VALUE_PREFIXES;
    static {
        Set<String> props = new HashSet<>(45);
        props.add("addressFactory");
        props.add("bridgeErrorHandler");
        props.add("cacheConnections");
        props.add("callIdHeader");
        props.add("consumer");
        props.add("contactHeader");
        props.add("contentSubType");
        props.add("contentType");
        props.add("contentTypeHeader");
        props.add("eventHeader");
        props.add("eventHeaderName");
        props.add("eventId");
        props.add("exceptionHandler");
        props.add("exchangePattern");
        props.add("expiresHeader");
        props.add("extensionHeader");
        props.add("fromHeader");
        props.add("fromHost");
        props.add("fromPort");
        props.add("fromUser");
        props.add("headerFactory");
        props.add("implementationDebugLogFile");
        props.add("implementationServerLogFile");
        props.add("implementationTraceLevel");
        props.add("lazyStartProducer");
        props.add("listeningPoint");
        props.add("maxForwards");
        props.add("maxForwardsHeader");
        props.add("maxMessageSize");
        props.add("messageFactory");
        props.add("msgExpiration");
        props.add("presenceAgent");
        props.add("receiveTimeoutMillis");
        props.add("sipFactory");
        props.add("sipStack");
        props.add("sipUri");
        props.add("stackName");
        props.add("toHeader");
        props.add("toHost");
        props.add("toPort");
        props.add("toUser");
        props.add("transport");
        props.add("uri");
        props.add("useRouterForAllUris");
        props.add("viaHeaders");
        PROPERTY_NAMES = Collections.unmodifiableSet(props);
        SECRET_PROPERTY_NAMES = Collections.emptySet();
        MULTI_VALUE_PREFIXES = Collections.emptySet();
    }

    @Override
    public boolean isEnabled(String scheme) {
        for (String s : SCHEMES) {
            if (s.equals(scheme)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String buildUri(String scheme, Map<String, Object> properties, boolean encode) throws URISyntaxException {
        String syntax = scheme + BASE;
        String uri = syntax;

        Map<String, Object> copy = new HashMap<>(properties);

        uri = buildPathParameter(syntax, uri, "uri", null, true, copy);
        uri = buildQueryParameters(uri, copy, encode);
        return uri;
    }

    @Override
    public Set<String> propertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public Set<String> secretPropertyNames() {
        return SECRET_PROPERTY_NAMES;
    }

    @Override
    public Set<String> multiValuePrefixes() {
        return MULTI_VALUE_PREFIXES;
    }

    @Override
    public boolean isLenientProperties() {
        return false;
    }
}

