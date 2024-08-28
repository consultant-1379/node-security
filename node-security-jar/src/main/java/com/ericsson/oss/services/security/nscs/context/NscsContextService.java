/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.context;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import com.ericsson.oss.itpf.sdk.context.ContextService;
import com.ericsson.oss.services.security.nscs.command.util.NscsCommandHelper;

/**
 * Auxiliary class to wrap the Context Service provided by SFWK.
 */
public class NscsContextService {

    // CLI commands from script-engine and REST
    private static final String USER_ID_CONTEXT_KEY = "X-Tor-UserID";
    private static final String SOURCE_IP_ADDR_CONTEXT_KEY = "SOURCE_IP_ADDRESS";
    private static final String SESSION_ID_CONTEXT_KEY = "SESSION_ID";

    // PKI  API
    private static final String USER_NAME_CONTEXT_KEY = "User.Name";

    // NSCS internal for Compact Audit Log
    private static final String COMMAND_TEXT_CONTEXT_KEY = "COMMAND_TEXT";
    private static final String COMMAND_TYPE_CONTEXT_KEY = "COMMAND_TYPE";
    private static final String NUM_VALID_ITEMS_CONTEXT_KEY = "NUM_VALID_ITEMS";
    private static final String NUM_SKIPPED_ITEMS_CONTEXT_KEY = "NUM_SKIPPED_ITEMS";
    private static final String NUM_INVALID_ITEMS_CONTEXT_KEY = "NUM_INVALID_ITEMS";
    private static final String NUM_SUCCESS_ITEMS_CONTEXT_KEY = "NUM_SUCCESS_ITEMS";
    private static final String NUM_FAILED_ITEMS_CONTEXT_KEY = "NUM_FAILED_ITEMS";
    private static final String JOB_ID_CONTEXT_KEY = "JOB_ID";
    private static final String PROXY_ACCOUNT_NAME_CONTEXT_KEY = "PROXY_ACCOUNT_NAME";
    private static final String REST_METHOD_CONTEXT_KEY = "REST_METHOD";
    private static final String REST_URL_FILE_CONTEXT_KEY = "REST_URL_FILE";
    private static final String REST_URL_PATH_CONTEXT_KEY = "REST_URL_PATH";
    private static final String REST_REQUEST_PAYLOAD_CONTEXT_KEY = "REST_REQUEST_PAYLOAD";
    private static final String ERROR_DETAIL_CONTEXT_KEY = "ERROR_DETAIL";

    // NSCS internal for Remote Ejb Logger

    private static final String CLASS_NAME_CONTEXT_KEY = "CLASS_NAME";

    private static final String METHOD_NAME_CONTEXT_KEY = "METHOD_NAME";

    private static final String INPUT_NODE_NAME_CONTEXT_KEY = "INPUT_NODE_NAME";


    private static final String SECADM_PREFIX = "secadm";

    @Inject
    private ContextService contextService;

    /**
     * Get the user ID from context.
     * 
     * @return the user ID.
     */
    public String getUserIdContextValue() {
        return contextService.getContextValue(USER_ID_CONTEXT_KEY);
    }

    /**
     * Set the user ID in the context.
     * 
     * @param userId
     *            the user ID.
     */
    public void setUserIdContextValue(final String userId) {
        contextService.setContextValue(USER_ID_CONTEXT_KEY, userId);
    }

    /**
     * Get the source IP address from context.
     * 
     * @return the source IP address.
     */
    public String getSourceIpAddrContextValue() {
        return contextService.getContextValue(SOURCE_IP_ADDR_CONTEXT_KEY);
    }

    /**
     * Set the source IP address in the context.
     * 
     * @param sourceIpAddr
     *            the source IP address.
     */
    public void setSourceIpAddrContextValue(final String sourceIpAddr) {
        contextService.setContextValue(SOURCE_IP_ADDR_CONTEXT_KEY, sourceIpAddr);
    }

    /**
     * Get the session ID from context.
     * 
     * @return the session ID.
     */
    public String getSessionIdContextValue() {
        return contextService.getContextValue(SESSION_ID_CONTEXT_KEY);
    }

    /**
     * Set the session ID in the context.
     * 
     * @param sessionId
     *            the session ID.
     */
    public void setSessionIdContextValue(final String sessionId) {
        contextService.setContextValue(SESSION_ID_CONTEXT_KEY, sessionId);
    }

    /**
     * Set the username in the context.
     * 
     * This is used when NSCS invokes PKI API.
     * 
     * @param username
     *            the username.
     */
    public void setUserNameContextValue(final String username) {
        contextService.setContextValue(USER_NAME_CONTEXT_KEY, username);
    }

    /**
     * Get the command text from context.
     * 
     * @return the command text.
     */
    public String getCommandTextContextValue() {
        return contextService.getContextValue(COMMAND_TEXT_CONTEXT_KEY);
    }

    /**
     * Set the command text in the context.
     * 
     * If the provided command text contains passwords or keys, they are obfuscated before setting the command text in the context.
     * 
     * @param commandText
     *            the command text.
     */
    public void setCommandTextContextValue(final String commandText) {
        final String obfuscatedCommandText = getObfuscatedCommandText(commandText);
        contextService.setContextValue(COMMAND_TEXT_CONTEXT_KEY, obfuscatedCommandText);
    }

    /**
     * Get the command type from context.
     * 
     * @return the command type.
     */
    public String getCommandTypeContextValue() {
        return contextService.getContextValue(COMMAND_TYPE_CONTEXT_KEY);
    }

    /**
     * Set the command type in the context.
     * 
     * @param commandType
     *            the command type.
     */
    public void setCommandTypeContextValue(final String commandType) {
        contextService.setContextValue(COMMAND_TYPE_CONTEXT_KEY, commandType);
    }

    /**
     * Get the num of valid items from context.
     * 
     * @return the num of valid items.
     */
    public Integer getNumValidItemsContextValue() {
        return contextService.getContextValue(NUM_VALID_ITEMS_CONTEXT_KEY);
    }

    /**
     * Set the num of valid items in the context.
     * 
     * @param numValidItems
     *            the num of valid items.
     */
    public void setNumValidItemsContextValue(final Integer numValidItems) {
        contextService.setContextValue(NUM_VALID_ITEMS_CONTEXT_KEY, numValidItems);
    }

    /**
     * Get the num of skipped items from context.
     * 
     * @return the num of skipped items.
     */
    public Integer getNumSkippedItemsContextValue() {
        return contextService.getContextValue(NUM_SKIPPED_ITEMS_CONTEXT_KEY);
    }

    /**
     * Set the num of skipped items in the context.
     * 
     * @param numSkippedItems
     *            the num of skipped items.
     */
    public void setNumSkippedItemsContextValue(final Integer numSkippedItems) {
        contextService.setContextValue(NUM_SKIPPED_ITEMS_CONTEXT_KEY, numSkippedItems);
    }

    /**
     * Get the num of invalid items from context.
     * 
     * @return the num of invalid items.
     */
    public Integer getNumInvalidItemsContextValue() {
        return contextService.getContextValue(NUM_INVALID_ITEMS_CONTEXT_KEY);
    }

    /**
     * Set the num of invalid items in the context.
     * 
     * @param numInvalidItems
     *            the num of invalid items.
     */
    public void setNumInvalidItemsContextValue(final Integer numInvalidItems) {
        contextService.setContextValue(NUM_INVALID_ITEMS_CONTEXT_KEY, numInvalidItems);
    }

    /**
     * Get the num of success items from context.
     * 
     * @return the num of success items.
     */
    public Integer getNumSuccessItemsContextValue() {
        return contextService.getContextValue(NUM_SUCCESS_ITEMS_CONTEXT_KEY);
    }

    /**
     * Set the num of success items in the context.
     * 
     * @param numSuccessItems
     *            the num of success items.
     */
    public void setNumSuccessItemsContextValue(final Integer numSuccessItems) {
        contextService.setContextValue(NUM_SUCCESS_ITEMS_CONTEXT_KEY, numSuccessItems);
    }

    /**
     * Get the num of failed items from context.
     * 
     * @return the num of failed items.
     */
    public Integer getNumFailedItemsContextValue() {
        return contextService.getContextValue(NUM_FAILED_ITEMS_CONTEXT_KEY);
    }

    /**
     * Set the num of failed items in the context.
     * 
     * @param numFailedItems
     *            the num of failed items.
     */
    public void setNumFailedItemsContextValue(final Integer numFailedItems) {
        contextService.setContextValue(NUM_FAILED_ITEMS_CONTEXT_KEY, numFailedItems);
    }

    /**
     * Get the job ID from context.
     * 
     * @return the job ID.
     */
    public UUID getJobIdContextValue() {
        return contextService.getContextValue(JOB_ID_CONTEXT_KEY);
    }

    /**
     * Set the job ID in the context.
     * 
     * @param jobId
     *            the job ID.
     */
    public void setJobIdContextValue(final UUID jobId) {
        contextService.setContextValue(JOB_ID_CONTEXT_KEY, jobId);
    }

    /**
     * Get the proxy account name from context.
     * 
     * @return the proxy account name.
     */
    public String getProxyAccountNameContextValue() {
        return contextService.getContextValue(PROXY_ACCOUNT_NAME_CONTEXT_KEY);
    }

    /**
     * Set the proxy account name in the context.
     * 
     * @param proxyAccountName
     *            the proxy account name.
     */
    public void setProxyAccountNameContextValue(final String proxyAccountName) {
        contextService.setContextValue(PROXY_ACCOUNT_NAME_CONTEXT_KEY, proxyAccountName);
    }

    /**
     * Get the rest method from context.
     * 
     * @return the rest method.
     */
    public String getRestMethodContextValue() {
        return contextService.getContextValue(REST_METHOD_CONTEXT_KEY);
    }

    /**
     * Set the rest method in the context.
     * 
     * @param restMethod
     *            the rest method.
     */
    public void setRestMethodContextValue(final String restMethod) {
        contextService.setContextValue(REST_METHOD_CONTEXT_KEY, restMethod);
    }

    /**
     * Get the rest URL file from context.
     * 
     * @return the rest URL file.
     */
    public String getRestUrlFileContextValue() {
        return contextService.getContextValue(REST_URL_FILE_CONTEXT_KEY);
    }

    /**
     * Set the rest URL file in the context.
     * 
     * @param restUrlFile
     *            the rest URL file.
     */
    public void setRestUrlFileContextValue(final String restUrlFile) {
        contextService.setContextValue(REST_URL_FILE_CONTEXT_KEY, restUrlFile);
    }

    /**
     * Get the rest URL path from context.
     * 
     * @return the rest URL path.
     */
    public String getRestUrlPathContextValue() {
        return contextService.getContextValue(REST_URL_PATH_CONTEXT_KEY);
    }

    /**
     * Set the rest URL path in the context.
     * 
     * @param restUrlPath
     *            the rest URL path.
     */
    public void setRestUrlPathContextValue(final String restUrlPath) {
        contextService.setContextValue(REST_URL_PATH_CONTEXT_KEY, restUrlPath);
    }

    /**
     * Get the rest request payload from context.
     * 
     * @return the rest request payload.
     */
    public String getRestRequestPayloadContextValue() {
        return contextService.getContextValue(REST_REQUEST_PAYLOAD_CONTEXT_KEY);
    }

    /**
     * Set the rest request payload in the context.
     * 
     * @param restRequestPayload
     *            the rest request payload.
     */
    public void setRestRequestPayloadContextValue(final String restRequestPayload) {
        contextService.setContextValue(REST_REQUEST_PAYLOAD_CONTEXT_KEY, restRequestPayload);
    }

    /**
     * Get the error detail from context.
     * 
     * @return the error detail.
     */
    public String getErrorDetailContextValue() {
        return contextService.getContextValue(ERROR_DETAIL_CONTEXT_KEY);
    }

    /**
     * Set the error detail in the context.
     * 
     * @param errorDetail
     *            the error detail.
     */
    public void setErrorDetailContextValue(final String errorDetail) {
        contextService.setContextValue(ERROR_DETAIL_CONTEXT_KEY, errorDetail);
    }

    /**
     * Get the class invocation name from context.
     *
     * @return the class name.
     */
    public String getClassNameContextValue() {
        return contextService.getContextValue(CLASS_NAME_CONTEXT_KEY);
    }

    /**
     * Set the class name in the context.
     *
     * @param className
     *            the class name.
     */
    public void setClassNameContextValue(final String className) {
        contextService.setContextValue(CLASS_NAME_CONTEXT_KEY, className);
    }

    /**
     * Get the method invocation name from context.
     *
     * @return the method name.
     */
    public String getMethodNameContextValue() {
        return contextService.getContextValue(METHOD_NAME_CONTEXT_KEY);
    }

    /**
     * Set the method name in the context.
     *
     * @param methodName
     *            the method name.
     */
    public void setMethodNameContextValue(final String methodName) {
        contextService.setContextValue(METHOD_NAME_CONTEXT_KEY, methodName);
    }

    /**
     * Get the input node name from context.
     *
     * @return the input node name.
     */
    public String getInputNodeNameContextValue() {
        return contextService.getContextValue(INPUT_NODE_NAME_CONTEXT_KEY);
    }

    /**
     * Set the input node name in the context.
     *
     * @param inputNodeName
     *            the node name.
     */
    public void setInputNodeNameContextValue(final String inputNodeName) {
        contextService.setContextValue(INPUT_NODE_NAME_CONTEXT_KEY, inputNodeName);
    }

    /**
     * Get all data from the context.
     * 
     * @return all context data.
     */
    public Map<String, Serializable> getContextData() {
        return contextService.getContextData();
    }

    /**
     * Initialize the items statistics in the context for a synchronous command.
     * 
     * This method shall be invoked for synchronous command handlers with distinct validation and execution phases.
     * 
     * This method shall be invoked after validating the command and before executing it on valid items or rejecting it for the presence of invalid
     * items (according to the rejection policy adopted by the specific command handler). Only the valid and invalid items are set at this step.
     * 
     * @param valid
     *            the num of valid items.
     * @param invalid
     *            the num of invalid items.
     */
    public void initItemsStatsForSyncCommand(final Integer valid, final Integer invalid) {
        setNumValidItemsContextValue(valid);
        setNumInvalidItemsContextValue(invalid);
    }

    /**
     * Update the items result statistics in the context for a synchronous command.
     * 
     * This method shall be invoked for synchronous command handlers with distinct validation and execution phases.
     * 
     * This method shall be invoked after executing it on valid items. Only the success and failed items are set at this step.
     * 
     * @param success
     *            the num of successful items.
     * @param failed
     *            the num of failed items.
     */
    public void updateItemsResultStatsForSyncCommand(final Integer success, final Integer failed) {
        setNumSuccessItemsContextValue(success);
        setNumFailedItemsContextValue(failed);
    }

    /**
     * Update the items statistics in the context for a synchronous command or a REST.
     * 
     * This method shall be invoked for synchronous command handlers without distinct validation and execution phases or for REST. The command handler
     * loops on items and executes the command for valid ones while invalid ones are skipped.
     * 
     * This method shall be invoked after validating the command and executing it on valid items. The valid, invalid, success and failed items are set
     * at this step.
     * 
     * @param valid
     *            the num of valid items.
     * @param invalid
     *            the num of invalid items.
     * @param success
     *            the num of successful items.
     * @param failed
     *            the num of failed items.
     */
    public void updateItemsStatsForSyncCommand(final Integer valid, final Integer invalid, final Integer success, final Integer failed) {
        setNumValidItemsContextValue(valid);
        setNumInvalidItemsContextValue(invalid);
        setNumSuccessItemsContextValue(success);
        setNumFailedItemsContextValue(failed);
    }

    /**
     * Initialize the items statistics in the context for an asynchronous command.
     * 
     * This method shall be invoked for asynchronous command handlers with distinct validation and job creation phases.
     * 
     * This method shall be invoked after validating the command and before creating a job for valid items or rejecting it for the presence of invalid
     * items (according to the rejection policy adopted by the specific command handler).
     * 
     * Only the valid and invalid items are set at this step.
     * 
     * The invalid items is needed when inserting the job since it is set in the job cache.
     * 
     * The valid items cannot be initialized to 0 here and then updated in the context when inserting the workflow batch in the inserted job since
     * sometimes such operation is performed asynchronously and the valid items is used in the command finished with success to understand if the CAL
     * log shall be performed.
     * 
     * 
     * @param valid
     *            the num of valid items.
     * @param invalid
     *            the num of invalid items.
     */
    public void initItemsStatsForAsyncCommand(final Integer valid, final Integer invalid) {
        initItemsStatsForSyncCommand(valid, invalid);
    }

    /**
     * Get the obfuscated command text.
     * 
     * @param commandText
     *            the command text.
     * @return the obfuscated command text.
     */
    private String getObfuscatedCommandText(final String commandText) {
        final String obfuscatedCommandText = NscsCommandHelper.obfuscateCommandText(commandText);
        if (obfuscatedCommandText != null && !obfuscatedCommandText.isEmpty()) {
            return String.format("%s %s", SECADM_PREFIX, obfuscatedCommandText);
        }
        return null;
    }
}
