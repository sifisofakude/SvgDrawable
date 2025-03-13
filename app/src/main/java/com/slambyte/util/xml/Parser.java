package com.slambyte.util.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.io.InputStreamReader;

import java.nio.fuile.Path;

import java.util.ArrayList;

public class Parser	{
	public static final int CONVERTING_TO_SVG = 1;
	public static final int CONVERTING_TO_DRAWABLE = 2;
	public static final int NOT_CONVERTING = 3;

	private int currentProcess = NOT_CONVERTING;
	Document doc;

	public Parser()	{
		 doc = Document.getInstance();
	}

	public void setCurrentProcess(int process)	{
		switch(process)	{
			case Parser.CONVERTING_TO_DRAWABLE:
			case Parser.CONVERTING_TO_SVG:
				currentProcess = process;
				break;
			default:
				currentProcess = Parser.NOT_CONVERTING;
		}
	}
	
	public void parseFile(String input,String output,ArrayList<String> options)	{
		if(input == null || input.isEmpty()) return;

		String extension = input.substring(input.length()-3);
		if(extension.equals("svg"))	{
			currentProcess = CONVERTING_TO_DRAWABLE;
		}else if(extension.equals("xml"))	{
			currentProcess = CONVERTING_TO_SVG;
		}else 	{
			currentProcess = NOT_CONVERTING;
		}

		if(output == null)	{
			output = input.substring(0,input.length()-3);
			if(currentProcess == Parser.CONVERTING_TO_SVG)	{
				output += "svg";
			}else if(currentProcess == Parser.CONVERTING_TO_DRAWABLE)	{
				output += "xml";
			}else	{
				output = null;
			}
		}

		try	{
			BufferedReader br = new BufferedReader(new FileReader(input));
			String line;
			while((line = br.readLine()) != null)	{
				processLine(line);
			}
			// doc.writeToFile(output);
			Element el = doc.getElement();
			while(el.getParent() != null)	{
				Element parent = el.getParent();
				// System.out.println(parent.getName() + " " + el.getName());
				if(el.getName().equals(parent.getName())) break;
				el = parent;
			}

			if(options != null && options.size() > 0 && "vector".equals(el.getName()))	{
				doc.gradientsLinked = false;

				Path file = new File(input).toPath();
				System.out.println(file.getParent().toString());
				for(String option : options)	{
					switch(option)	{
					case "--clean-duplicates"-> doc.cleanDuplicates(el);
					case "--external-gradients"-> doc.saveGradients(el);
					}
				}
			}

			if(doc.gradientsLinked)	{
				doc.populateAapts();
				NsAttribute attr = new NsAttribute("xmlns","aapt","http://schemas.android.com/aapt");
				el.getNsAttributes().add(1,attr);
				doc.gradients.clear();
			}

			if(doc.gradients.size() > 0 && "svg".equals(el.getName()))	{
				el.addNsAttribute("xmlns","xlink","http://www.w3.org/1999/xlink");
				Element tmpElement = new Element("defs");
				tmpElement.addAttribute("id","defs1");
				ArrayList<Element> children = el.getChildren();
				children.add(0,tmpElement);
				for(Element elem : doc.gradients)	{
					tmpElement.addChild(elem);
				}
			}

			// doc.checkForDuplicates(el);
			doc.printFormatted(el,0);
			// doc.toString(el,0);
			// System.out.println(doc.docStr);
			doc.writeToFile(output);
		}catch(IOException e)	{}
	}
	
	public void parseFile(String input,int process)	{
		if(input == null || input.replaceAll("\\s\\|\\t\\","").isEmpty()) return;

		if(process == Parser.CONVERTING_TO_DRAWABLE)	{
			currentProcess = Parser.CONVERTING_TO_DRAWABLE;
		}else if(process == Parser.CONVERTING_TO_SVG)	{
			currentProcess = Parser.CONVERTING_TO_SVG;
		}else 	{
			currentProcess = Parser.NOT_CONVERTING;
		}

		try	{
			BufferedReader br = new BufferedReader(new FileReader(input));
			String line;
			while((line = br.readLine()) != null)	{
				processLine(line);
			}
			Element element = doc.getElement();
			while(element.getParent() != null)	{
				element = element.getParent();
			}

			Element tmpElement = new Element("g");
			tmpElement.addAttribute("id","svglite_layer1");

			ArrayList<Element> children = element.getChildren();
			tmpElement.addChildren(children);
			children.clear();
			element.addChild(tmpElement);

			if(doc.gradientsLinked)	{
				doc.gradients.clear();
			}

			if(doc.gradients.size() > 0)	{
				doc.unlinkGradients();
				element.addNsAttribute("xmlns","xlink","http://www.w3.org/1999/xlink");
				tmpElement = new Element("defs");
				tmpElement.addAttribute("id","defs1");
				
				children = element.getChildren();
				children.add(0,tmpElement);
				for(Element elem : doc.gradients)	{
					tmpElement.addChild(elem);
				}
			}
			// doc.printFormatted(element,0);
			// doc.writeToFile(output);
		}catch(IOException e)	{}
	}

	public void createSvgSkeleton(String width,String height)	{
		if(width == null || width.isEmpty()) return;
		if(height == null || height.isEmpty()) return;
		
		Element element = new Element("svg");
		element.addAttribute("xmlns","http://www.w3.org/2000/svg");
		element.addAttribute("width",width);
		element.addAttribute("height",height);

		String viewBox = String.format("0 0 %s %s",width,height);
		element.addAttribute("viewBox",viewBox);

		doc.setCurrentElement(element);
		// doc.printFormatted(element,0);
	}

	public boolean writeToFile(String path)	{
		if(path != null || !path.isEmpty())	{
			doc.writeToFile(path);
			return true;
		}
		return false;
	}

	public Element getElement()	{
		return doc.getElement();
	}

	public void processLine(String line)	{
		// System.out.println(line.trim());
		if(line == null || line.isEmpty()) return;

		line = line.trim();
		
		SearchForElements elements = new SearchForElements(line);
		if(elements.length() > 0)	{
			String tmpLine;
			while((tmpLine = elements.getLine()) != null)	{
				// if(currentProcess == CONVERTING_TO_DRAWABLE)
				ElementOrAttribute ea = new ElementOrAttribute(tmpLine);

				String tmpLineII;
				while((tmpLineII = ea.getLine()) != null)	{
					if(currentProcess == Parser.CONVERTING_TO_DRAWABLE)	{
						new ToDrawable(doc,tmpLineII);
					}else if(currentProcess == Parser.CONVERTING_TO_SVG)	{
						new ToSvg(doc,tmpLineII);
					}else	{
						new RawSvg(doc,tmpLineII);
					}
				}
			}
		}else	{
			ElementOrAttribute ea = new ElementOrAttribute(line);

			String tmpLine;
			while((tmpLine = ea.getLine()) != null)	{
				if(currentProcess == Parser.CONVERTING_TO_DRAWABLE)	{
					new ToDrawable(doc,tmpLine);
				}else if(currentProcess == Parser.CONVERTING_TO_SVG)	{
					new ToSvg(doc,tmpLine);
				}else	{
					new RawSvg(doc,tmpLine);
				}
			}
		}
	}

	public class ElementOrAttribute	{
		ArrayList<String> lines = new ArrayList<String>();

		public ElementOrAttribute(String line)	{
			boolean quote_hit = false;
			boolean space_hit = false;
			String incomplete_line = "";

			int start_index = -1;
			int end_index = -1;

			String tmpLine;
			
			for(int i = 0; i < line.length(); i ++)	{
				String character = line.substring(i,i+1);


				if(character.equals("\"")) quote_hit = quote_hit ? false:true;

				String EOL = line.substring(i+1);
				boolean isLineEnded = (EOL.equals("/>") || EOL.equals(">")) ? true:false;
			
				if(character.equals(" ") && !isLineEnded)	{
					if(!quote_hit) end_index = i;
				}else	{
					if(start_index == -1) start_index = i;
				}

				if(start_index > -1 && end_index > -1)	{
					lines.add(line.substring(start_index,end_index));
					
					start_index = -1;
					end_index = -1;
				}

				if(start_index > -1 && end_index == -1 && line.length() == i+1)	{
					lines.add(line.substring(start_index));
				}
			}
		}

		public String getLine()	{
			if(lines.size() > 0)	{
				String tmpLine = lines.get(0);
				lines.remove(0);
				
				return tmpLine;
			}
			return null;
		}

		public int length()	{
			return lines.size();
		}
	}

	public class SearchForElements	{
		ArrayList<String> lines = new ArrayList<String>();
		
		public SearchForElements(String line)	{
			if(line != null && !line.isEmpty())	{
				int end_index = -1;
				int start_index = -1;
				for(int i = 0; i < line.length(); i ++)	{
					if(line.substring(i,i+1).equals("<")) start_index = i;
					if(line.substring(i,i+1).equals(">")) end_index = i;

					if(start_index > -1 && end_index > -1)	{
						lines.add(line.substring(start_index,end_index+1));
						start_index = -1;
						end_index = -1;
					}
				}
			}
		}

		public String getLine()	{
			if(lines.size() > 0)	{
				String line = lines.get(0);
				lines.remove(0);
				return line;
			}
			return null;
		}

		public int length()	{
			return lines.size();
		}
	}
}
