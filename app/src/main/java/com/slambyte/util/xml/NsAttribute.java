package com.slambyte.util.xml;

public class NsAttribute implements Cloneable	{
	private String ns;
	private String name;
	private String value;

	public NsAttribute(String ns,String name,String value)	{
		this.ns = ns;
		this.name = name;
		this.value = value;
	}

	public Object clone()	{
		try 	{
			NsAttribute attr = (NsAttribute) super.clone();
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

	public String getNs()	{
		return ns;
	}

	public String getName()	{
		return name;
	}
}
