/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.gdpr.anonymize;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GdprHashingTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(GdprHashingTest.class);
	private static final String IMEI_IMSI_FILENAME_TEST = "IMSI_IMEI_TEST_FILENAME";

	GdprHashing gdprHashingTest;

	@Before
	public void setup(){
		gdprHashingTest = new GdprHashing();
	}

	@Test
	public void gdprBuildHashingTest() throws NoSuchAlgorithmException {
		/* Base64 SHA-256 has 256 bit len grouped 6 bits that has to be muliple of 3.
		   So there are ceiling(256/6) = 43 chars + \0 = 44 chars
		   A pad value : '=' must be added to have 45 chars near ceiled multiple of 3
		 */
		final int  hashedLength = 44;

		final String hashed = gdprHashingTest.gdprBuildHashing(IMEI_IMSI_FILENAME_TEST, GdprConstantsUtils.GDPR_HASH_ALGO);
		LOGGER.debug("Hashed : {}",hashed);

		/* check if hashed value contains zeo or more numbers,letters or characters -=_ */
		boolean hashcheck = (hashed.matches("[a-zA-Z0-9]*") || hashed.matches(".*[-=_].*"));

		assertEquals(hashed.length(), hashedLength);
		assertTrue(hashcheck);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void gdprBuildHashingExceptionTest() throws NoSuchAlgorithmException {
		String fakeAlgorithm = "SHA_NOT _VALID";
		String hashed = gdprHashingTest.gdprBuildHashing(IMEI_IMSI_FILENAME_TEST, fakeAlgorithm);
		LOGGER.debug("Hashed : {}",hashed);

		/* check if hashed value contains zeo or more numbers,letters or characters -=_ */
		boolean hashcheck = (hashed.matches("[a-zA-Z0-9]*") || hashed.matches(".*[-=_].*"));

	}

}
