package com.slambyte.util.xml;

public class InlineAttribute implements Cloneable	{
	private String name;
	private String value;

	public InlineAttribute(String name,String value)	{
		this.name = name;
		this.value = value;
	}

	public Object clone()	{
		try 	{
			InlineAttribute attr = (InlineAttribute) super.clone();
			return attr;
		}catch(CloneNotSupportedException e)	{
			return null;
		}
	}

	public void setValue(String value)	{
		if(value == null || value.isEmpty()) return;
		this.value = value;
	}

	public String getValue()	{
		return value;
	}

	public String getName()	{
		return name;
	}
}
