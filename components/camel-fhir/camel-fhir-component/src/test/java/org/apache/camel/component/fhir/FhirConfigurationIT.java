/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.fhir;

import java.util.List;

import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import org.apache.camel.CamelContext;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.fhir.internal.FhirApiCollection;
import org.apache.camel.component.fhir.internal.FhirCreateApiMethod;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link FhirConfiguration} APIs.
 */
@ExtendWith(MockitoExtension.class)
public class FhirConfigurationIT extends AbstractFhirTestSupport {

    private static final String PATH_PREFIX = FhirApiCollection.getCollection().getApiName(FhirCreateApiMethod.class).getName();

    private static final String TEST_URI = "fhir://" + PATH_PREFIX + "/resource?inBody=resourceAsString&log=true&"
                                           + "encoding=JSON&summary=TEXT&compress=true&username=art&password=tatum&sessionCookie=mycookie%3DChips%20Ahoy"
                                           + "&accessToken=token&serverUrl=http://localhost:8080/hapi-fhir-jpaserver-example/baseR4&fhirVersion=R4";
    private FhirConfiguration componentConfiguration;

    @Mock
    private IClientInterceptor mockClientInterceptor;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        final CamelContext context = new DefaultCamelContext(createCamelRegistry());

        // add FhirComponent to Camel context but don't set up componentConfiguration
        final FhirComponent component = new FhirComponent(context);
        FhirConfiguration fhirConfiguration = new FhirConfiguration();
        fhirConfiguration.setLog(true);
        fhirConfiguration.setEncoding("JSON");
        fhirConfiguration.setSummary("TEXT");
        fhirConfiguration.setCompress(true);
        fhirConfiguration.setUsername("art");
        fhirConfiguration.setPassword("tatum");
        fhirConfiguration.setSessionCookie("mycookie=Chips Ahoy");
        fhirConfiguration.setAccessToken("token");
        fhirConfiguration.setServerUrl("http://localhost:8080/hapi-fhir-jpaserver-example/baseR4");
        fhirConfiguration.setFhirVersion("R4");
        component.setConfiguration(fhirConfiguration);

        final IGenericClient client = component.createClient(fhirConfiguration);
        fhirConfiguration.setClient(client);
        client.registerInterceptor(this.mockClientInterceptor);

        this.componentConfiguration = fhirConfiguration;
        context.addComponent("fhir", component);

        return context;
    }

    @Test
    public void testConfiguration() throws Exception {
        FhirEndpoint endpoint = getMandatoryEndpoint(TEST_URI, FhirEndpoint.class);
        GenericClient client = (GenericClient) endpoint.getClient();
        FhirConfiguration configuration = endpoint.getConfiguration();
        assertEquals(this.componentConfiguration, configuration);
        assertEquals("http://localhost:8080/hapi-fhir-jpaserver-example/baseR4", client.getUrlBase());
        assertEquals(EncodingEnum.JSON, client.getEncoding());
        assertEquals(SummaryEnum.TEXT, client.getSummary());
        List<Object> interceptors = client.getInterceptorService().getAllRegisteredInterceptors();
        assertEquals(6, interceptors.size());

        assertTrue(interceptors.contains(this.mockClientInterceptor), "User defined IClientInterceptor not found");

        long counter = context.adapt(ExtendedCamelContext.class).getBeanIntrospection().getInvokedCounter();
        assertEquals(0, counter, "Should not use reflection");
    }

    @Override
    public void cleanFhirServerState() {
        // do nothing
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct://CONFIGURATION").to(TEST_URI);
            }
        };
    }
}
