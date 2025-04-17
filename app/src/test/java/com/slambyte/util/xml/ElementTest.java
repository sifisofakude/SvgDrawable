package com.slambyte.util.xml;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElementTest	{
	@Test
	public void coreElementTest()	{
		Element element = new Element("webster");
		assertEquals("webster",element.getName());

		Element child = new Element("child");
		element.addChild(child);

		assertEquals(true,child == element.getChildAt(0));

		child.setName("burp");
		assertEquals(true,element.getChildAt(0).getName().equals(child.getName()));

	}
}