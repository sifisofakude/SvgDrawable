package com.slambyte.util.xml;

import java.util.ArrayList;

public class ToDrawable	{
	Document doc;
	public ToDrawable(Document doc,String line)	{
		this.doc = doc;
		
		if(line.substring(0,1).equals("<"))	{
			String name = line.substring(1);

			Element element = null;

			if(name.equals("svg"))	{
				element = new Element("vector");
				element.addNsAttribute("xmlns","android","http://schemas.android.com/apk/res/android");
			}


			if(name.equals("g"))	{
				element = new Element("group");
			}

			if(name.equals("path"))	{
				element = new Element("path");
			}

			if(name.equals("stop"))	{
				element = new Element("item");
			}
			
			if(name.equals("defs"))	{
				element = new Element(name);
			}

			if(element == null)	{
				doc.currentTag = Element.ELEMENT_TAG_UNKNOWN;
			}

			if(name.length() > 2)	{
				if(name.length() > 8 && "Gradient".equals(name.substring(name.length()-8)))	{
					String type = name.substring(0,name.length()-8);

					element = new Element("gradient");
					element.addNsAttribute("android","type",type);

					Element root = doc.getElement();
					// doc.printFormatted(root,0);

							// System.out.println(root.getParent().getName());
					if(root != null)	{
						// root = (Element) root.clone();
						// while(root.getParent() != null)	{
						// 	root = root.getParent();
						// }
						// root.addNsAttribute("xmlns","aapt","http://schemas.android.com/aapt");
						// NsAttribute attr = new NsAttribute("xmlns","aapt","http://schemas.android.com/aapt");

						// ArrayList<NsAttribute> attrs = root.getNsAttributes();
						// attrs.add(1,attr);
					}
				}
			}

			if(name.substring(0,1).equals("/"))	{
				// doc.isAcceptingChildren(false);
				name = name.substring(1,name.length()-1);

				// if(doc.isAcceptingChildren())	{
				// 	doc.isAcceptingChildren(false);
				// 	// doc.closeElement(name);
				// }
				
				if(doc.knownTagOpen())	{
					// System.out.println(doc.getElement().getName());

					doc.closeElement(name);
				}
			}

			if(element != null)	{
				doc.setCurrentElement(element);
			}
		}else	{
			if(doc.knownTagOpen())	{
				new AttributeFromString(doc,line,true);

				int len = line.length();
				if(line.substring(len-1).equals(">"))	{
					if(line.substring(len-2,len-1).equals("/"))	{
					// doc.removeGradientAttributes();
						if(doc.getElement() != null)	{
							String name = doc.getElement().getName();
							doc.closeElement(name);
						}
					}else	{
						// doc.acceptChildren();
					}
				}
				
			}
		}
	}
}
