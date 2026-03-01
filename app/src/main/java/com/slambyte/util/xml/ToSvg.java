package com.slambyte.util.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.util.ArrayList;

public class ToSvg	{
	Document document;
	Element defs = null;
	ArrayList gradients = new ArrayList<Element>();

	String linkValue = null;

	public ToSvg(Document document,String input)	{
		this.document = document;

		defs = new Element("defs");
		
		try	{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			org.w3c.dom.Document w3cDoc = builder.parse(new File(input));

			Node rootNode = w3cDoc.getDocumentElement();
			rootNode.normalize();

			if("vector".equals(rootNode.getNodeName()))	{
				Element element = new Element("svg");
				element.addAttribute("xmlns","http://www.w3.org/2000/svg");

				setAttributes(element,rootNode.getAttributes());
				// if(element.hasAttributes())	{
				// }

				document.rootElement = element;

				element.addChild(defs);

				NodeList children = rootNode.getChildNodes();

				element.addChildren(parseChildren(children));

				String childGradId = null;
				for(Object grad : gradients)	{
					if(((Element) grad).hasChildren())	{
						childGradId = "linearGradient"+ (Math.round(Math.random()*9999)+1000);

						Element childGrad = new Element("linearGradient");
						childGrad.addChildren(((Element) grad).getChildren());
						childGrad.addAttribute("id",childGradId);

						((Element) grad).removeChildren();

						defs.addChild(childGrad);
					}

					if(childGradId != null) ((Element) grad).addAttribute("xlink:href","#"+ childGradId);

					defs.addChild((Element) grad);
				}
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}

	public ArrayList<Element> parseChildren(NodeList childNodes)	{
		ArrayList result = new ArrayList<Element>();

		if(childNodes == null) return result;

		for(int i = 0; i < childNodes.getLength(); i ++)	{
			Node child = childNodes.item(i);

			Element element = null;
			ArrayList grandChildren = null;

			switch(child.getNodeName())	{
			case "group":
				element = new Element("g");
				break;

			case "path":
				element = new Element("path");
				break;

			case "aapt:attr":
				element = new Element("aapt");
				document.rootElement.addAttribute("xmlns:xlink","http://www.w3.org/1999/xlink");
				break;

			case "item":
				element = new Element("stop");
				break;

			case "gradient":
				element = new Element("gradient");
				element.addAttribute("gradientUnits","userSpaceOnUse");
				break;
			}

			if(element != null)	{
				setAttributes(element,child.getAttributes());

				if("aapt".equals(element.getName()))	{
					gradients.addAll(parseChildren(child.getChildNodes()));
					System.out.println(gradients.size());
					// document.printFormatted((Element) result.get(result.size()-1),0);
					continue;
				}

				boolean isLinkElement = linkValue != null && !element.getName().contains("Gradient") 
					&& !"stop".equals(element.getName()) && !"aapt".equals(element.getName());

				if(isLinkElement)	{
					Element tmpElement = (Element) result.get(result.size() - 1);
					styleElement(tmpElement,linkValue);
					linkValue = null;
				}


				if(element.getName().contains("Gradient") && linkValue != null)	{
					String gradientId = null;
					if(!element.hasAttribute("id"))	{
						gradientId = element.getName() +""+ (Math.round(Math.random()*9999)+1000);
						element.addAttribute("id",gradientId);
					}else {
						gradientId = element.getAttribute("id").getValue();
					}
					linkValue = linkValue +": url(#"+ gradientId +")";
				}

				Attribute styleAttr = element.getAttribute("style");
				if(styleAttr != null)	{
					String style = styleAttr.getValue();
					if(!style.contains("fill:") && !"stop".equals(element.getName()))	{
						styleAttr.setValue(style +";fill: none");
					}
				}

				result.add(element);
				
				element.addChildren(parseChildren(child.getChildNodes()));
			}
		}
		return result;
	}

	public void setAttributes(Element element, NamedNodeMap attributes)	{
		if(element == null || attributes == null) return;

		String transformation = null;

		for(int i = 0; i < attributes.getLength(); i ++)	{
			Node attr = attributes.item(i);

			String name = attr.getNodeName().replace("android:","");
			String value = attr.getNodeValue();

			if(name.equals("startX")) element.addAttribute("x1",value);
			if(name.equals("startY")) element.addAttribute("y1",value);
			if(name.equals("endX")) element.addAttribute("x2",value);
			if(name.equals("endY")) element.addAttribute("y2",value);
			if(name.equals("offset")) element.addAttribute("offset",value);
			if(name.equals("pathData")) element.addAttribute("d",value);
			if(name.equals("color")) styleElement(element,name+":"+value);

			if(name.equals("name"))	{
				if(!"aapt".equals(element.getName()))	{
					element.addAttribute("id",value);
				}else {
					linkValue = value.replace("android:","");
				}
			}

			if(name.equals("width") || name.equals("height"))	{
				value = value.replaceAll("[A-Za-z]","");

				element.addAttribute(name,value);
			}

			if(name.equals("type") && "gradient".equals(element.getName()))	{
				String gradName = value + "Gradient";
				element.setName(gradName);
			}

			if(name.equals("viewportWidth"))	{
				Attribute viewBox = element.getAttribute("viewBox");
				if(viewBox == null)	{
					value = "0 0 "+ value;
				}else {
					value = viewBox.getValue().replace("width",value);
				}
				element.addAttribute("viewBox",value);
			}

			if(name.equals("viewportHeight"))	{
				Attribute viewBox = element.getAttribute("viewBox");
				if(viewBox == null)	{
					value = "0 0 width "+ value;
				}else {
					value = viewBox.getValue() +" "+ value;
				}
				element.addAttribute("viewBox",value);
			}

			if(name.equals("fillColor") || name.equals("strokeColor") || name.equals("fillType"))	{
				styleElement(element,name +":"+ value);
			}

			if(name.equals("strokeAlpha") || name.equals("fillAlpha"))	{
				styleElement(element,name +":"+ value);
			}

			if(name.equals("strokeWidth") || name.equals("strokeMiterLimit") || name.equals("strokeLineCap"))	{
				styleElement(element,name +":"+ value);
			}

			if(name.equals("strokeLineJoin"))	{
				styleElement(element,name +":"+ value);
			}

			if(name.equals("translateX"))	{
				if(transformation == null)	{
					transformation = "translate("+ value +",y)";
				}else 	{
					transformElement(element, transformation.replace("x",value));
					transformation = null;
				}
			}

			if(name.equals("translateY"))	{
				if(transformation == null)	{
					transformation = "translate(x,"+ value +")";
				}else 	{
					transformElement(element, transformation.replace("y",value));
					transformation = null;
				}
			}

			if(name.equals("scaleY"))	{
				if(transformation == null)	{
					transformation = "scale(x,"+ value +")";
				}else 	{
					transformElement(element, transformation.replace("y",value));
					transformation = null;
				}
			}

			if(name.equals("scaleX"))	{
				if(transformation == null)	{
					transformation = "scale("+ value +",y)";
				}else 	{
					transformElement(element, transformation.replace("x",value));
					transformation = null;
				}
			}

			if(name.equals("pivotX"))	{
				if(transformation == null)	{
					transformation = "rotate(r,"+ value +",py)";
				}else 	{
					transformation = transformation.replace("px",value);
				}
			}

			if(name.equals("pivotY"))	{
				if(transformation == null)	{
					transformation = "rotate(r,px,"+ value +")";
				}else 	{
					transformation = transformation.replace("py",value);
				}
			}

			if(name.equals("rotate"))	{
				if(transformation == null)	{
					transformation = "rotate("+ value +")";
					transformElement(element,transformation);
					transformation = null;
				}else 	{
					transformElement(element, transformation.replace("(r","("+value));
					transformation = null;
				}
			}
			System.out.println(transformation);
		}
	}

	public void styleElement(Element element,String style)	{
		if(element == null || style == null) return;

		String[] styles = style.split(":");
		String name = styles[0];
		String value = styles[1];

		// reset variable for reuse
		style = null;

		if(name.equals("strokeWidth")) style = "stroke-width: "+ value;
		if(name.equals("strokeLineCap")) style = "stroke-linecap: "+ value;
		if(name.equals("strokeLineJoin")) style = "stroke-linejoin: "+ value;
		if(name.equals("strokeMiterLimit")) style = "stroke-miterlimit: "+ value;
		if(name.equals("strokeAlpha")) style = "stroke-opacity: "+ value;

		if(name.equals("fillAlpha")) style = "fill-opacity: "+ value;
		if(name.equals("fillType")) style = "fill-rule: "+ value.toLowerCase();

		if(name.equals("fillColor"))	{
			if(value.startsWith("url"))	{
				style = "fill: "+ value;
			}else {
				String opacity = document.opacityFromRgbaHex(value);
				if(opacity == null) style = "fill: "+ value;
				else style = "fill: #"+ value.substring(3) +";fill-opacity: "+ opacity;
			}
		}

		if(name.equals("strokeColor"))	{
			if(value.startsWith("url"))	{
				style = " stroke: "+ value;
			}else {
				String opacity = document.opacityFromRgbaHex(value);
				if(opacity == null) style = "stroke: "+ value;
				else style = "stroke: #"+ value.substring(3) +";stroke-opacity: "+ opacity;
			}
		}

		if(name.equals("color"))	{
			String opacity = document.opacityFromRgbaHex(value);
			if(opacity == null) style = "stop-color: "+ value;
			else style = " stop-color: #"+ value.substring(3) +";stop-opacity: "+ opacity;
		}

		if(style != null)	{
			Attribute styleAttr = element.getAttribute("style");
			if(styleAttr == null)	{
				element.addAttribute("style",style.trim());
			}else {
				String styleValue = styleAttr.getValue();
				if(name.equals("fillColor") || name.equals("strokeColor"))	{
					String regex = "\\b"+ name.replace("Color","") +": [^;]*;?";
					styleValue = styleValue.replaceAll(regex,"").replaceAll(";$","");
				}
				styleAttr.setValue(styleValue +";"+ style);
			}
		}
	}

	public void transformElement(Element element, String transformation)	{
		if(element == null || transformation == null || transformation.isEmpty()) return;

		Attribute transform = element.getAttribute("transform");
		if(transform == null)	{
			element.addAttribute("transform",transformation);
		}else {
			transform.setValue(transform.getValue() +" "+ transformation);
		}
	}
}
