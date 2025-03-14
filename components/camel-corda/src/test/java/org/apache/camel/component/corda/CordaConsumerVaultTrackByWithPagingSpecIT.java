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
package org.apache.camel.component.corda;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.apache.camel.component.corda.CordaConstants.OPERATION;
import static org.apache.camel.component.corda.CordaConstants.VAULT_TRACK_BY_WITH_PAGING_SPEC;

@Disabled("Requires manual steps to setup and run")
public class CordaConsumerVaultTrackByWithPagingSpecIT extends CordaConsumerITSupport {

    @Test
    public void vaultTrackByWithPagingSpecTest() throws Exception {
        mockResult.expectedMinimumMessageCount(1);
        mockError.expectedMessageCount(0);
        MockEndpoint.assertIsSatisfied(context);
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                errorHandler(deadLetterChannel("mock:error"));

                from(getUrl() + "&" + OPERATION.toLowerCase() + "=" + VAULT_TRACK_BY_WITH_PAGING_SPEC
                     + "&contractStateClass=#contractStateClass"
                     + "&queryCriteria=#queryCriteria"
                     + "&pageSpecification=#pageSpecification")
                             .to("mock:result");
            }
        };
    }
}
