/*------------------------------------------------------------------------------
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
package com.ericsson.nms.security.nscs.integration.jee.test.command;

public interface FtpesTests {

    void activateFtpesCommandShouldInformWhenSpecifiedNodeNotExists() throws Exception;

    void deactivateFtpesCommandShouldInformWhenSpecifiedNodeNotExists() throws Exception;

    void activateFtpesCommandShouldInformWhenSpecifiedNodeIsNotSynchronized() throws Exception;

    void deactivateFtpesCommandShouldInformWhenSpecifiedNodeIsNotSynchronized() throws Exception;

    void activateFtpesCommandShouldInformWhenNodeTypeIsWrong() throws Exception;

    void deactivateFtpesCommandShouldInformWhenNodeTypeIsWrong() throws Exception;
}
