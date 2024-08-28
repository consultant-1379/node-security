package com.ericsson.nms.security.nscs.cpp.level;

/**
 * CommandFactory is responsible to create the required command handler for the typed in command
 * 
 * @author eabdsin
 * 
 */
public interface SecurityLevelProcessorFactory {
    /**
     * The method is responsible to create the SecLevelProcessor according to request
     * 
     * @param secLevelRequest the secLevelRequest
     *
     * @return Instance of SecLevelProcessor
     */
    SecLevelProcessor createSecLevelProcessor(SecLevelRequest secLevelRequest);
}