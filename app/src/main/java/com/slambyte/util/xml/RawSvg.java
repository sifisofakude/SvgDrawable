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

public class RawSvg	{
	Document doc;
	public RawSvg(Document doc,String line)	{
		this.doc = doc;
		
		if(line.substring(0,1).equals("<"))	{
			String name = line.substring(1);

			Element element = null;

			if(name.equals("svg"))	{
				element = new Element("svg");
				element.addAttribute("xmlns","http://www.w3.org/2000/svg");
			}

			if(name.equals("path"))	{
				element = new Element("path");
			}

			if(name.equals("g"))	{
				element = new Element("g");
				// doc.printFormatted(element,0);
			}

			if(name.substring(0,1).equals("/"))	{
					// System.out.println(line);
				// doc.isAcceptingChildren(false);
				name = name.substring(1,name.length()-1);

				if(doc.isAcceptingChildren())	{
					doc.isAcceptingChildren(false);
					doc.closeElement(name);
				}
				
				if(doc.knownTagOpen())	{
					doc.closeElement(name);
				}
			}

			if(element != null)	{
				doc.setCurrentElement(element);
			}
		}else	{
			if(doc.knownTagOpen())	{
				new AttributeFromString(doc,line,false);

				int len = line.length();
				if(line.substring(len-1).equals(">"))	{
					if(line.substring(len-2,len-1).equals("/"))	{
						String name = doc.getElement().getName();
						doc.closeElement(name);
					}else	{
						doc.acceptChildren();
					}
				}
				
			}
		}
	}
}
