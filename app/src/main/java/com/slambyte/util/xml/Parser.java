package com.slambyte.util.xml;

import java.io.File;

import java.nio.file.Path;

import java.util.ArrayList;

public class Parser	{
	Document doc;

	public Parser()	{
		 doc = Document.getInstance();
	}
	
	public void parseFile(String input,String output,ArrayList<String> options)	{
		if(input == null || input.isEmpty()) return;

		String extension = input.substring(input.length()-3);

		if(output == null)	{
			output = input.substring(0,input.length()-3);
			if(extension.equals("xml"))	{
				output += "svg";
			}else if(extension.equals("svg"))	{
				output += "xml";
			}else	{
				output += extension;
			}
		}

		if(extension.equals("svg"))	{
			new ToDrawable(doc,input);
		}else {
			new ToSvg(doc,input);
		}

		writeToFile(output);
	}

	public boolean writeToFile(String path)	{
		if(path != null || !path.isEmpty())	{
			doc.writeToFile(path);
			return true;
		}
		return false;
	}

	public Element getElement()	{
		return doc.rootElement;
	}
}
