package com.slambyte.util.xml;

import java.io.File;
import java.io.PrintStream;
import java.io.IOException;

import java.util.ArrayList;

public class Document	{
	boolean isAaptHit = false;
	boolean gradientsLinked = false;
	boolean acceptingChildren = false;

	public Element currentElement = null;
	public Element currentGradient = null;

	// Holder for aapt elements for 
	public ArrayList<Element> aapts;

	// Array of gradients to include  
	public ArrayList<Element> gradients;

	public String docStr = "";
	public String rootName = null;
	public String gradientId = null;

	public static final int ELEMENT_TAG_UNKNOWN = 1;
	public static final int ELEMENT_TAG_ROOT = 2;
	public static final int ELEMENT_TAG_GROUP = 3;
	public static final int ELEMENT_TAG_PATH = 4;
	public static final int ELEMENT_TAG_STOP = 5;
	public static final int ELEMENT_TAG_GRADIENT = 6;
	public static final int ELEMENT_TAG_AAPT = 7;
	public static final int ELEMENT_TAG_DEFS = 8;

	public int currentTag = Document.ELEMENT_TAG_UNKNOWN;

	private static Document instance = null;

	private Document()	{
		aapts = new ArrayList<Element>();
		gradients = new ArrayList<Element>();
	}

	public static Document getInstance()	{
		if(instance == null) instance = new Document();
		return instance;
	}

	public Element getElement()	{
		if(currentTag == Document.ELEMENT_TAG_GRADIENT || currentTag == Document.ELEMENT_TAG_STOP)	{
		// System.out.println(currentTag);
			return currentGradient;
		}else {
			return currentElement;
		}
	}

	public String getRootName()	{
		return rootName;
	}

	public void setCurrentElement(Element element)	{
		currentTag = getElementTag(element.getName());

		if(currentTag != Document.ELEMENT_TAG_GRADIENT && currentTag != Document.ELEMENT_TAG_STOP)	{
			// if(currentElement == null) currentElement = element;

			if(currentTag != Document.ELEMENT_TAG_AAPT && currentTag != Document.ELEMENT_TAG_DEFS)	{
				if(currentElement != null)	{
					currentElement.addChild(element);
				}
				currentElement = element;
			}

			if(currentTag == Document.ELEMENT_TAG_ROOT)	{
				rootName = element.getName();
			}
		}else {
			if(currentGradient != null)	{
				// // while(currentGradient.getParent() != null)	{
				// 	// currentGradient = currentGradient.getParent();
				// // }
				currentGradient.addChild(element);
			}
		// printFormatted(currentGradient,0);
			currentGradient = element;
			// printFormatted(element,0);
		}
	}

	public int getElementTag(String name)	{
		int tag = Document.ELEMENT_TAG_UNKNOWN;
		switch(name)	{
			case "svg":
			case "vector":
				tag = Document.ELEMENT_TAG_ROOT;
				break;
			case "g":
			case "group":
				tag = Document.ELEMENT_TAG_GROUP;
				break;
			case "gradient":
			case "linearGradient":
			case "radialGradient":
				tag = Document.ELEMENT_TAG_GRADIENT;
				break;
			case "path":
				tag = Document.ELEMENT_TAG_PATH;
				break;
			case "stop":
			case "item":
				tag = Document.ELEMENT_TAG_STOP;
				break;
			case "defs":
				tag = Document.ELEMENT_TAG_DEFS;
				break;
			case "aapt":
			case "aapt:attr":
				tag = Document.ELEMENT_TAG_AAPT;
				break;
			default:
				tag = Document.ELEMENT_TAG_UNKNOWN;
		}
		return tag;
	}

	

	public boolean knownTagOpen()	{
		boolean isTagKnown = false;
		
		switch(currentTag)	{
			case Document.ELEMENT_TAG_STOP:
			case Document.ELEMENT_TAG_PATH:
			case Document.ELEMENT_TAG_ROOT:
			case Document.ELEMENT_TAG_AAPT:
			case Document.ELEMENT_TAG_DEFS:
			case Document.ELEMENT_TAG_GROUP:
			case Document.ELEMENT_TAG_GRADIENT:
				isTagKnown = true;
				break;
		}
		return isTagKnown;
	}

	public void acceptChildren()	{
		acceptingChildren = true;

		currentTag = Document.ELEMENT_TAG_UNKNOWN;
	}

	public void isAcceptingChildren(boolean bool)	{
		acceptingChildren = bool;
	}

	public boolean isAcceptingChildren()	{
		return acceptingChildren;
	}

	public void closeElement(String name)	{
			currentTag = getElementTag(name);

		if(currentTag == Document.ELEMENT_TAG_PATH)	{
			if("vector".equals(rootName))	{
				// addGradientToGroupChildren(currentElement);
			}
		}

		if(currentTag == Document.ELEMENT_TAG_GROUP)	{
			removeGradientAttributes();
		}

		// // System.out.println(name);

		if(currentTag == Document.ELEMENT_TAG_ROOT)	{
			if(rootName.equals("vector"))	{
				linkGradients();
			}else {
				unlinkGradients();
			}
		}

		if(currentTag != Document.ELEMENT_TAG_GRADIENT && currentTag != Document.ELEMENT_TAG_STOP)	{
			if(currentElement.getParent() != null)	{
				if(currentTag != Document.ELEMENT_TAG_AAPT && currentTag != Document.ELEMENT_TAG_DEFS)	{
					currentElement =	currentElement.getParent();
					currentTag = getElementTag(currentElement.getName());
				}
			}
			// currentTag = Document.ELEMENT_TAG_UNKNOWN;
		}else {
			if(currentGradient.hasParent())	{
				currentGradient = currentGradient.getParent();
			}else {
				if(!isElementDuplicate(gradients,currentGradient))	{
				// printFormatted(currentGradient,0);
					gradients.add(currentGradient);
					// System.out.println(gradients.size());
				}
				currentGradient = null;
			}
		}
	}

	public void addGradientToGroupChildren(Element element)	{
		if(aapts.size() > 0)	{
			if(!"g".equals(element.getName()))	{
				Element parent = element.getParent();

				if(parent.hasAttribute("fillRef") || parent.hasAttribute("strokeRef"))	{
					String fillId = null;
					if(parent.hasAttribute("fillRef")) fillId = parent.getAttribute("fillRef").getValue();

					String strokeId = null;
					if(parent.hasAttribute("strokeRef")) strokeId = parent.getAttribute("strokeRef").getValue();
					
					for(int i = 0; i < aapts.size(); i++)	{
						Element tmp = (Element) aapts.get(i).clone();
						Attribute attr = tmp.getAttribute("id");
						String aaptId = attr != null ? attr.getValue():null;
						// System.out.println(aaptId);
						if(aaptId != null && fillId.equals(aaptId))	{
							tmp.removeAttribute("id");
							element.addChild(tmp);
							// printFormatted(element,0);
							continue;
						}

						if(aaptId != null && strokeId.equals(strokeId))	{
							tmp.removeAttribute("id");
							element.addChild(tmp);
							// printFormatted(tmp,0);
							continue;
						}
						// printFormatted(tmp,0);
					}
				}
				// currentTag = Document.ELEMENT_TAG_GROUP;
			}
		}
	}

	public void removeGradientAttributes()	{
		if(currentElement.hasAttribute("fillRef")) currentElement.removeAttribute("fillRef");
		if(currentElement.hasAttribute("strokeRef")) currentElement.removeAttribute("strokeRef");
	}

	public void linkGradients()	{
		if(gradientsLinked) return;

		if(gradients.size() > 0)	{
			var tmpGrads = new ArrayList<Element>();
// System.out.println(gradients.size());
			for(int i = 0; i < gradients.size();i++)	{
				Element grad = gradients.get(i);
				Attribute attr = grad.getAttribute("childGradient");
				if(attr != null)	{
					for(var tmpGrad : gradients)	{
						var tmpAttr = tmpGrad.getNsAttribute("android","name");
						if(attr.getValue().equals(tmpAttr.getValue()))	{
							grad.addChildren(((Element) tmpGrad.clone()).getChildren());
							
						}
					// printFormatted(tmpGrad,0);
					// printFormatted(grad,0);

					}
					tmpGrads.add(grad);
					grad.removeAttribute("childGradient");
				}
				
			}
			gradients.clear();
			gradients = tmpGrads;
		}
		gradientsLinked = true;
	}

	public void populateAapts()	{
		System.out.println(aapts.size());
		for(var aapt : aapts)	{
			var gradId = aapt.getAttribute("gradientId").getValue();
			for(var grad : gradients)	{
				var attr = grad.getNsAttribute("android","name");
			// System.out.println(gradId + " "+ attr.getValue());
				if(gradId.equals(attr.getValue()))	{
					aapt.addChild(grad);
					aapt.removeAttribute("gradientId");
				}
			}
		}
	}

	public void unlinkGradients()	{
		ArrayList<Element> tmpGrads = new ArrayList<Element>();
		// System.out.println(gradients.size());
		for(Element grad : gradients)	{
			Element tmpGrad = (Element) grad.clone();
			if(tmpGrad.hasChildren())	{
				Element tmpParent = new Element("linearGradient");

				var id = "linearGradient"+ (Math.round(Math.random()*8000)+1000);
				tmpParent.addAttribute("id",id);
				tmpParent.addChildren(tmpGrad.getChildren());

				tmpGrad.getChildren().clear();
				if(!isElementDuplicate(tmpGrads,tmpParent,Element.ATTRIBUTE_TYPE,"id"))	{
					tmpGrad.addNsAttribute("xlink","href","#"+ id);
					tmpGrads.add(tmpParent);
				}else {
					for(var tmp : tmpGrads)	{
						if(tmp.equals(tmpParent,Element.ATTRIBUTE_TYPE,"id"))	{
							tmpGrad.addNsAttribute("xlink","href","#"+ tmp.getAttribute("id").getValue());
							break;
						}
					}
				}
			}
			tmpGrads.add(tmpGrad);
		}
		gradients.clear();
		gradients = tmpGrads;
	}

	private boolean isElementDuplicate(ArrayList<Element> grads,Element elem)	{
		boolean isDuplicate = false;
		for(Element grad : grads)	{
			if(grad.equals(elem))	{
				isDuplicate = true;
				break;
			}
		}
		return isDuplicate;
	}

	private boolean isElementDuplicate(ArrayList<Element> grads,Element elem,int exclude,String string)	{
		boolean isDuplicate = false;
		for(Element grad : grads)	{
			if(grad.equals(elem,exclude,string))	{
				isDuplicate = true;
				break;
			}
		}
		return isDuplicate;
	}

	public void cleanDuplicates(Element element)	{
		if(element.hasChildren())	{
			Element prevChild = null;
			ArrayList<Integer> indexes_to_remove = new ArrayList<Integer>();

			for(Element child : element.getChildren())	{
				var name = child.getName();
				if("g".equals(name) || "group".equals(name))	{
					cleanDuplicates(child);
					continue;
				}

				int index = element.getChildren().indexOf(child);

				if(indexes_to_remove.indexOf(index) > -1)	{
					continue;
				}
			}
		}
	}

	public void saveGradients(Element element)	{
		if(gradients.size() > 0)	{
			for(Element grad : gradients)	{
				String id = grad.getNsAttribute("android","name").getValue();

				Element shape = new Element("shape");
				shape.addInlineNsAttribute("xmlns","android","http://schemas.android.com/apk/res/android");
				shape.addInlineNsAttribute("xmlns","androi","http://schemas.android.com/apk/res/android");
				shape.addChild(grad);
				printFormatted(shape,0);
			}
		}
		if(element.hasChildren())	{
			for(Element child : element.getChildren())	{
				printFormatted(child,0);
			}

		}
	}

	private Element getGradient(Element elem)	{
		Element grad = null;
		for(Element tmpGrad : gradients)	{
			if(tmpGrad.equals(elem))	{
				grad = tmpGrad;
				break;
			}
		}
		return grad;
	}

	public Element sortDuplicateStyles(Element element)	{
		return null;
	}

	public void printFormatted(Element element,int tabs)	{
		if(element == null) return;
		
		String tab = getTabs(tabs);
		System.out.print(tab + "<" + element.getName());

		if(element.getInlineNsAttributes().size() > 0)	{
			String attrs = "";
			for(InlineNsAttribute attr : element.getInlineNsAttributes())	{
				attrs += String.format(" %s:%s=\"%s\"",attr.getNs(),attr.getName(),attr.getValue());
			attrs.trim();
			}
			System.out.print(attrs);
		}

		if(element.getInlineAttributes().size() > 0)	{
			String attrs = "";
			for(InlineAttribute attr : element.getInlineAttributes())	{
				attrs += String.format(" %s=\"%s\"",attr.getName(),attr.getValue());
			attrs.trim();
			}
			System.out.print(attrs);
		}

		if(element.getNsAttributes().size() > 0)	{
			tab = getTabs(tabs+1);
			ArrayList<NsAttribute> attrs = element.getNsAttributes();

			int len = attrs.size();
			for(int i = 0; i < len; i ++)	{
				String ns = attrs.get(i).getNs();
				String name = attrs.get(i).getName();
				String value = attrs.get(i).getValue();
				
				System.out.print(
					String.format("\n%s%s:%s=\"%s\"",tab,ns,name,value)
				);
			}
		}

		if(element.getAttributes().size() > 0)	{
			tab = getTabs(tabs+1);
			ArrayList<Attribute> attrs = element.getAttributes();

			int len = attrs.size();
			for(int i = 0; i < len; i ++)	{
				String name = attrs.get(i).getName();
				String value = attrs.get(i).getValue();
				
				System.out.print(
					String.format("\n%s%s=\"%s\"",tab,name,value)
				);
			}
		}

		if(element.hasChildren())	{
			System.out.print(">\n");

			for(Element child : element.getChildren())	{
				// System.out.println(child.getName());
				printFormatted(child,tabs+1);
			}
			tab = getTabs(tabs);
			System.out.print(
				String.format("%s</%s>\n",tab,element.getName())
			);
		}else	{
			System.out.print("/>\n");
		}
	}

	public void writeToFile(String filename)	{
		try	{
			PrintStream ps = new PrintStream(filename);
			while(currentElement.getParent() != null)	{
				currentElement = currentElement.getParent();
			}

			String fe = filename.substring(filename.length()-3);
			if(fe.equals("xml"))	{
				ps.printf("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			}else	{
				ps.printf("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n");
			}

			writtingToFile(ps,currentElement,0);
			ps.flush();
			ps.close();
		}catch(IOException e) {}
	}

	public void writtingToFile(PrintStream ps,Element element,int tabs)	{
		if(element == null) return;
		
		String tab = getTabs(tabs);
		ps.printf("%s<%s",tab,element.getName());

		if(element.getInlineNsAttributes().size() > 0)	{
			for(InlineNsAttribute attr : element.getInlineNsAttributes())	{
				ps.printf(" %s:%s=\"%s\"",attr.getNs(),attr.getName(),attr.getValue());
			}
		}

		if(element.getInlineAttributes().size() > 0)	{
			for(InlineAttribute attr : element.getInlineAttributes())	{
				ps.printf(" %s=\"%s\"",attr.getName(),attr.getValue());
			}
		}

		if(element.getNsAttributes().size() > 0)	{
			tab = getTabs(tabs+1);
			ArrayList<NsAttribute> attrs = element.getNsAttributes();

			int len = attrs.size();
			for(int i = 0; i < len; i ++)	{
				String ns = attrs.get(i).getNs();
				String name = attrs.get(i).getName();
				String value = attrs.get(i).getValue();
				
				ps.printf("\n%s%s:%s=\"%s\"",tab,ns,name,value);
			}
		}

		if(element.getAttributes().size() > 0)	{
			tab = getTabs(tabs+1);
			ArrayList<Attribute> attrs = element.getAttributes();

			int len = attrs.size();
			for(int i = 0; i < len; i ++)	{
				String name = attrs.get(i).getName();
				String value = attrs.get(i).getValue();
				
				ps.printf("\n%s%s=\"%s\"",tab,name,value);
			}
		}

		if(element.getChildren().size() > 0)	{
			ps.printf(">\n");
			ArrayList<Element> elements = element.getChildren();

			int len = elements.size();
			for(int i = 0; i < len; i ++)	{
				Element child = elements.get(i);
				
				writtingToFile(ps,child,tabs+1);
			}
			tab = getTabs(tabs);
			ps.printf("%s</%s>\n",tab,element.getName());
		}else	{
			ps.printf("/>\n");
		}
	}

	public void toString(Element element,int tabs)	{
		if(element == null) return;
		
		String tab = getTabs(tabs);

		docStr += String.format("%s<%s",tab,element.getName());

		if(element.getNsAttributes().size() > 0)	{
			tab = getTabs(tabs+1);
			ArrayList<NsAttribute> attrs = element.getNsAttributes();

			int len = attrs.size();
			for(int i = 0; i < len; i ++)	{
				String ns = attrs.get(i).getNs();
				String name = attrs.get(i).getName();
				String value = attrs.get(i).getValue();
				
				docStr += String.format("\n%s%s:%s=\"%s\"",tab,ns,name,value);
			}
		}

		if(element.getAttributes().size() > 0)	{
			tab = getTabs(tabs+1);
			ArrayList<Attribute> attrs = element.getAttributes();

			int len = attrs.size();
			for(int i = 0; i < len; i ++)	{
				String name = attrs.get(i).getName();
				String value = attrs.get(i).getValue();
				
				docStr += String.format("\n%s%s=\"%s\"",tab,name,value);
			}
		}

		if(element.getChildren().size() > 0)	{
			docStr += ">\n";
			ArrayList<Element> elements = element.getChildren();

			int len = elements.size();
			for(int i = 0; i < len; i ++)	{
				Element child = elements.get(i);
				System.out.println(child.getName());
				this.toString(child,tabs+1);
			}
			tab = getTabs(tabs);
			docStr += String.format("%s</%s>\n",tab,element.getName());
		}else	{
			docStr += "/>\n";
		}
	}

	public String getTabs(int tabs)	{
		if(tabs > 0)	{
			String tab = "";
			for(int i = 0; i < tabs; i ++)	{
				tab += "\t";
			}
			return tab;
		}
		return "";
	}
}
