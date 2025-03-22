package com.slambyte.util.xml;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class Element implements Cloneable	{
	private String name;
	private Element parent = null;
	private ArrayList<Element> children;
	private ArrayList<Attribute> attributes;
	private ArrayList<NsAttribute> nsAttributes;
	private ArrayList<InlineAttribute> inlineAttributes;
	private ArrayList<InlineNsAttribute> inlineNsAttributes;

	public static final int ATTRIBUTE_TYPE = 1;
	public static final int NS_ATTRIBUTE_TYPE = 2;
	public static final int INLINE_ATTRIBUTE_TYPE = 3;
	public static final int INLINE_NS_ATTRIBUTE_TYPE = 4;

	public Element(String name)	{
		if(name == null || name.isEmpty()) return;
		
		this.name = name;
		children = new ArrayList<Element>();
		attributes = new ArrayList<Attribute>();
		nsAttributes = new ArrayList<NsAttribute>();
		inlineAttributes = new ArrayList<InlineAttribute>();
		inlineNsAttributes = new ArrayList<InlineNsAttribute>();
	}

	@Override
	public Object clone()	{
		try  {
			Element tmpElement = (Element) super.clone();

			ArrayList<Attribute> tmpAttributes = new ArrayList<Attribute>();
			for(Attribute attr : attributes)	{
				tmpAttributes.add((Attribute) attr.clone());
			// System.out.println(attr.getName());
			}
			tmpElement.setAttributes(tmpAttributes);
			tmpAttributes = null;

			ArrayList<NsAttribute> tmpNsAttributes = new ArrayList<NsAttribute>();
			for(NsAttribute attr : nsAttributes)	{
				tmpNsAttributes.add((NsAttribute) attr.clone());
			// System.out.println(attr.getName());
			}
			tmpElement.setNsAttributes(tmpNsAttributes);
			tmpNsAttributes = null;

			ArrayList<InlineAttribute> tmpInlineAttributes = new ArrayList<InlineAttribute>();
			for(InlineAttribute attr : inlineAttributes)	{
				tmpInlineAttributes.add((InlineAttribute) attr.clone());
			// System.out.println(attr.getName());
			}
			tmpElement.setInlineAttributes(tmpInlineAttributes);
			tmpInlineAttributes = null;

			ArrayList<InlineNsAttribute> tmpInlineNsAttributes = new ArrayList<InlineNsAttribute>();
			for(InlineNsAttribute attr : inlineNsAttributes)	{
				tmpInlineNsAttributes.add((InlineNsAttribute) attr.clone());
			// System.out.println(attr.getName());
			}
			tmpElement.setInlineNsAttributes(tmpInlineNsAttributes);
			tmpInlineNsAttributes = null;

			return tmpElement;
		}catch(CloneNotSupportedException e)	{
			return null;
		}
	}

	public void addChild(Element child)	{
		if(child != null)	{
			child.setParent(this);
			children.add(child);
		}
	}

	public void addChildren(ArrayList<Element> tmpChildren)	{
		if(tmpChildren != null)	{
			for(Element child : tmpChildren)	{
				child.setParent(this);
				this.children.add(child);
			}
		}
	}

	public void setChildren(ArrayList<Element> children)	{
		this.children = children;
	}

	public void setAttributes(ArrayList<Attribute> attributes)	{
		this.attributes = attributes;
	}

	public void setNsAttributes(ArrayList<NsAttribute> nsAttributes)	{
		this.nsAttributes = nsAttributes;
	}

	public void setInlineAttributes(ArrayList<InlineAttribute> inlineAttributes)	{
		this.inlineAttributes = inlineAttributes;
	}

	public void setInlineNsAttributes(ArrayList<InlineNsAttribute> inlineAttributes)	{
		this.inlineNsAttributes = inlineNsAttributes;
	}

	public void addAttribute(String name,String value)	{
		if(name == null || value == null) return;
		if(name.isEmpty() || value.isEmpty()) return;

		Attribute attr = getAttribute(name);

		if(name.equals("viewBox"))	{
			// System.out.println(attr == null);
		}
		if(attr == null)	{
			attr = new Attribute(name,value);
			attributes.add(attr);
		}else	{
			attr.setValue(value);
		}
	}

	public void appendAttribute(String name,String value)	{
		if(name == null || value == null) return;
		if(name.isEmpty() || value.isEmpty()) return;

		Attribute attr = getAttribute(name);
		if(attr == null)	{
			attr = new Attribute(name,value);
			attributes.add(attr);
		}else	{
			attr.setValue(attr.getValue()+""+value);
		}
	}

	public void addNsAttribute(String ns,String name,String value)	{
		if(ns == null ||name == null || value == null) return;
		if(ns.isEmpty() || name.isEmpty() || value.isEmpty()) return;

		NsAttribute attr = getNsAttribute(ns,name);
		if(attr == null)	{
			attr = new NsAttribute(ns,name,value);
			nsAttributes.add(attr);
		}else	{
			attr.setValue(value);
		}
	}

	public void addInlineAttribute(String name,String value)	{
		if(name == null || value == null) return;
		if(name.isEmpty() || value.isEmpty()) return;

		InlineAttribute attr = getInlineAttribute(name);
		if(attr != null)	{
			attr.setValue(value);
		}else {
			attr = new InlineAttribute(name,value);
			inlineAttributes.add(attr);
		}
	}

	public void addInlineNsAttribute(String ns,String name,String value)	{
		if(ns == null || name == null || value == null) return;
		if(ns.isEmpty() || name.isEmpty() || value.isEmpty()) return;

		InlineNsAttribute attr = getInlineNsAttribute(ns,name);
		if(attr != null)	{
			attr.setValue(value);
		}else {
			attr = new InlineNsAttribute(ns,name,value);
			inlineNsAttributes.add(attr);
		}
	}

	public boolean equals(Element element)	{
		boolean result = super.equals(element);

		boolean nameBool = false;
		if(name.equals(element.getName()))	{
			nameBool = true;
		}


		boolean inlineAttr = false;
		if(inlineAttributes.size() == element.getInlineAttributes().size()) inlineAttr = true;
		for(InlineAttribute attr : inlineAttributes)	{
			// if("id".equals(attr.getName())) continue;
			if(!inlineAttr) break;

			InlineAttribute inAttr = element.getInlineAttribute(attr.getName());
			if(inAttr != null)	{
				if(attr.getValue().equals(inAttr.getValue()))	{
					inlineAttr = true;
				}else {
					inlineAttr = false;
				}
			}else {
				inlineAttr = false;
			}
			if(!inlineAttr) break;
		}
		// System.out.println(name);
		// System.out.println(inlineAttr);

		boolean inlineNsAttr = false;
		if(inlineNsAttributes.size() == element.getInlineNsAttributes().size()) inlineNsAttr = true;

		for(InlineNsAttribute attr : inlineNsAttributes)	{
			// if("name".equals(attr.getName())) continue;
			if(!inlineNsAttr) break;
			InlineNsAttribute inAttr = element.getInlineNsAttribute(attr.getNs(),attr.getName());
			if(inAttr != null)	{
				if(attr.getValue().equals(inAttr.getValue()))	{
					inlineNsAttr = true;
				}else {
					inlineNsAttr = false;
				}
			}else {
				inlineNsAttr = false;
			}

			if(!inlineNsAttr) break;
		}

		boolean attrsBool = false;
		if(attributes.size() == element.getAttributes().size()) attrsBool = true;

		for(int i = 0; i < attributes.size(); i ++)	{
			// if("name".equals(attr.getName())) continue;
			if(!attrsBool) break;

			var attr = attributes.get(i);
			var inAttr = element.getAttributes().get(i);

			attrsBool = attr.getName().equals(inAttr.getName());
			attrsBool = attr.getValue().equals(inAttr.getValue());

			// System.out.println(attr.getName() +" "+ inAttr.getName());
			// System.out.println(attr.getValue() +" "+ inAttr.getValue());

			if(!attrsBool) break;
		}

		boolean nsAttrsBool = false;
		if(nsAttributes.size() == element.getNsAttributes().size()) nsAttrsBool = true;

		for(int i = 0; i < nsAttributes.size(); i ++)	{
			// if("name".equals(attr.getName())) continue;
			if(!nsAttrsBool) break;

			var attr = nsAttributes.get(i);
			var inAttr = element.getNsAttributes().get(i);

			nsAttrsBool = attr.getName().equals(inAttr.getName());
			nsAttrsBool = attr.getValue().equals(inAttr.getValue());

			if(!nsAttrsBool) break;
		}

		boolean childBool = false;
		if(element.getChildren().size() == children.size()) childBool = true;

		if(childBool)	{
			for(int i = 0; i < children.size(); i ++)	{
				Element child = children.get(i);
				Element inChild = element.getChildren().get(i);

				childBool = child.equals(inChild);
			// System.out.println(child.getName() + " " + inChild.getName() +" "+ child.equals(inChild));

				if(!childBool) break;
			}

			if(attrsBool && nsAttrsBool && inlineAttr && inlineNsAttr && nameBool && childBool)	{
				result = true;
			}
		}
		// System.out.println(nsAttrsBool +" "+ attrsBool +" "+ childBool +" "+ inlineAttr);
		return result;
	}

	public boolean equals(Element element,HashMap<Integer,List<String>> exclude)	{
		boolean result = super.equals(element);

		boolean nameBool = false;
		if(name.equals(element.getName()))	{
			nameBool = true;
		}


		boolean inlineAttr = false;
		if(inlineAttributes.size() == element.getInlineAttributes().size()) inlineAttr = true;
		for(InlineAttribute attr : inlineAttributes)	{
			List<String> strings = (List<String>) exclude.get(Element.INLINE_ATTRIBUTE_TYPE);
			if(strings != null)	{
				if(strings.contains(attr.getName())) continue;
			}

			InlineAttribute inAttr = element.getInlineAttribute(attr.getName());
			if(inAttr != null)	{
				if(attr.getValue().equals(inAttr.getValue()))	{
					inlineAttr = true;
				}else {
					inlineAttr = false;
				}
			}else {
				inlineAttr = false;
			}
			if(!inlineAttr) break;
		}
		// System.out.println(name);
		// System.out.println(inlineAttr);

		boolean inlineNsAttr = false;
		if(inlineNsAttributes.size() == element.getInlineNsAttributes().size()) inlineNsAttr = true;

		for(InlineNsAttribute attr : inlineNsAttributes)	{
			List<String> strings = (List<String>) exclude.get(Element.INLINE_NS_ATTRIBUTE_TYPE);
			if(strings != null)	{
				if(strings.contains(attr.getName())) continue;
			}

			InlineNsAttribute inAttr = element.getInlineNsAttribute(attr.getNs(),attr.getName());
			if(inAttr != null)	{
				if(attr.getValue().equals(inAttr.getValue()))	{
					inlineNsAttr = true;
				}else {
					inlineNsAttr = false;
				}
			}else {
				inlineNsAttr = false;
			}

			if(!inlineNsAttr) break;
		}

			// System.out.println(string);
			// System.out.println(exclude);
		boolean attrsBool = false;
		if(attributes.size() == element.getAttributes().size()) attrsBool = true;

		for(int i = 0; i < attributes.size(); i ++)	{
			// if("name".equals(attr.getName())) continue;
			if(!attrsBool) break;

			var attr = attributes.get(i);
			var inAttr = element.getAttributes().get(i);

			List<String> strings = (List<String>) exclude.get(Element.ATTRIBUTE_TYPE);
			if(strings != null)	{
				if(strings.contains(attr.getName())) continue;
			}

			attrsBool = attr.getName().equals(inAttr.getName());
			attrsBool = attr.getValue().equals(inAttr.getValue());

			// System.out.println(attr.getName() +" "+ inAttr.getName());
			// System.out.println(attr.getValue() +" "+ inAttr.getValue());

			if(!attrsBool) break;
		}

		boolean nsAttrsBool = false;
		if(nsAttributes.size() == element.getNsAttributes().size()) nsAttrsBool = true;
		for(int i = 0; i < nsAttributes.size(); i ++)	{
			// if("name".equals(attr.getName())) continue;
			if(!nsAttrsBool) break;

			var attr = nsAttributes.get(i);
			var inAttr = element.getNsAttributes().get(i);

			List<String> strings = (List<String>) exclude.get(Element.NS_ATTRIBUTE_TYPE);
			if(strings != null)	{
// System.out.println(attr.getName()+ " "+ strings);
				if(strings.contains(attr.getName())) continue;
			}

			nsAttrsBool = attr.getName().equals(inAttr.getName());
			nsAttrsBool = attr.getValue().equals(inAttr.getValue());

			if(!nsAttrsBool) break;
		}

		boolean childBool = false;
		if(element.getChildren().size() == children.size()) childBool = true;

		if(childBool)	{
			for(int i = 0; i < children.size(); i ++)	{
				Element child = children.get(i);
				Element inChild = element.getChildren().get(i);

				childBool = child.equals(inChild);
			// System.out.println(child.getName() + " " + inChild.getName() +" "+ child.equals(inChild));

				if(!childBool) break;
			}

			if(attrsBool && nsAttrsBool && inlineAttr && inlineNsAttr && nameBool && childBool)	{
				result = true;
			}
		}
		// System.out.println(nsAttrsBool +" "+ attrsBool +" "+ childBool +" "+ inlineAttr);
		return result;
	}

	public String getName()	{
		return name;
	}

	public Attribute getAttribute(String name)	{
		int len = attributes.size();
		Attribute attr = null;
		for(int i = 0; i < len; i ++)	{
			Attribute tmpAttr = attributes.get(i);
			if(name.equals(tmpAttr.getName()))	{
				attr = tmpAttr;
				break;
			}
		}
		return attr;
	}

	public NsAttribute getNsAttribute(String ns,String name)	{
		int len = nsAttributes.size();

		NsAttribute attr = null;
		for(int i = 0; i < len; i ++)	{
			NsAttribute tmpAttr = nsAttributes.get(i);
			if(name.equals(tmpAttr.getName()) && ns.equals(tmpAttr.getNs()))	{
				attr = tmpAttr;
				break;
			}
		}
		return attr;
	}

	public InlineAttribute getInlineAttribute(String name)	{
		int len = inlineAttributes.size();

		InlineAttribute attr = null;
		for(int i = 0; i < len; i ++)	{
			InlineAttribute tmpAttr = inlineAttributes.get(i);
			if(name.equals(tmpAttr.getName()))	{
				attr = tmpAttr;
				break;
			}
		}
		return attr;
	}

	public InlineNsAttribute getInlineNsAttribute(String ns,String name)	{
		int len = inlineNsAttributes.size();

		InlineNsAttribute attr = null;
		for(int i = 0; i < len; i ++)	{
			InlineNsAttribute tmpAttr = inlineNsAttributes.get(i);
			if(name.equals(tmpAttr.getName()) && ns.equals(tmpAttr.getNs()))	{
				attr = tmpAttr;
				break;
			}
		}
		return attr;
	}

	public boolean hasParent()	{
		if(parent != null)	{
			return true;
		}
		return false;
	}

	public boolean hasChildren()	{
		if(children.size() > 0) return true;
		return false;
	}

	public boolean hasAttribute(String name)	{
		Attribute attr = getAttribute(name);
		if(attr != null)	{
			return true;
		}
		return false;
	}

	public boolean hasAttributes()	{
		if(attributes.size() > 0)	{
			return true;
		}
		return false;
	}

	public boolean hasNsAttribute(String ns,String name)	{
		NsAttribute attr = getNsAttribute(ns,name);
		if(attr != null)	{
			return true;
		}
		return false;
	}

	public boolean hasNsAttributes()	{
		if(nsAttributes.size() > 0)	{
			return true;
		}
		return false;
	}

	public boolean hasInlineAttribute(String name)	{
		InlineAttribute attr = getInlineAttribute(name);
		if(attr != null)	{
			return true;
		}
		return false;
	}

	public boolean hasInlineAttributes()	{
		if(inlineNsAttributes.size() > 0)	{
			return true;
		}
		return false;
	}

	public boolean hasInlineNsAttribute(String ns,String name)	{
		InlineNsAttribute attr = getInlineNsAttribute(ns,name);
		if(attr != null)	{
			return true;
		}
		return false;
	}

	public boolean hasInlineNsAttributes()	{
		if(inlineNsAttributes.size() > 0)	{
			return true;
		}
		return false;
	}

	public boolean removeAttribute(String name)	{
		if(getAttributes().size() > 0)	{
			int len = getAttributes().size();
			for(int i = 0; i < len; i ++)	{
				Attribute attr = getAttributes().get(i);
				if(name.equals(attr.getName()))	{
					getAttributes().remove(i);
					return true;
				}
			}
		}
		return false;
	}

	public boolean removeNsAttribute(String ns,String name)	{
		if(getAttributes().size() > 0)	{
			int len = getNsAttributes().size();
			for(int i = 0; i < len; i ++)	{
				NsAttribute attr = getNsAttributes().get(i);
				if(name.equals(attr.getName()) && ns.equals(attr.getNs()))	{
					getNsAttributes().remove(i);
					return true;
				}
			}
		}
		return false;
	}

	public boolean removeChild(String name,String value)	{
		if(getChildren().size() > 0)	{
			int len = getChildren().size();
			for(int i = 0; i < len; i ++)	{
				Element child = getChildren().get(i);
				Attribute attr = getAttribute(name);
				if(attr != null)	{
					if(value.equals(attr.getValue()))	{
						getChildren().remove(i);
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean removeChildNs(String ns,String name,String value)	{
		if(getChildren().size() > 0)	{
			int len = getChildren().size();
			for(int i = 0; i < len; i ++)	{
				Element child = getChildren().get(i);
				NsAttribute attr = getNsAttribute(ns,name);
				if(attr != null)	{
					if(value.equals(attr.getValue()))	{
						getChildren().remove(i);
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean moveToPosition(int src,int dest)	{
		boolean moved = false;
		int len = getChildren().size();
		
		if(src > -1 && src < len && dest > -1 && dest < len && src != dest)	{
			Element childToMove = getChildren().get(src);
			ArrayList<Element> tmpChildren = new ArrayList<Element>();
			
			for(int i = 0; i < len; i ++)	{
				Element child = getChildren().get(i);
				
				if(dest == i)	{
					tmpChildren.add(childToMove);
					moved = true;
					continue;
				}

				if(src == i) continue;
				
				tmpChildren.add(child);
			}
			children = tmpChildren;
		}
		return moved;
	}

	public void setName(String name)	{
		if(name != null && name.isEmpty()) return;

		this.name = name;
	}

	public void setParent(Element parent)	{
		this.parent = parent;
	}

	public Element getParent()	{
		return parent;
	}

	public ArrayList<Element> getChildren()	{
		return children;
	}

	public ArrayList<Attribute> getAttributes()	{
		return attributes;
	}

	public ArrayList<NsAttribute> getNsAttributes()	{
		return nsAttributes;
	}

	public ArrayList<InlineAttribute> getInlineAttributes()	{
		return inlineAttributes;
	}

	public ArrayList<InlineNsAttribute> getInlineNsAttributes()	{
		return inlineNsAttributes;
	}
}
