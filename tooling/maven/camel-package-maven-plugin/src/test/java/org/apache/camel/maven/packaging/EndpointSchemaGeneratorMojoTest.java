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
package org.apache.camel.maven.packaging;

import java.nio.file.Paths;
import java.util.List;

import org.apache.camel.maven.packaging.endpoint.SomeEndpoint;
import org.apache.camel.maven.packaging.endpoint.SomeEndpointUsingEnumConstants;
import org.apache.camel.maven.packaging.endpoint.SomeEndpointWithBadHeaders;
import org.apache.camel.maven.packaging.endpoint.SomeEndpointWithFilter;
import org.apache.camel.maven.packaging.endpoint.SomeEndpointWithJavadocAsDescription;
import org.apache.camel.maven.packaging.endpoint.SomeEndpointWithoutHeaders;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.tooling.model.ComponentModel;
import org.apache.camel.tooling.model.ComponentModel.EndpointHeaderModel;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The unit test of {@link EndpointSchemaGeneratorMojo}.
 */
class EndpointSchemaGeneratorMojoTest {

    private final EndpointSchemaGeneratorMojo mojo = new EndpointSchemaGeneratorMojo();
    private final ComponentModel model = new ComponentModel();

    @BeforeEach
    void init() throws Exception {
        mojo.sourceRoots = List.of(
                Paths.get(EndpointSchemaGeneratorMojoTest.class.getResource("/").toURI())
                        .resolve("../../src/test/java/"));
        mojo.project = new MavenProject();
    }

    @ParameterizedTest
    @ValueSource(classes = { SomeEndpoint.class, SomeEndpointUsingEnumConstants.class })
    void testCanRetrieveMetadataOfHeaders(Class<?> clazz) {
        mojo.addEndpointHeaders(model, clazz.getAnnotation(UriEndpoint.class), "some");
        List<EndpointHeaderModel> endpointHeaders = model.getEndpointHeaders();
        assertEquals(3, endpointHeaders.size());
        // Full
        EndpointHeaderModel headerFull = endpointHeaders.get(0);
        assertEquals("header", headerFull.getKind());
        assertEquals("KEY_FULL", headerFull.getName());
        assertEquals("key full desc", headerFull.getDescription());
        assertEquals("my display name", headerFull.getDisplayName());
        assertEquals("org.apache.camel.maven.packaging.endpoint.SomeEndpoint$MyEnum", headerFull.getJavaType());
        assertTrue(headerFull.isRequired());
        assertEquals("VAL1", headerFull.getDefaultValue());
        assertTrue(headerFull.isDeprecated());
        assertEquals("my deprecated note", headerFull.getDeprecationNote());
        assertTrue(headerFull.isSecret());
        assertEquals("my label", headerFull.getLabel());
        assertEquals(3, headerFull.getEnums().size());
        assertEquals("my label", headerFull.getGroup());
        // Empty
        EndpointHeaderModel headerEmpty = endpointHeaders.get(1);
        assertEquals("header", headerEmpty.getKind());
        assertEquals("KEY_EMPTY", headerEmpty.getName());
        assertTrue(headerEmpty.getDescription().isEmpty());
        assertTrue(headerEmpty.getDisplayName().isEmpty());
        assertTrue(headerEmpty.getJavaType().isEmpty());
        assertFalse(headerEmpty.isRequired());
        assertInstanceOf(String.class, headerEmpty.getDefaultValue());
        assertTrue(((String) headerEmpty.getDefaultValue()).isEmpty());
        assertFalse(headerEmpty.isDeprecated());
        assertTrue(headerEmpty.getDeprecationNote().isEmpty());
        assertFalse(headerEmpty.isSecret());
        assertTrue(headerEmpty.getLabel().isEmpty());
        assertNull(headerEmpty.getEnums());
        assertEquals("common", headerEmpty.getGroup());
        // Empty with Javadoc as description
        EndpointHeaderModel headerEmptyWithJavadoc = endpointHeaders.get(2);
        assertEquals("header", headerEmptyWithJavadoc.getKind());
        assertEquals("KEY_EMPTY_WITH_JAVA_DOC", headerEmptyWithJavadoc.getName());
        assertEquals("Some description", headerEmptyWithJavadoc.getDescription());
        assertTrue(headerEmptyWithJavadoc.getDisplayName().isEmpty());
        assertTrue(headerEmptyWithJavadoc.getJavaType().isEmpty());
        assertFalse(headerEmptyWithJavadoc.isRequired());
        assertInstanceOf(String.class, headerEmptyWithJavadoc.getDefaultValue());
        assertTrue(((String) headerEmptyWithJavadoc.getDefaultValue()).isEmpty());
        assertFalse(headerEmptyWithJavadoc.isDeprecated());
        assertTrue(headerEmptyWithJavadoc.getDeprecationNote().isEmpty());
        assertFalse(headerEmptyWithJavadoc.isSecret());
        assertTrue(headerEmptyWithJavadoc.getLabel().isEmpty());
        assertNull(headerEmptyWithJavadoc.getEnums());
        assertEquals("common", headerEmptyWithJavadoc.getGroup());
    }

    @Test
    void testHeadersNotProperlyDefinedAreIgnored() {
        mojo.addEndpointHeaders(model, SomeEndpointWithBadHeaders.class.getAnnotation(UriEndpoint.class), "some");
        assertEquals(0, model.getEndpointHeaders().size());
    }

    @Test
    void testEndpointWithoutHeadersAreIgnored() {
        mojo.addEndpointHeaders(model, SomeEndpointWithoutHeaders.class.getAnnotation(UriEndpoint.class), "some");
        assertEquals(0, model.getEndpointHeaders().size());
    }

    @Test
    void testEndpointWithFilterKeepOnlyApplicableHeaders() {
        mojo.addEndpointHeaders(model, SomeEndpointWithFilter.class.getAnnotation(UriEndpoint.class), "some");
        List<EndpointHeaderModel> endpointHeaders = model.getEndpointHeaders();
        assertEquals(2, endpointHeaders.size());
        for (int i = 0; i < endpointHeaders.size(); i++) {
            EndpointHeaderModel headerEmpty = endpointHeaders.get(i);
            assertEquals("header", headerEmpty.getKind());
            assertEquals(String.format("keep-%d", i + 1), headerEmpty.getName());
        }
    }

    @Test
    void testEndpointWithCleanedJavadocAsDescription() {
        mojo.addEndpointHeaders(model, SomeEndpointWithJavadocAsDescription.class.getAnnotation(UriEndpoint.class), "some");
        mojo.enhanceComponentModel(model, null, "", "");
        List<EndpointHeaderModel> endpointHeaders = model.getEndpointHeaders();
        assertEquals(1, endpointHeaders.size());
        EndpointHeaderModel headerEmpty = endpointHeaders.get(0);
        assertEquals("header", headerEmpty.getKind());
        assertEquals("no-description", headerEmpty.getName());
        assertEquals("Some description about NO_DESCRIPTION.", headerEmpty.getDescription());
    }
}
