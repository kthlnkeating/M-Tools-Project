package org.mumps.pathstructure.generic;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class PathFileSearchVisitor extends SimpleFileVisitor<Path> {

	private Path result = null;
	private String searchFor;

	public Path getResult() {
		return result;
	}

	public PathFileSearchVisitor(String searchFor) {
		this.searchFor = searchFor;
	}

	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {
      System.out.println(file);
      System.out.println("root:" +file.endsWith(searchFor));
      if (file.endsWith(searchFor)) {
    	  result = file;
    	  return FileVisitResult.TERMINATE;
      } else
    	  return FileVisitResult.CONTINUE;
    }
}
