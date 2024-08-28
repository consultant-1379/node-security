package com.ericsson.nms.security.nscs.cpp.level;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.cpp.level.processor.qualifiers.SecurityLevelRequestType;

/**
 * SecurityLevelProcessorFactory default implementation
 *
 * @see SecurityLevelProcessorFactory {@inheritDoc}
 * 
 * @author eabdsin
 * 
 */
public class SecurityLevelProcessorFactoryImpl implements SecurityLevelProcessorFactory {

    @SecurityLevelRequestType(SecLevelRequestType.DEACTIVATE_SECURITY_LEVEL)
    @Inject
    SecLevelProcessor deactivateStatusCommandHandler;

    @SecurityLevelRequestType(SecLevelRequestType.ACTIVATE_SECURITY_LEVEL)
    @Inject
    SecLevelProcessor activateStatusCommandHandler;

    @Inject
    Logger log;

    @Override
    public SecLevelProcessor createSecLevelProcessor(final SecLevelRequest commandObject) {

        SecLevelProcessor secLevelProcessor = null;

        final SecLevelRequestType secLevelRequestType = commandObject.getSecLevelRequestType();
        
        switch (secLevelRequestType) {

            case ACTIVATE_SECURITY_LEVEL:
                secLevelProcessor = activateStatusCommandHandler;
                break;

            case DEACTIVATE_SECURITY_LEVEL:
                secLevelProcessor = deactivateStatusCommandHandler;
                break;
            default:
                throw new IllegalArgumentException("Invalid Security level request type");
        }
        
        log.info("SecurityLevelProcessorFactoryImpl, Security level processor created : {}", secLevelProcessor);
        
        return secLevelProcessor;
    }
}