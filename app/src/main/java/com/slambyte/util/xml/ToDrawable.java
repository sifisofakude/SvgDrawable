package com.slambyte.util.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.util.ArrayList;

public class ToDrawable	{
	Document document;
	ArrayList gradients = new ArrayList<Element>();

	public ToDrawable(Document document,String input)	{
		this.document = document;
		
		try	{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			org.w3c.dom.Document w3cDoc = builder.parse(new File(input));

			Node rootNode = w3cDoc.getDocumentElement();
			rootNode.normalize();

			if("svg".equals(rootNode.getNodeName()))	{
				Element element = new Element("vector");
				element.addNsAttribute("xmlns","android","http://schemas.android.com/apk/res/android");

				setAttributes(element,rootNode.getAttributes());
				// if(element.hasAttributes())	{
				// }

				document.rootElement = element;

				NodeList children = rootNode.getChildNodes();

				element.addChildren(parseChildren(children));
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
			case "g":
				element = new Element("group");
				break;

			case "path":
				element = new Element("path");
				break;

			case "circle":
				element = new Element("circle");
				break;

			case "rect":
				element = new Element("rect");
				break;

			case "ellipse":
				element = new Element("ellipse");
				break;

			case "polyline":
				element = new Element("polyline");
				break;

			case "polyline":
				element = new Element("line");
				break;

			case "stop":
				element = new Element("item");
				break;

			case "linearGradient":
			case "radialGradient":
				String name = child.getNodeName();

				int typeEnd = name.indexOf("Gradient");
				String type = name.substring(0,typeEnd);

				element = new Element("gradient");
				element.addNsAttribute("android","type",type);
				break;
			}

			if(element != null)	{
				setAttributes(element,child.getAttributes());
				
				element.addChildren(parseChildren(child.getChildNodes()));

				boolean isShape = "circle".equals(element.getName()) || "rect".equals(element.getName()) ||
					"ellipse".equals(element.getName());

				if(isShape) shapeToPath(element);

				if(!"gradient".equals(element.getName()))	{
					result.add(element);
				}else {
					gradients.add(element);
				}
			}else if("defs".equals(child.getNodeName())) {
				gradients.addAll(parseChildren(child.getChildNodes()));
			}
		}
		return result;
	}

	public void setAttributes(Element element, NamedNodeMap attributes)	{
		if(element == null || attributes == null) return;

		for(int i = 0; i < attributes.getLength(); i ++)	{
			Node attr = attributes.item(i);

			String name = attr.getNodeName();
			String value = attr.getNodeValue();

			if(name.equals("id")) element.addNsAttribute("android","name",value);
			if(name.equals("x1")) element.addNsAttribute("android","startX",value);
			if(name.equals("y1")) element.addNsAttribute("android","startY",value);
			if(name.equals("x2")) element.addNsAttribute("android","endX",value);
			if(name.equals("y2")) element.addNsAttribute("android","endY",value);
			if(name.equals("offset")) element.addNsAttribute("android",name,value);
			if(name.equals("d")) element.addNsAttribute("android","pathData",value);

			if(name.equals("transform")) transformElement(element,value);

			if(name.equals("cx") || name.equals("cy") || name.equals("y") || name.equals("x"))	{
				element.addNsAttribute("android",name,value);
			}

			if(name.equals("rx") || name.equals("ry") || name.equals("r") || name.equals("points"))	{
				element.addNsAttribute("android",name,value);
			}

			if(name.equals("width") || name.equals("height"))	{
				value = value.replaceAll("[A-Za-z]","");
				if("vector".equals(element.getName()))	{
					element.addNsAttribute("android",name,value+"dp");
				}else {
					element.addNsAttribute("android",name,value);
				}
			}

			if(name.equals("xlink:href") && "gradient".equals(element.getName()))	{
				element.addAttribute("childGradient",value.substring(1));
			}

			if(name.equals("viewBox"))	{
				String[] values = value.replace("0 0 ","").split(" ");

				element.addNsAttribute("android","viewportWidth",values[0]);
				element.addNsAttribute("android","viewportHeight",values[1]);
			}

			if(name.equals("fill") || name.equals("stroke") || name.equals("fill-rule"))	{
				styleElement(element,name +":"+ value);
			}

			if(name.equals("opacity") || name.equals("stroke-opacity") || name.equals("fill-opacity"))	{
				styleElement(element,name +":"+ value);
			}

			if(name.equals("stroke-width") || name.equals("stroke-miterlimit") || name.equals("stroke-linecap"))	{
				styleElement(element,name +":"+ value);
			}

			if(name.equals("stroke-linejoin"))	{
				styleElement(element,name +":"+ value);
			}

			if(name.equals("style")) styleElement(element,value);
		}
	}

	public void styleElement(Element element,String style)	{
		String opacity = null;
		String[] styles = style.replaceAll(";$","").split(";");

		for(int i = 0; i < styles.length; i ++)	{
			String[] values = styles[i].split(":");
			String name = values[0];
			String value = values[1];

			if(name.equals("stroke-width")) element.addNsAttribute("android","strokeWidth",value);
			if(name.equals("stroke-linecap")) element.addNsAttribute("android","strokeLineCap",value);
			if(name.equals("stroke-linejoin")) element.addNsAttribute("android","strokeLineJoin",value);
			if(name.equals("stroke-miterlimit")) element.addNsAttribute("android","strokeMiterLimit",value);

			if(name.equals("stop-color"))	{
				Attribute alpha = element.getAttribute("stopAlpha");
				if(alpha != null) {
					String tmpOpacity = opacityToHex(alpha.getValue());
					if(tmpOpacity != null) value = "#" + tmpOpacity + value.substring(1);

					element.removeAttribute("stopAlpha");
				}
				element.addNsAttribute("android","color",value);
			}

			if(name.equals("stop-opacity"))	{
				NsAttribute color = element.getNsAttribute("android","color");
				if(color != null) {
					String tmpOpacity = opacityToHex(value);
					if(tmpOpacity == null) continue;

					value = "#" + tmpOpacity + color.getValue().substring(1);

					element.addNsAttribute("android","color",value);
				}else {
					element.addAttribute("stopAlpha",value);
				}
			}

			if(name.equals("fill-rule"))	{
				value = value.equals("evenodd") ? "evenOdd":"noneZero";
			}

			if(name.equals("fill-opacity")) fillOpacity(element,value);
			if(name.equals("stroke-opacity")) strokeOpacity(element,value);

			if(name.equals("fill") && !value.equals("none"))	{
				if(!value.startsWith("url"))	{
					NsAttribute alpha = element.getNsAttribute("android","fillAlpha");
					if(alpha != null)	{
						String tmpOpacity = opacityToHex(alpha.getValue());
						if(tmpOpacity != null) value = "#"+ tmpOpacity + value.substring(1);

						element.removeNsAttribute("android","fillAlpha");
					}
					element.addNsAttribute("android","fillColor",value);
				}else {
					addGradient(element,name,value);
				}
			}

			if(name.equals("stroke") && !value.equals("none"))	{
				if(!value.startsWith("url"))	{
					NsAttribute alpha = element.getNsAttribute("android","strokeAlpha");
					if(alpha != null)	{
						String tmpOpacity = opacityToHex(alpha.getValue());
						if(tmpOpacity != null) value = "#"+ tmpOpacity + value.substring(1);

						element.removeNsAttribute("android","strokeAlpha");
					}
					element.addNsAttribute("android","strokeColor",value);
				}else {
					addGradient(element,name,value);
				}
			}
		}
	}

	public void transformElement(Element element, String transformation)	{
		if(element == null || transformation == null || transformation.isEmpty()) return;

		String[] transformations = transformation.replace(", ",",").split(" ");
		for(int i = 0; i < transformations.length; i ++)	{
			String[] values = transformations[i].replace("("," ").replace(")","").split(" ");

			String name = values[0];
			String value = values[1];

			if(name.equals("translate"))	{
				String[] translation = value.split(",");

				element.addNsAttribute("android","translateX",translation[0]);
				element.addNsAttribute("android","translateY",translation[0]);

				if(translation.length == 2)	{
					element.addNsAttribute("android","translateY",translation[1]);
				}
			}

			if(name.equals("rotate"))	{
				String[] rotation = value.split(",");

				if(rotation.length == 3)	{
					float px = Float.valueOf(rotation[1]);
					float py = Float.valueOf(rotation[2]);

					element.addNsAttribute("android","pivotX",rotation[1]);
					element.addNsAttribute("android","pivotY",rotation[2]);
				}
				element.addNsAttribute("android","rotate",rotation[0]);
			}

			if(name.equals("scale"))	{
				String[] scale = value.split(",");

				element.addNsAttribute("android","scaleX",scale[0]);
				element.addNsAttribute("android","scaleY",scale[0]);

				if(scale.length == 2)	{
					element.addNsAttribute("android","scaleY",scale[1]);
				}
			}
		}
	}

	public void addGradient(Element element, String name, String value)	{
		if(element == null || name == null || value == null) return;

		String aaptAttr = null;
		if(name.equals("fill"))	{
			aaptAttr = "android:fillColor";
		}else {
			aaptAttr = "android:strokeColor";
		}

		Element aapt = new Element("aapt");
		aapt.addInlineAttribute("name",aaptAttr);

		Element gradient = null;
		String gradientId = value.replace("url(#","").replace(")","");

		for(Object grad : gradients)	{
			String gradId = ((Element) grad).getNsAttribute("android","name").getValue();
			if(gradientId.equals(gradId))	{
				gradient = (Element) grad;
				break;
			}
		}

		if(gradient != null)	{
			if(gradient.hasAttribute("childGradient"))	{
				String childId = gradient.getAttribute("childGradient").getValue();
				for(Object grad : gradients)	{
					String gradId = ((Element) grad).getNsAttribute("android","name").getValue();
					if(childId.equals(gradId))	{
						gradient.addChildren(((Element) grad).getChildren());
						break;
					}
				}
				gradient.removeAttribute("childGradient");
			}
			aapt.addChild(gradient);
			element.addChild(aapt);
		}
	}

	public void shapeToPath(Element element)	{
		// Convert rect element containing rx/ry to path element
		if("rect".equals(element.getName()))	{
			String rxString = null;
			String ryString = null;
			double x = Double.valueOf(element.getNsAttribute("android","x").getValue());
			double y = Double.valueOf(element.getNsAttribute("android","y").getValue());
			double width = Double.valueOf(element.getNsAttribute("android","width").getValue());
			double height = Double.valueOf(element.getNsAttribute("android","height").getValue());

			if(element.hasNsAttribute("android","rx"))	{
				rxString = element.getNsAttribute("android","rx").getValue();
			}else {
				rxString = "0";
			}

			if(element.hasNsAttribute("android","ry"))	{
				ryString = element.getNsAttribute("android","ry").getValue();
			}else {
				ryString = "0";
			}

			if(rxString.equals("0") && !ryString.equals("0")) rxString = ryString;
			if(!rxString.equals("0") && ryString.equals("0")) ryString = rxString;

			double rx = Double.valueOf(rxString);
			double ry = Double.valueOf(ryString);

			String pathData = "M"+ (x+rx) +","+ y +" ";
			pathData += ("L"+ (x+width-rx) +","+ y +" ");
			pathData += ("C"+ (x+width-rx+rx) +","+ y +" "+ (x+width) +","+ (y+ry) +" ");
			pathData += ((x+width) +","+ (y+ry)+ " ");
			pathData += ("L"+ (x+width) +","+ (y+height-ry) + " ");
			pathData += ("C"+ (x+width) +","+ (y+height-ry+ry) +" ");
			pathData += ((x+width-rx) +","+ (y+height) +" "+ (x+width-rx) +","+ (y+height) +" ");
			pathData += ("L"+ (x+rx) +","+ (y+height) +" ");
			pathData += ("C"+ (x+rx-ry) +","+ (y+height) +" "+ x +","+ (y+height-ry) +" ");
			pathData += (x +","+ (y+height-ry) +" ");
			pathData += ("L"+ x +","+ (y+ry) +" ");
			pathData += ("C"+ x +","+ (y+ry-ry) +" "+ (x+rx) +","+ y +" "+ (x+rx) +","+ y);

			element.removeNsAttribute("android","x"); 
			element.removeNsAttribute("android","y"); 
			element.removeNsAttribute("android","rx"); 
			element.removeNsAttribute("android","ry"); 
			element.removeNsAttribute("android","width"); 
			element.removeNsAttribute("android","height");

			element.setName("path");
			element.addNsAttribute("android","pathData",pathData); 
		}

		// Convert circle element containing rx/ry to path element
		if("ellipse".equals(element.getName()) || "circle".equals(element.getName()))	{
			if(element.hasNsAttribute("android","r"))	{
				String r = element.getNsAttribute("android","r").getValue();
				
				element.addNsAttribute("android","rx",r);
				element.addNsAttribute("android","ry",r);

				element.removeNsAttribute("android","r");
			}

			double cx = Double.valueOf(element.getNsAttribute("android","cx").getValue());
			double cy = Double.valueOf(element.getNsAttribute("android","cy").getValue());
			double rx = Double.valueOf(element.getNsAttribute("android","rx").getValue());
			double ry = Double.valueOf(element.getNsAttribute("android","ry").getValue());

			double vh = Double.valueOf(document.rootElement.getNsAttribute("android","viewportHeight").getValue());

			double k = 0.5522847498;

			// // horizontal offset
			double ox = k * rx;

			// // vertical offset
			double oy = k * ry;
			
			String pathData = "M"+ (cx-rx) +","+ cy +" ";
			pathData += ("C"+ (cx-rx) +","+ (cy-oy) +" "+ (cx-ox) +","+ (cy-ry) +" "+ cx +","+ (cy-ry) +" ");
			pathData += ("C"+ (cx+ox) +","+ (cy-ry) +" "+ (cx+rx) +","+ (cy-oy) +" "+ (cx+rx) +","+ cy +" ");
			pathData += ("C"+ (cx+rx) +","+ (cy+oy) +" "+ (cx+ox) +","+ (cy+ry) +" "+ cx +","+ (cy+ry) +" ");
			pathData += ("C"+ (cx-ox) +","+ (cy+ry) +" "+ (cx-rx) +","+ (cy+oy) +" "+ (cx-rx) +","+ cy +" Z");

			element.removeNsAttribute("android","cx"); 
			element.removeNsAttribute("android","cy"); 
			element.removeNsAttribute("android","rx"); 
			element.removeNsAttribute("android","ry"); 

			element.setName("path");
			element.addNsAttribute("android","pathData",pathData);
		}

		if("line".equals(element.getName()))	{
			double x1 = Double.valueOf(element.getNsAttribute("android","startX").getValue());
			double y1 = Double.valueOf(element.getNsAttribute("android","startY").getValue());
			double x2 = Double.valueOf(element.getNsAttribute("android","endX").getValue());
			double y2 = Double.valueOf(element.getNsAttribute("android","endY").getValue());

			String pathData = "M "+ x1 +","+ y1 +" "+ x2 +","+ y2;

			element.removeNsAttribute("android","startX");
			element.removeNsAttribute("android","startY");
			element.removeNsAttribute("android","endX");
			element.removeNsAttribute("android","endY");

			element.setName("path");
			element.addNsAttribute("android","pathData",pathData); 
		}

		if("polyline".equals(element.getName()))	{
			String points = element.getNsAttribute("android","points").getValue();
			points = points.replace(", "," ").replace(","," ");
			System.out.println(points);

			String[] actualPoints = points.split(" ");

			String pathData = "M ";
			for(int j = 0; j < actualPoints.length; j++)	{
				if(j%2 == 0)	{
					pathData += (actualPoints[j] +",");
				}else {
					pathData += (actualPoints[j] +" ");
				}
			}

			pathData = pathData.substring(0,pathData.length() - 1);
			element.removeNsAttribute("android","points");

			element.setName("path");
			element.addNsAttribute("android","pathData",pathData);
		}
	}

	public void strokeOpacity(Element element, String value)	{
		if(value.equals("1")) return;
		
		NsAttribute attr = element.getNsAttribute("android","strokeColor");
		// System.out.println(attr);
		if(attr == null)	{
			element.addNsAttribute("android","strokeAlpha",value);
		}else	{
			String fillColor = attr.getValue();
			String opacity = opacityFromRgbaHex(fillColor);
		// System.out.println(opacity);

			if(opacity == null)	{
				element.addNsAttribute("android","strokeAlpha",value);
				return;
			}

			String color;
			if(attr.getValue().length() == 7)	{
				color = "#" + opacity + "" + value.substring(1);
			}else if(attr.getValue().length() == 9)	{
				color = "#" + opacity + value.substring(3);
			}else	{
				return;
			}
			element.addNsAttribute("android","strokeColor",color);
		}
	}

	public void fillOpacity(Element element, String value)	{
		if(value.equals("1")) return;
		
		NsAttribute attr = element.getNsAttribute("android","fillColor");
		// System.out.println(attr);
		if(attr == null)	{
			element.addNsAttribute("android","fillAlpha",value);
		}else	{
			String fillColor = attr.getValue();
			String opacity = opacityFromRgbaHex(fillColor);
		// System.out.println(opacity);

			if(opacity == null)	{
				element.addNsAttribute("android","fillAlpha",value);
				return;
			}

			String color;
			if(attr.getValue().length() == 7)	{
				color = "#" + opacity + "" + value.substring(1);
			}else if(attr.getValue().length() == 9)	{
				color = "#" + opacity + value.substring(3);
			}else	{
				return;
			}
			element.addNsAttribute("android","fillColor",color);
		}
	}

	public String opacityToHex(String opacity)	{
		String result = null;

		int raw = (int) (Double.valueOf(opacity)*255);
		result = String.format("%x",raw);
		result = result.equals("0") ? "0"+result:result;
		return result;
	}

	public String opacityFromRgbaHex(String color)	{
		String opacity = null;
		
		if(color.length() == 9)	{
			String hex = color.substring(1,3);
			
			float op = Integer.valueOf(hex,16);

			opacity = String.format("%f",op/255.0).replace(",",".");
		}
		return opacity;
	}
}
