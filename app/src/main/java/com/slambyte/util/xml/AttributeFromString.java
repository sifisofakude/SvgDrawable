package com.slambyte.util.xml;

import java.util.ArrayList;

public class AttributeFromString	{
	Document doc;
	Element element;

	boolean isConverting;
	final int FILL_URL_REF = 1;
	final int STROKE_URL_REF = 2;
	final int NO_URL_REF = 3;

	public static int urlRef;;

	public static String incompleteName = "";
	public static String incompleteAttribute = "";

	public AttributeFromString(Document doc,String string,boolean isConverting)	{
		this.doc = doc;
		this.isConverting = isConverting;
		
		element = doc.getElement();
		// System.out.println(element.getName());
		if(element == null) return;
		
		String name = null;
		String value = null;
		
		// String[] pieces = string.split(":");

		String[] pieces = string.split("=");

		name = pieces[0];
		if(pieces.length == 1)	{
			if(pieces[0].lastIndexOf("\"") < 0)	{
				AttributeFromString.incompleteAttribute += (" "+pieces[0]);
				return;
			}else if (pieces[0].lastIndexOf("\"") > 0) {
				name = AttributeFromString.incompleteName;

				AttributeFromString.incompleteAttribute += (" "+pieces[0]);
				value = AttributeFromString.incompleteAttribute;
				value = value.replaceAll("\"|\\/|>","").trim();
			}
		}else {
			value = pieces[1].replaceAll("\"|\\/|>","").trim();
			if(pieces[1].lastIndexOf("\"") == 0)	{
				AttributeFromString.incompleteName = name;
				AttributeFromString.incompleteAttribute += (" "+value);
				return;
			}
		}

		// System.out.println(name +" "+ value);

		if(name.equals("width") || name.equals("height"))	{
			double d = Double.valueOf(value.replaceAll("[a-zA-Z]",""));
			String s = String.format("%.2f",d).replace(",",".");
			if(isConverting)	{
				element.addNsAttribute("android",name,s + "dp");
			}else	{
				element.addAttribute(name,s);
			}
		}

		if(name.equals("id"))	{
			if(isConverting)	{
				element.addNsAttribute("android","name",value);
			}else	{
				element.addAttribute(name,value);
			}
		}

		if(name.equals("viewBox"))	{
			String[] values = value.split(" ");

			double vpWidthDouble = Double.valueOf(values[2]);
			String vpWidth = String.format("%.2f",vpWidthDouble).replace(",",".");

			double vpHeightDouble = Double.valueOf(values[3]);
			String vpHeight = String.format("%.2f",vpHeightDouble).replace(",",".");

			if(isConverting)	{
				element.addNsAttribute("android","viewportWidth",vpWidth);
				element.addNsAttribute("android","viewportHeight",vpHeight);
			}else	{
				element.addAttribute(name,value);
			}
		}

		if(name.equals("d"))	{
			if(isConverting)	{
				element.addNsAttribute("android","pathData",value);
			}else	{
				element.addAttribute(name,value);
			}
		}

		if(name.equals("fill"))	{
			styleElement(name + ":" + value);
		}

		if(name.equals("fill-opacity"))	{
			styleElement(name + ":" + value);
		}

		if(name.equals("stroke"))	{
			styleElement(name + ":" + value);
		}

		if(name.equals("stroke-opacity"))	{
			styleElement(name + ":" + value);
		}

		if(name.equals("stroke-width"))	{
			styleElement(name + ":" + value);
		}

		if(name.equals("stroke-linejoin"))	{
			styleElement(name + ":" + value);
		}

		if(name.equals("stroke-miterlimit"))	{
			styleElement(name + ":" + value);
		}

		if(name.equals("fill-rule"))	{
			styleElement(name + ":" + value);
		}

		if(name.equals("style"))	{
			styleElement(value);
		}

		if(name.equals("transform"))	{
			transform(value);
		}

		if(name.equals("android:width"))	{
			element.addAttribute("width",value.replaceAll("[a-zA-Z]",""));
		}

		if(name.equals("android:height"))	{
			element.addAttribute("height",value.replaceAll("[a-zA-Z]",""));
		}

		if(name.equals("android:viewportHeight"))	{
			Attribute attr = element.getAttribute("viewBox");
			if(attr != null)	{
				String tmp = attr.getValue();
				value = tmp + " " +value;
			}else	{
				value = "0 0 0 "+value;
			}
			element.addAttribute("viewBox",value);
		}

		if(name.equals("android:viewportWidth"))	{
			Attribute attr = element.getAttribute("viewBox");
			if(attr != null)	{
				String tmp = attr.getValue();
				String[] tmpValue = tmp.split(" ");
				value = "0 0 "+value + " " + tmpValue[3];
				attr.setValue(value);
			}else	{
				value = "0 0 "+value;
				element.addAttribute("viewBox",value);
			}
		}

		if(name.equals("android:name"))	{
			String tmpValue = null;
			if(AttributeFromString.urlRef == FILL_URL_REF)	{
				tmpValue = "fill";
			}

			if(AttributeFromString.urlRef == STROKE_URL_REF)	{
				tmpValue = "stroke";
			}


			if(doc.currentTag == Document.ELEMENT_TAG_GRADIENT)	{
				// System.out.println(AttributeFromString.urlRef);
				if(AttributeFromString.urlRef != NO_URL_REF)	{
					Attribute attr = new Attribute(tmpValue,"url(#"+value+")");
					doc.currentElement.getAttributes().add(1,attr);
					// doc.currentElement.setName("bush");
				}
			}
			element.addAttribute("id",value);
		}

		if(name.equals("android:pathData"))	{
			element.addAttribute("d",value);
		}

		if(name.equals("android:fillColor"))	{
				if(!value.startsWith("@drawable/"))	{
				Attribute attr = element.getAttribute("style");
				
				String opacity = opacityFromRgbaHex(value);
				if(attr != null)	{
					String tmpValue = attr.getValue();
					tmpValue += ";fill:" + ((opacity != null) ? "#" +value.substring(3):value);

					value = tmpValue;
				}else	{
					value = "fill:" + ((opacity != null) ? "#" +value.substring(3):value);
				}

				if(opacity != null) value += ";fill-opacity:"+opacity;
				element.addAttribute("style",value);
			}
		}else {
			// AttributeFromString.urlRef = FILL_URL_REF;
		}

		if(name.equals("android:fillApha"))	{
			Attribute attr = element.getAttribute("style");
			if(attr != null)	{
				String tmpValue = attr.getValue();
				tmpValue += ";fill-opacity:" + value;

				value = tmpValue;
			}else	{
				value = "fill-opacity:" + value;
			}
			element.addAttribute("style",value);
		}

		if(name.equals("android:strokeColor"))	{
			Attribute attr = element.getAttribute("style");

			String opacity = opacityFromRgbaHex(value);
			if(attr != null)	{
				String tmpValue = attr.getValue();
				tmpValue += ";stroke:" + ((opacity != null) ? "#" +value.substring(3):value);

				value = tmpValue;
			}else	{
				value = "stroke:" + ((opacity != null) ? "#" +value.substring(3):value);
			}

			if(opacity != null) value += ";stroke-opacity:"+opacity;
			element.addAttribute("style",value);
		}

		if(name.equals("android:strokeAlpha"))	{
			Attribute attr = element.getAttribute("style");
			if(attr != null)	{
				String tmpValue = attr.getValue();
				tmpValue += ";stroke-opacity:" + value;

				value = tmpValue;
			}else	{
				value = "stroke-opacity:" + value;
			}
			element.addAttribute("style",value);
		}

		if(name.equals("android:strokeLineCap"))	{
			Attribute attr = element.getAttribute("style");
			if(attr != null)	{
				String tmpValue = attr.getValue();
				tmpValue += ";stroke-linecap:" + value;

				value = tmpValue;
			}else	{
				value = "stroke-linecap:" + value;
			}
			element.addAttribute("style",value);
		}

		if(name.equals("android:strokeLineJoin"))	{
			Attribute attr = element.getAttribute("style");
			if(attr != null)	{
				String tmpValue = attr.getValue();
				tmpValue += ";stroke-linejoin:" + value;

				value = tmpValue;
			}else	{
				value = "stroke-linejoin:" + value;
			}
			element.addAttribute("style",value);
		}

		if(name.equals("android:strokeMiterLimit"))	{
			Attribute attr = element.getAttribute("style");
			if(attr != null)	{
				String tmpValue = attr.getValue();
				tmpValue += ";stroke-miterlimit:" + value;

				value = tmpValue;
			}else	{
				value = "stroke-miterlimit:" + value;
			}
			element.addAttribute("style",value);
		}

		if(name.equals("android:strokeWidth"))	{
			Attribute attr = element.getAttribute("style");
			if(attr != null)	{
				String tmpValue = attr.getValue();
				tmpValue += ";stroke-width:" + value;

				value = tmpValue;
			}else	{
				value = "stroke-width:" + value;
			}
			element.addAttribute("style",value);
		}

		if(name.equals("android:fillType"))	{
			Attribute attr = element.getAttribute("style");
			if(attr != null)	{
				String tmpValue = attr.getValue();
				tmpValue += ";fill-rule:" + value.toLowerCase();

				value = tmpValue;
			// System.out.println(attr.getValue());
			}else	{
				value = "fill-rule:"+value.toLowerCase();
			}
			element.addAttribute("style",value);

		}

		if(name.equals("android:color"))	{
			String opacity = opacityFromRgbaHex(value);
			if(opacity != null)	{
				value = "stop-color: #"+value.substring(3)+";stop-opacity: "+opacity;
			}else {
				value = "stop-color: "+value;
			}

			Attribute attr = element.getAttribute("style");
			if(attr != null)	{
				String tmpValue = attr.getValue();
				value = (tmpValue+";"+value);
			}
			element.addAttribute("style",value);
			// System.out.println(value);
		}

		if(name.equals("android:offset"))	{
			element.addAttribute("offset",value);
		}

		if(name.equals("name"))	{
			if(doc.currentTag != Document.ELEMENT_TAG_AAPT)	{
				AttributeFromString.urlRef = NO_URL_REF;
				return;
			}

			if(value.equals("android:fillColor") || value.equals("android:strokeColor"))	{
				if(value.equals("android:fillColor"))	{
					AttributeFromString.urlRef = FILL_URL_REF;
				}else 	{
					AttributeFromString.urlRef = STROKE_URL_REF;
				}
				// System.out.println(urlRef);
			}
		}

		// if()

		if(name.equals("x1"))	{
			element.addNsAttribute("android","startX",value);
		}

		if(name.equals("x2"))	{
			element.addNsAttribute("android","endX",value);
		}

		if(name.equals("y1"))	{
			element.addNsAttribute("android","startY",value);
		}

		if(name.equals("y2"))	{
			element.addNsAttribute("android","endY",value);
		}

		if(name.equals("offset"))	{
			element.addNsAttribute("android","offset",value);
		}

		if(name.equals("xlink:href"))	{
			element.addAttribute("childGradient",value.trim().substring(1));
		}

		if(name.equals("android:startX"))	{
			element.addAttribute("x1",value);
		}

		if(name.equals("android:startY"))	{
			element.addAttribute("y1",value);
		}

		if(name.equals("android:endX"))	{
			element.addAttribute("x2",value);
		}

		if(name.equals("android:endY"))	{
			element.addAttribute("y2",value);
		}

		if(name.equals("android:type"))	{
			if(value.equals("linear") || value.equals("radial"))	{
				element.setName(value+"Gradient");
			}
		}
	}

	public void styleElement(String style)	{
		if(isConverting)	{
			for(String s:style.split(";"))	{
				// System.out.println(s);
				String name = s.split(":")[0];
				String value = s.split(":")[1];

				if(name.equals("fill"))	{
					value = value.trim();
					if(value.startsWith("#"))	{
						NsAttribute attr = element.getNsAttribute("android","fillApha");

						if(attr == null)	{
							element.addNsAttribute("android","fillColor",value);
						}else	{
							String fillAlpha = attr.getValue();
							String opacity = opacityToHex(fillAlpha);

							String color = "#" + opacity + "" + value.substring(1);
							element.addNsAttribute("android","fillColor",color);
						}
					}else if(value.startsWith("url")) {
						Element tmpElement = new Element("aapt:attr");
						tmpElement.addInlineAttribute("name","android:fillColor");

						String id = value.substring(5,value.length()-1);
						tmpElement.addAttribute("gradientId",id);

						doc.aapts.add(tmpElement);
						element.addChild(tmpElement);
						// System.out.println(element.getName());
					}
				}

				if(name.equals("fill-opacity"))	{
					if(value.equals("1")) continue;
					
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
							continue;
						}

						String color;
						if(attr.getValue().length() == 7)	{
							color = "#" + opacity + "" + value.substring(1);
						}else if(attr.getValue().length() == 9)	{
							color = "#" + opacity + value.substring(3);
						}else	{
							continue;
						}
						element.addNsAttribute("android","fillColor",color);
					}
				}

				if(name.equals("stroke"))	{
					value = value.trim();
					if(value.startsWith("#"))	{
						NsAttribute attr = element.getNsAttribute("android","strokeApha");

						if(attr == null)	{
							element.addNsAttribute("android","strokeColor",value);
						}else	{
							String fillAlpha = attr.getValue();
							String opacity = opacityFromRgbaHex(fillAlpha);

							String color = "#" + opacity + "" + value.substring(1);
							element.addNsAttribute("android","strokeColor",color);
						}
					}else if(value.startsWith("url")) {
						Element tmpElement = new Element("aapt:attr");
						tmpElement.addInlineAttribute("name","android:strokeColor");

						String id = value.substring(5,value.length()-1);
						tmpElement.addAttribute("gradientId",id);

						doc.aapts.add(tmpElement);
						element.addChild(tmpElement);
					}
				}

				if(name.equals("stroke-opacity"))	{
					if(value.equals("1")) continue;
				// continue;
					NsAttribute attr = element.getNsAttribute("android","strokeColor");

					if(attr == null)	{
					// System.out.println(value);
						element.addNsAttribute("android","strokeAlpha",value);
					}else	{
						String hex = attr.getValue();
						String opacity = opacityFromRgbaHex(hex);

						if(opacity == null)	{
							element.addNsAttribute("android","strokeAlpha",value);
							continue;
						}

						String color;
						if(attr.getValue().length() == 7)	{
							color = "#" + opacity + "" + value.substring(1);
						}else if(attr.getValue().length() == 9)	{
							color = "#" + opacity + value.substring(3);
						}else	{
							continue;
						}
						element.addNsAttribute("android","strokeColor",color);
					}
				}

				if(name.equals("stroke-width"))	{
					value = value.replaceAll("[a-zA-Z]","");
					double sw = Double.valueOf(value);
					if(sw > 0.0)	{
						element.addNsAttribute("android","strokeWidth",value);
					}
				}

				if(name.equals("stroke-linecap"))	{
					element.addNsAttribute("android","strokeLineCap",value);
				}

				if(name.equals("stroke-miterlimit"))	{
					element.addNsAttribute("android","strokeMiterLimit",value);
				}

				if(name.equals("stroke-linejoin"))	{
					element.addNsAttribute("android","strokeLineJoin",value);
				}

				if(name.equals("fill-rule"))	{
					String type;
					if(value.equals("evenodd"))	type = "evenOdd";
					else type = "nonZero";

					element.addNsAttribute("android","fillType",type);
				}

				if(name.equals("stop-color"))	{
					NsAttribute attr = element.getNsAttribute("android","color");
					if(attr != null)	{
						String tmpColor = attr.getValue();
						if(tmpColor.length() == 3)	{
							attr.setValue(tmpColor+""+value.trim().substring(1));
						}else {
							attr.setValue(value.trim());
						}
					}else {
						element.addNsAttribute("android","color",value.trim());
					}
				}

				if(name.equals("stop-opacity"))	{
					String opacity = opacityToHex(value.trim());
					NsAttribute attr = element.getNsAttribute("android","color");
					if(attr != null)	{
						String tmpColor = attr.getValue();
						if(tmpColor.length() == 7)	{
							attr.setValue("#"+ opacity +""+tmpColor.substring(1));
						}else {
							attr.setValue("#"+ opacity + "" +tmpColor.substring(3));
						}
					}else {
						element.addNsAttribute("android","color","#"+opacity);
					}
				}
			}
		}else	{
			Attribute attr = element.getAttribute("style");
			if(attr != null)	{
				String tmpVal = attr.getValue() + ";" + style;
				attr.setValue(tmpVal);
			}else	{
				element.addAttribute("style",style);
			}
		}
		
	}

	public void transform(String transformation)	{
		String key = null;
		String value = null;
		String translateX = null;
		String translateY = null;

		if(transformation.startsWith("translate"))	{
			transformation = transformation.replaceAll("translate\\(|\\)","");
			String[] translate = transformation.split(",");

			translateX = translate[0];
			if(translate.length == 2)	{
				translateY = translate[1];
			}
		// System.out.println(transformation);

		}
		// if(doc.currentTag == Document.ELEMENT_TAG_GRADIENT)	{
			// Element aapt = new Element("aapt:attr");
			if(translateX != null) element.addNsAttribute("android","translateX",translateX);
			if(translateY != null) element.addNsAttribute("android","translateY",translateX);
		// }
	}

	public String opacityToHex(String opacity)	{
		String result = null;

		int raw = (int) (Double.valueOf(opacity)*255);
		result = String.format("%x",raw);
		result = result.equals("0") ? "0"+result:result;
		// System.out.println(result);
		return result;
	}

	public String opacityFromRgbaHex(String color)	{
		String opacity = null;
		
		if(color.length() == 9)	{
			String hex = color.substring(1,3);
			
			// hex to int
			float op = Integer.valueOf(hex,16);

			opacity = String.format("%f",op/255.0).replace(",",".");
		}
		return opacity;
	}
}
