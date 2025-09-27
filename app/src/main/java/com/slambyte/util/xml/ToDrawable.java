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

import java.util.ArrayList;

public class ToDrawable	{
	Document document;
	public ToDrawable(Document document,String line)	{
		this.document = document;
		
		if(line.substring(0,1).equals("<"))	{
			String name = line.substring(1);

			Element element = null;

			if(name.equals("svg"))	{
				element = new Element("vector");
				element.addNsAttribute("xmlns","android","http://schemas.android.com/apk/res/android");

				document.rootElement = element;
			}


			if(name.equals("g"))	{
				element = new Element("group");
			}

			if(name.equals("path"))	{
				element = new Element("path");
			}

			if(name.equals("circle"))	{
				element = new Element("circle");
			}

			if(name.equals("rect"))	{
				element = new Element("rect");
			}

			if(name.equals("polyline"))	{
				element = new Element("polyline");
			}

			if(name.equals("line"))	{
				element = new Element("line");
			}

			if(name.equals("ellipse"))	{
				element = new Element("ellipse");
			}

			if(name.equals("stop"))	{
				element = new Element("item");
			}
			
			if(name.equals("defs"))	{
				// element = new Element(name);
			}

			if(name.length() > 2)	{
				if(name.length() > 8 && "Gradient".equals(name.substring(name.length()-8)))	{
					String type = name.substring(0,name.length()-8);

					element = new Element("gradient");
					element.addNsAttribute("android","type",type);

					Element root = document.getElement();
				}
			}

			if(element == null)	{
				document.currentTag = Document.ELEMENT_TAG_UNKNOWN;
			}

			if(name.substring(0,1).equals("/"))	{
				name = name.substring(1,name.length()-1);
				
				if(document.knownTagOpen())	{
					document.closeElement(name);
				}
			}

			if(element != null)	{
				document.setCurrentElement(element);
			}
		}else	{
			if(document.knownTagOpen())	{
				new AttributeFromString(document,line,true);

				int len = line.length();
				if(line.substring(len-1).equals(">"))	{
					if(line.substring(len-2,len-1).equals("/"))	{
						if(document.getElement() != null)	{
							String name = document.getElement().getName();
							document.closeElement(name);
						}
					}
				}
				
			}
		}
	}
}
