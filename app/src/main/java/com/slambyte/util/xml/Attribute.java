package com.slambyte.util.xml;

public class Attribute implements Cloneable	{
	private String name;
	private String value;

	public Attribute(String name,String value)	{
		this.name = name;
		this.value = value;
	}

	public Object clone()	{
		try 	{
			Attribute attr = (Attribute) super.clone();
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
