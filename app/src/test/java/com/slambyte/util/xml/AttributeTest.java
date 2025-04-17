package com.slambyte.util.xml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeTest	{
	@Test
	@DisplayName("Attribute Test Passed")
	public void attributeTest()	{
		Attribute attr = new Attribute("id","197");

		assertEquals("id",attr.getName());
		assertEquals("197",attr.getValue());
		assertEquals(true,attr != (Attribute) attr.clone());
	}

	@Test
	@DisplayName("Namespace Attribute Test Passed")
	public void nsAttributeTest()	{
		NsAttribute attr = new NsAttribute("android","name","197");

		assertEquals("android",attr.getNs());
		assertEquals("name",attr.getName());
		assertEquals("197",attr.getValue());
		assertEquals(true,attr != (NsAttribute) attr.clone());
	}
}