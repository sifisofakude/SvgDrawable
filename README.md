# SVGDrawable

SVGDrawable is a java CLI/library for converting SVG images to Android Vector Drawables and vice versa without using Android Studio and offline.

## Features
- Can parse gradients from external file when converting to SVG.
- Save gradients to external file when converting to Android Drawable.
- Merge duplicated path elements.
## Usage

### #CLI

```
$ java -jar /path/to/svgdrawable.jar --help
usage: java -jar /path/to/svgdrawable.jar inputfile [outputfile] [options]

options:
   --clean-duplicates      merges paths elements with same
                           styling in to one path element. make sure your
                           paths are using absolute coordinates to avoid any
                           inconsistancies.

   --external-gradients    parses gradients from inpufile and
                           save them in a separate file,only works when
                           converting to Android Vector Drawable and
                           saves the file in the res/drawable folder 
                           according to their android:name(e.g if
                           android:name=gradient52, then gradient file
                           name will be res/drawable/gradient52.xml). If
                           gradient is without the android:name, file name
                           will be auto generated.
```

### #As Library
#### Parsing A File
```
import com.slambyte.util.xml.Parser;

Parser parser = new Parser();
parser.parseFile("/path/to/file",process);
```
`process` can be any of the following:
```
int Parser.NOT_CONVERTING
int Parser.CONVERTING_TO_SVG
int Parser.CONVERTING_TO_DRAWABLE
```
To save the converted result to a file, you use `parser.writeToFile("/path/to/file");`
