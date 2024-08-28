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
package com.ericsson.oss.services.nscs.workflow.serializer.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.List;

public class NscsObjectSerializer {

	public static <T extends Serializable> String writeObject(T object)
			throws IOException {
		String serialized = null;
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			serialized = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
		} catch (IOException e) {
			throw e;
		}
		return serialized;
	}

        public static <T extends Serializable> String writeObject(List<T> object)
                        throws IOException {
                String serialized = null;
                try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                                        byteArrayOutputStream);
                        objectOutputStream.writeObject(object);
                        objectOutputStream.close();
            serialized = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
                } catch (IOException e) {
                        throw e;
                }
                return serialized;
        }

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T readObject(String serialized) {
		T object = null;
		byte[] bytes = null;
		try {
			bytes = Base64.getDecoder().decode(serialized);
		} catch (Exception e) {
			// Do nothing
		}

		if (bytes != null) {
			try {
				ObjectInputStream objectInputStream = new ObjectInputStream(
						new ByteArrayInputStream(bytes));
				object = (T) objectInputStream.readObject();
			} catch (IOException e) {
				// Do nothing
			} catch (ClassNotFoundException e) {
				// Do nothing
			} catch (ClassCastException e) {
				// Do nothing
			}
		}
		return object;
	}
}
