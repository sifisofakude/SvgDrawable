/*
 * Copyright 2025 Sifiso Fakude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slambyte.util.xml;

import java.util.ArrayList;

public class App {
	public static void help()	{
		System.out.println(
	 		"usage: java -jar svgdrawable.jar inputfile [outputfile] [options]\n\n"+
	 		"   options:\n"+
	    "      --clean-duplicates     merges paths elements with same styling in to one\n"+
	    "                             path element of not more than 800 characters, if\n"+
	    "                             path element excedes 800 characters creating a\n"+
	    "                             duplicate\n\n"+
	   	"      --external-gradients   parses gradients from inpufile and save them in a\n"+
	   	"                             separate file,only works when converting to Android\n"+
	   	"                             Vector Drawable and saves the file in the\n"+
	   	"                             res/drawable folder according to their\n"+
	   	"                             android:name(e.g if android:name=gradient52,\n"+ 
	   	"                             then gradient file name will be\n"+
	   	"                             res/drawable/gradient52.xml). If gradient\n"+
	   	"                             is without the android:name, file name will be\n"+
	   	"                             auto generated.\n"
		);
	}

	public static void main(String[] args) {
		if(args.length == 0)	{
			App.help();
		}

		String input = null;
		String output = null;

		ArrayList<String> options = new ArrayList<String>();

		for(String arg : args)	{
			if(arg.startsWith("--"))	{
				options.add(arg);
			}else {
				if(input == null)	{
					input = arg;
				}else if(output == null)	{
					output = arg;
				}
			}
		}

		new Parser().parseFile(input,output,options);
		// new Parser().createSvgSkeleton("200","250");
	}
}

