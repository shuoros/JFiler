<p align="center">
 <img src="https://user-images.githubusercontent.com/45015114/149667480-788c34bb-a13b-4e81-93de-fe0ed9420c19.png" style="width:150px; height:150px" align="center" alt="JFiler" />
 <h2 align="center">JFiler</h2>
 <p align="center">Is a library for managing files in Java which easily and with the least line of code gives you
  the ability to manage files like moving through folders and directories, reading files and folders information,
  creating new files or folders, making changes to files and folders such as renaming or hiding them, deleting files
  and folders, searching for files or folders by regex and compressing files and folders or extracting them from zip files.</p>
</p>
  <p align="center">
    <a href="https://github.com/shuoros/JFiler/actions">
      <img src="https://img.shields.io/github/workflow/status/shuoros/JFiler/Test?label=Test&style=for-the-badge" />
    </a>
    <a href="https://mvnrepository.com/artifact/io.github.shuoros/JFiler">
      <img src="https://img.shields.io/maven-central/v/io.github.shuoros/JFiler?style=for-the-badge" />
    </a>
    <a href="https://www.codefactor.io/repository/github/shuoros/jterminal">
      <img alt="code factor" src="https://img.shields.io/codefactor/grade/github/shuoros/jfiler/main?style=for-the-badge" />
    </a>
    <a href="#">
      <img alt="Contributors" src="https://img.shields.io/github/contributors/shuoros/jfiler?style=for-the-badge&color=blueviolet" />
    </a>
    <a href="https://github.com/shuoros/JFiler/blob/main/LICENSE">
      <img alt="License" src="https://img.shields.io/github/license/shuoros/jfiler?style=for-the-badge" />
    </a>
    <br />
    <br />
    <a href="https://github.com/shuoros/JFiler/issues">
      <img src="https://img.shields.io/github/issues-raw/shuoros/jfiler?style=for-the-badge&color=red"/>
    </a>
    <a href="https://github.com/shuoros/JFiler/issues">
      <img src="https://img.shields.io/github/issues-closed-raw/shuoros/jfiler?style=for-the-badge"/>
    </a>
  </p>
  <p align="center">
	If you like this project, help me by giving me a star =))<3
  </p>

## What is in V1.1.0

- Open JFiler in a directory and move in files and folders.
- Lock home to limit access to just files and folders of a specific location.
- Create new files and folders.
- Copy, Cut, Paste, Rename, Delete files and folders.
- Hide or Unhide files and folders.
- Zip files and folders.
- Extract zip files.

## Hello JFiler

To use JFiler you just need to make a simple call to your desired API and JFiler will do the rest.

For example JFiler's Static APIs:

```java
import io.github.shuoros.jcompressor.compress.ZipCompressor;
import io.github.shuoros.jfiler.JFiler;
import io.github.shuoros.jfiler.file.File;

public class Main {
    public static void main(String[] args) {
        File file = File.open(//
                "/home/soroush/Desktop/test.file");
        try {
            // hide a file or folder
            JFiler.hide(file);

            // make a file visible
            JFiler.unHide(file);

            // rename a file or folder
            JFiler.rename(file, "newTest.file");

            // move a file or folder to a destination
            File destination = File.open(//
                    "/home/soroush/Desktop/newFolder/newTest.file");
            JFiler.moveTo(file, destination);

            // copy a file or folder to a destination
            JFiler.copyTo(destination, file);
            
            // delete a file or folder
            JFiler.deleteThe(destination);
            
            // create a new file or folder
            JFiler.createNewFile("/home/soroush/Desktop/new.file");
            JFiler.createNewFolder("/home/soroush/Desktop/newFolder");

            // compress files or folders to zip file
            File zipFile = File.open(//
                    "/home/soroush/Desktop/file.zip");
            JFiler.compress(file, zipFile, new ZipCompressor());

            // extract a zip file
            File desktop = File.open(//
                    "/home/soroush/Desktop");
            JFiler.extract(zipFile, desktop, new ZipCompressor());

            // search for a regex in a dir
            List<String> foundedFiles = JFiler.search(//
                    ".txt$"//
                    , desktop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

For example JFiler's Home APIs:

```java
import io.github.shuoros.jfiler.JFiler;

public class Main {
    public static void main(String[] args) {
        // Opens a JFiler instance in desktop
        JFiler desktop = JFiler.open("/home/soroush/Desktop");
        try {
            // List of home's files and folders
            List<File> listOfFiles = desktop.getList();
            
            // open a folder
            desktop.openFolder("newFolder");
            
            // go backward
            desktop.goBackward();
            
            // go forward
            desktop.goForward();
            
            // go up in parent folder
            desktop.goUp();
            
            // copy/cut and paste files
            desktop.copy("/test.file");
            desktop.paste("/newFolder/test.file");
            
            // delete a file
            desktop.delete("/newFolder/test.file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Installation

You can use **JFiler** with any project management tool:

### Maven

```xml
<!-- https://mvnrepository.com/artifact/io.github.shuoros/JFiler -->
<dependency>
    <groupId>io.github.shuoros</groupId>
    <artifactId>JFiler</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Gradle

```gradle
// https://mvnrepository.com/artifact/io.github.shuoros/JFiler
implementation group: 'io.github.shuoros', name: 'JFiler', version: '1.1.0'
```

Or

```gradle
// https://mvnrepository.com/artifact/io.github.shuoros/JFiler
implementation 'io.github.shuoros:JFiler:1.1.0'
```

And in **Kotlin**

```gradle
// https://mvnrepository.com/artifact/io.github.shuoros/JFiler
implementation("io.github.shuoros:JFiler:1.1.0")
```

### SBT

```sbt
// https://mvnrepository.com/artifact/io.github.shuoros/JFiler
libraryDependencies += "io.github.shuoros" % "JFiler" % "1.1.0"
```

### Ivy

```xml
<!-- https://mvnrepository.com/artifact/io.github.shuoros/JFiler -->
<dependency org="io.github.shuoros" name="JFiler" rev="1.1.0"/>
```

### Grape

```java
// https://mvnrepository.com/artifact/io.github.shuoros/JFiler
@Grapes(
        @Grab(group = 'io.github.shuoros', module = 'JFiler', version = '1.1.0')
)
```

### Leiningen

```clj
;; https://mvnrepository.com/artifact/io.github.shuoros/JFiler
[io.github.shuoros/JFiler "1.1.0"]
```

## Authors

JFiler is developed by [Soroush Shemshadi](https://github.com/shuoros)
and [contributors](https://github.com/shuoros/JFiler/blob/main/CONTRIBUTORS.md).

## Contribution

If you want to contribute on this project, Please read
the [contribution guide](https://github.com/shuoros/JFiler/blob/main/CONTRIBUTE.md).

## Releases

To see the changes in different versions of JFiler, you can read
the [release notes](https://github.com/shuoros/JFiler/blob/main/RELEASENOTES.md).

## Issues

If you encounter a bug or vulnerability, please read
the [issue policy](https://github.com/shuoros/JFiler/blob/main/ISSUES.md).

## Documentation

To learn how to work with JFiler, please take a look at the [/doc](https://github.com/shuoros/JFiler/tree/main/doc)
folder.

## Acknowledgement

A great thanks to [@sarahrajabi](https://github.com/sarahrajabi) for designing the logo.
