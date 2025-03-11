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
