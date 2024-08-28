package com.ericsson.nms.security.nscs.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ericsson.nms.security.nscs.api.exception.InvalidNodeProductDataException;

public class NodeProductDataTest {

	  @Test(expected=InvalidNodeProductDataException.class)
	  public void testNullNodeProductData() {
		  NodeProductData nodeProductData = new NodeProductData(null);
		  assertFalse(nodeProductData.isValid());
	  }

	  @Test(expected=InvalidNodeProductDataException.class)
	  public void testEmptyNodeProductData() {
		  NodeProductData nodeProductData = new NodeProductData("");
		  assertFalse(nodeProductData.isValid());
	  }

	  @Test(expected=InvalidNodeProductDataException.class)
	  public void testNodeProductDataWithInvalidIdentity() {
		  NodeProductData nodeProductData = new NodeProductData("1234/2-R13NV");
		  assertFalse(nodeProductData.isValid());
	  }

	  @Test(expected=InvalidNodeProductDataException.class)
	  public void testNodeProductDataWithInvalidIdentity2() {
		  NodeProductData nodeProductData = new NodeProductData("CXP1234-R13NV");
		  assertFalse(nodeProductData.isValid());
	  }

	  @Test(expected=InvalidNodeProductDataException.class)
	  public void testNodeProductDataWithInvalidRevision() {
		  NodeProductData nodeProductData = new NodeProductData("CXP1234/2-13NV");
		  assertFalse(nodeProductData.isValid());
	  }

	  @Test(expected=InvalidNodeProductDataException.class)
	  public void testNodeProductDataWithInvalidRevision2() {
		  NodeProductData nodeProductData = new NodeProductData("CXP1234/2-R");
		  assertFalse(nodeProductData.isValid());
	  }

	  @Test(expected=InvalidNodeProductDataException.class)
	  public void testInvalidNodeProductData() {
		  NodeProductData nodeProductData = new NodeProductData("CXP1234/2R13NV");
		  assertFalse(nodeProductData.isValid());
	  }

	  @Test
	  public void testValidNodeProductData() {
		  NodeProductData nodeProductData = new NodeProductData("CXP1234/2-R13NV");
		  assertTrue(nodeProductData.isValid());
		  assertTrue("CXP1234/2".equals(nodeProductData.getIdentity()));
		  assertTrue("R13NV".equals(nodeProductData.getRevision()));
	  }
}
