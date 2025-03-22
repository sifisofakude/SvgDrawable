package com.slambyte.util.xml;

public class ToSvg	{
	Document doc;
	public ToSvg(Document doc,String line)	{
		this.doc = doc;
		
		if(line.substring(0,1).equals("<"))	{
			String name = line.substring(1);

			Element element = null;

			if(name.equals("vector"))	{
				element = new Element("svg");
				element.addAttribute("xmlns","http://www.w3.org/2000/svg");
			}

			if(name.equals("path"))	{
				element = new Element("path");
			}

			if(name.equals("gradient"))	{
				element = new Element("gradient");
				element.addAttribute("gradientUnits","userSpaceOnUse");
				// doc.isAcceptingChildren(false);
			}

			if(name.equals("aapt:attr"))	{
				// doc.isAcceptingChildren(false);
				element = new Element("aapt");
			}

			if(name.equals("group"))	{
				element = new Element("g");
				// doc.printFormatted(element,0);
			}

			if(name.equals("item"))	{
				element = new Element("stop");
			}

			// System.out.println(name);

			if(name.substring(0,1).equals("/"))	{
				name = name.substring(1,name.length()-1);

				if(name.equals("path"))	{
					checkFillColor(doc.getElement());
				}

				if(element == null)	{
					doc.currentTag = Element.ELEMENT_TAG_UNKNOWN;
				}
					
				if(doc.knownTagOpen())	{
					// if(name.equals("aapt:attr"))	{
						// doc.closeAaptElement();
					// }else {
					doc.closeElement(name);
					doc.isAcceptingChildren(false);

					element = null;
					// }
				}
			}

			if(element != null)	{
				doc.setCurrentElement(element);
			}
		}else	{
			if(doc.knownTagOpen())	{
				new AttributeFromString(doc,line,true);

				int len = line.length();
				if(doc.currentTag != Document.ELEMENT_TAG_AAPT)	{
					if(line.substring(len-1).equals(">"))	{
						if(line.substring(len-2,len-1).equals("/"))	{
							String name = doc.getElement().getName();
							if(name.equals("path"))	{
								checkFillColor(doc.getElement());
							}
							doc.closeElement(name);
						}else	{
							doc.acceptChildren();
						}
					}
				}
			}
		}
	}

	public void checkFillColor(Element element)	{
		Attribute attr = element.getAttribute("style");
		if(attr !=  null)	{
			boolean isFound = false;
			String[] styles = attr.getValue().split(";");
			for(String style : styles)	{
				if(style.startsWith("fill:"))	{
					isFound = true;
					break;
				}
			}

			if(!isFound)	{
				element.appendAttribute("style",";fill:none");
			}
		}
	}
}
