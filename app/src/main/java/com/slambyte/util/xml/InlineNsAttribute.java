package com.slambyte.util.xml;

public class InlineNsAttribute implements Cloneable	{
	private String ns;
	private String name;
	private String value;

	public InlineNsAttribute(String ns,String name,String value)	{
		this.ns = ns;
		this.name = name;
		this.value = value;
	}

	@Override
	public Object clone()	{
		try 	{
			InlineNsAttribute attr = (InlineNsAttribute) super.clone();
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

	public String getNs()	{
		return ns;
	}
}
