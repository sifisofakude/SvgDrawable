/*
 * Copyright 2025 Sifiso Fakude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
