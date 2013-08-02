//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package com.pwc.us.rgi.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.pwc.us.rgi.struct.Filter;
import com.pwc.us.rgi.util.filevisitor.DirectoryFlattener;
import com.pwc.us.rgi.util.filevisitor.NameFilteredFileVisitor;

public class IOUtil {
	private static Path getDirectoryPath(String directory) throws IOException {
		try {
			Path path = Paths.get(directory);
			if (! Files.isDirectory(path)) throw new IOException(directory + " is not a directory");
			return path;
		} catch (InvalidPathException e) {
			throw new IOException(e);
		}		
	}
	
	public static void flattenDirectory(String rootDirectory, String destinationDirectory, Filter<Path> pathFilter) throws IOException {
		try {
			Path rootPath = IOUtil.getDirectoryPath(rootDirectory);
			Path destinationPath = IOUtil.getDirectoryPath(destinationDirectory);
			DirectoryFlattener df = new DirectoryFlattener(destinationPath, pathFilter);
			Files.walkFileTree(rootPath, df);
		} catch (InvalidPathException e) {
			throw new IOException(e);
		}
	}
	
	public static List<Path> getFiles(String rootDirectory, Filter<String> nameFilter) throws IOException {
		NameFilteredFileVisitor visitor = new NameFilteredFileVisitor(nameFilter);
		Path path = Paths.get(rootDirectory);
		Files.walkFileTree(path, visitor);
		return visitor.getFiles();
	}
	
	public static List<Path> getFiles(List<Path> paths, Filter<String> nameFilter) throws IOException {
		NameFilteredFileVisitor visitor = new NameFilteredFileVisitor(nameFilter);
		for (Path path : paths) {
			Files.walkFileTree(path, visitor);
		}
		return visitor.getFiles();
	}
}
