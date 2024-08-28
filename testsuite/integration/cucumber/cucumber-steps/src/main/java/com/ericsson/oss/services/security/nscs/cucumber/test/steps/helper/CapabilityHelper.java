/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2018
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/

package com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CapabilityHelper {

    /**
     * Gets the target definitions from JSON file of given filename.
     *
     * @param fileName
     *            the file name (extension excluded)
     * @return the expected targets
     */
    public List<NscsTargetDefinition> getTargetDefinitions(final String fileName) {
        List<NscsTargetDefinition> expected = new ArrayList<NscsTargetDefinition>();
        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName + ".json");
        Assert.assertNotNull("null input stream.", inputStream);
        final ObjectMapper readMapper = new ObjectMapper();
        final TypeReference<List<NscsTargetDefinition>> typeRef = new TypeReference<List<NscsTargetDefinition>>() {
        };
        try {
            expected = readMapper.readValue(inputStream, typeRef);
        } catch (final IOException e) {
            Assert.fail("I/O exception reading input stream : " + e.getMessage());
        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (final IOException e) {
            Assert.fail("I/O exception closing input stream : " + e.getMessage());
        }
        return expected;
    }

    /**
     * Gets the capability definitions from JSON file of given filename.
     *
     * @param fileName
     *            the file name (extension excluded)
     * @return the expected capabilities
     */
    public List<NscsCapabilityDefinition> getCapabilityDefinitions(final String fileName) {
        List<NscsCapabilityDefinition> expected = new ArrayList<NscsCapabilityDefinition>();
        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName + ".json");
        Assert.assertNotNull("null input stream.", inputStream);
        final ObjectMapper readMapper = new ObjectMapper();
        final TypeReference<List<NscsCapabilityDefinition>> typeRef = new TypeReference<List<NscsCapabilityDefinition>>() {
        };
        try {
            expected = readMapper.readValue(inputStream, typeRef);
        } catch (final IOException e) {
            Assert.fail("I/O exception reading input stream : " + e.getMessage());
        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (final IOException e) {
            Assert.fail("I/O exception closing input stream : " + e.getMessage());
        }
        return expected;
    }

    /**
     * Gets the capability support definitions from JSON file of given filename.
     * 
     * @param fileName
     *            the file name (extension excluded)
     * @return the expected capability supports
     */
    public List<NscsCapabilitySupportDefinition> getCapabilitySupportDefinitions(final String fileName) {
        List<NscsCapabilitySupportDefinition> expected = new ArrayList<NscsCapabilitySupportDefinition>();
        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName + ".json");
        Assert.assertNotNull("null input stream.", inputStream);
        final ObjectMapper readMapper = new ObjectMapper();
        final TypeReference<List<NscsCapabilitySupportDefinition>> typeRef = new TypeReference<List<NscsCapabilitySupportDefinition>>() {
        };
        try {
            expected = readMapper.readValue(inputStream, typeRef);
        } catch (final IOException e) {
            Assert.fail("I/O exception reading input stream : " + e.getMessage());
        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (final IOException e) {
            Assert.fail("I/O exception closing input stream : " + e.getMessage());
        }
        return expected;
    }

}
