/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.laad;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

/**
 * Formatter cable of creating a versioned XML with LAAD information
 * @author enatbol
 */
public class LAADVersionFormatter extends Formatter {

    private static final SimpleDateFormat DATE = new SimpleDateFormat("yyMMddHHmmssZ");
    private final Properties transProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(LAADVersionFormatter.class);

    public LAADVersionFormatter() {
        transProperties = new Properties();
        transProperties.setProperty(OutputKeys.INDENT, "yes");
        transProperties.setProperty(OutputKeys.ENCODING, "US-ASCII");
        transProperties.setProperty(OutputKeys.METHOD, "xml");
        transProperties.setProperty(OutputKeys.DOCTYPE_SYSTEM, "laadver0.dtd");
    }

    @Override
    void formateAuthenticationData(final OutputStream os, final LAADData[] laadData, final int fileVersion) throws FormatterException {
    	LOGGER.info("LAADVersionFormatter.formateAuthenticationData() was called");
    	Document document;
        try {
            document = domDocument.initDomDocument();
        } catch (final ParserConfigurationException error) {
            /*
             * Replace with proper error handling/logging
             */
            final String message = "DomDocument Initialization Error";
            LOGGER.error("LAADVersionFormatter.formateAuthenticationData() {} with ParserConfigurationException message [{}]", message, error.getMessage());

            throw new FormatterException(message, error);
        }

        /*
         * Creating header to DomDocument
         */

        final Element rootElement = document.createElement("LocalAuthenticationFile");
        document.appendChild(rootElement);

        final Element fileType = document.createElement("fileType");
        rootElement.appendChild(fileType);
        final Text fileTypeText = document.createTextNode("1.3.6.1.4.1.193.140.5.1");
        fileType.appendChild(fileTypeText);

        final Element versionElement = document.createElement("version");
        rootElement.appendChild(versionElement);
        final Text versionText = document.createTextNode(Integer.toString(fileVersion));
        versionElement.appendChild(versionText);

        final Element timeStampElement = document.createElement("timestamp");
        rootElement.appendChild(timeStampElement);
        final String timeStampText = DATE.format(new Date(System.currentTimeMillis()));
        final Text timeStampTextElement = document.createTextNode(timeStampText);
        timeStampElement.appendChild(timeStampTextElement);

        final Element userEntriesElement = document.createElement("userEntries");
        rootElement.appendChild(userEntriesElement);

        /*
         * Populating user enteries
         */

        if (laadData != null) {
            for (int i = 0; i < laadData.length; i++) {
                final LAADData laadDataEntry = laadData[i];

                final Element userEntryElement = document.createElement("UserEntry");
                userEntriesElement.appendChild(userEntryElement);

                final Element userId = document.createElement("userId");
                userEntryElement.appendChild(userId);
                final Text userIdText = document.createTextNode(laadDataEntry.getUserId());
                userId.appendChild(userIdText);

                final Element authenticatorsElement = document.createElement("authenticators");
                userEntryElement.appendChild(authenticatorsElement);

                final Element hashIdentifier = document.createElement("shadowMD5");
                authenticatorsElement.appendChild(hashIdentifier);
                final char[] hashIdEntry = laadDataEntry.getPasswordHash();
                final String hashIdentifierEntry = new String(hashIdEntry);
                final Text hashIdentifierText = document.createTextNode(hashIdentifierEntry);
                hashIdentifier.appendChild(hashIdentifierText);
            }

            try {
                domDocument.writeDomDocument(document, transProperties, os);
            } catch (TransformerFactoryConfigurationError | TransformerException error) {
                LOGGER.error("LAADVersionFormatter.formateAuthenticationData() Exception message [{}]", error.getMessage());
                throw new FormatterException("", error);
            }
        }
        LOGGER.debug("LAADVersionFormatter.formateAuthenticationData() exiting");
    }

    @Override
    void formateAuthorizationData(final OutputStream os, final LAADData[] laadData, final int fileVersion) throws FormatterException {
    	LOGGER.info("LAADVersionFormatter.formateAuthorizationData() was called");
        Document document;
        try {
            document = domDocument.initDomDocument();
        } catch (final ParserConfigurationException error) {
            throw new FormatterException("", error);
        }

        final Element rootElement = document.createElement("LocalAuthorizationFile");
        document.appendChild(rootElement);

        final Element fileType = document.createElement("fileType");
        rootElement.appendChild(fileType);
        final Text fileTypeText = document.createTextNode("1.3.6.1.4.1.192.140.5.2");
        fileType.appendChild(fileTypeText);

        final Element versionElement = document.createElement("version");
        rootElement.appendChild(versionElement);
        final Text versionText = document.createTextNode(Integer.toString(fileVersion));
        versionElement.appendChild(versionText);

        final Element timeStampElement = document.createElement("timestamp");
        rootElement.appendChild(timeStampElement);
        final String timeStampText = DATE.format(new Date(System.currentTimeMillis()));
        final Text timeStampTextElement = document.createTextNode(timeStampText);
        timeStampElement.appendChild(timeStampTextElement);

        final Element authEntriesElement = document.createElement("authEntries");
        rootElement.appendChild(authEntriesElement);

        if (laadData != null) {
            for (int i = 0; i < laadData.length; i++) {
                final LAADData laadDataEntry = laadData[i];

                final Element authEntryElement = document.createElement("AuthEntry");
                authEntriesElement.appendChild(authEntryElement);

                final Element userId = document.createElement("userId");
                authEntryElement.appendChild(userId);
                final Text userIdText = document.createTextNode(laadDataEntry.getUserId());
                userId.appendChild(userIdText);

                final Element isAuthorizedFor = document.createElement("isAuthorizedFor");
                authEntryElement.appendChild(isAuthorizedFor);

                final String[] taskProfiles = laadDataEntry.getTaskProfiles();

                if (taskProfiles != null) {
                    for (int j = 0; j < taskProfiles.length; j++) {
                        final String taskProfile = taskProfiles[j];

                        final Element taskProfileElement = document.createElement("TaskProfile");
                        isAuthorizedFor.appendChild(taskProfileElement);
                        final Text taskProfileText = document.createTextNode(taskProfile);
                        taskProfileElement.appendChild(taskProfileText);
                    }
                }
            }
        }

        try {
            domDocument.writeDomDocument(document, transProperties, os);
        } catch (TransformerFactoryConfigurationError | TransformerException error) {
            LOGGER.error("LAADVersionFormatter.formateAuthorizationData() Exception message [{}]", error.getMessage());
            throw new FormatterException(error.getMessage(), error);
        }
        LOGGER.debug("LAADVersionFormatter.formateAuthorizationData() exiting");
    }

}
