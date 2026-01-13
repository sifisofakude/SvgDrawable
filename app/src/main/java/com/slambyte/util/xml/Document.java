package com.slambyte.util.xml;

import java.io.File;
import java.io.PrintStream;
import java.io.IOException;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.ListIterator;

public class Document	{
	public Element rootElement = null;

	public String docStr = "";
	public String rootName = null;

	private static Document instance = null;

	private Document()	{
	}

	public static Document getInstance()	{
		if(instance == null) instance = new Document();
		return instance;
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

			String fe = filename.substring(filename.length()-3);
			if(fe.equals("xml"))	{
				ps.printf("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			}else	{
				ps.printf("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n");
			}

			writtingToFile(ps,rootElement,0);
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
