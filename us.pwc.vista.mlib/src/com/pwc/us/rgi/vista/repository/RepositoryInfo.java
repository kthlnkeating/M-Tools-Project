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

package com.pwc.us.rgi.vista.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.EntryIdWithSource;
import com.pwc.us.rgi.m.parsetree.data.EntryId.StringFormat;
import com.pwc.us.rgi.m.parsetree.visitor.EntryRefVisitor;
import com.pwc.us.rgi.m.token.MLine;
import com.pwc.us.rgi.m.token.MObjectSupply;
import com.pwc.us.rgi.m.token.MTFSupply;
import com.pwc.us.rgi.m.token.MToken;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.parser.SyntaxErrorException;
import com.pwc.us.rgi.parser.Text;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.ObjectSupply;
import com.pwc.us.rgi.parsergen.ParseException;
import com.pwc.us.rgi.stringlib.DigitFilter;
import com.pwc.us.rgi.struct.Filter;
import com.pwc.us.rgi.struct.Transformer;
import com.pwc.us.rgi.vista.tools.MRALogger;

public class RepositoryInfo {
	private static class OptionFilter implements Filter<MGlobalNode> {
		private DigitFilter digitFilter = new DigitFilter();
		
		@Override
		public boolean isValid(MGlobalNode input) {
			if (this.digitFilter.isValid(input.getName())) {			
				MGlobalNode node0 = input.getNode("0");
				if (node0 != null) {
					String type = node0.getValuePiece(3);
					if ((type != null) && (type.equals("R"))) {
						MGlobalNode node25 = input.getNode("25");
						return node25 != null;
					}				
				}
			}
			return false;
		}
	}
		
	private static class OptionTransformer implements Transformer<MGlobalNode, EntryIdWithSource> {
		@Override 
		public EntryIdWithSource transform(MGlobalNode node) {
			MGlobalNode node25 = node.getNode("25");
			String value = node25.getValue().split("\\(")[0];
			EntryId entryId = EntryId.getInstance(value, StringFormat.SF_SINGLE_ROUTINE);
			if ((entryId != null) && (entryId.getRoutineName() != null)) {
				MGlobalNode node0 = node.getNode("0");
				String source = node0.getValuePiece(0);
				return new EntryIdWithSource(entryId, source);
			}
			return null;
		}			
	}
	
	private static class ProtocolFilter implements Filter<MGlobalNode> {
		private DigitFilter digitFilter = new DigitFilter();
		private String protocolType;
		
		public ProtocolFilter(String protocolType) {
			this.protocolType = protocolType;
		}
		
		@Override
		public boolean isValid(MGlobalNode input) {
			
			if (this.digitFilter.isValid(input.getName())) {
				MGlobalNode node0 = input.getNode("0");
				if (node0 != null) {
					String type = node0.getValuePiece(3);
					if ((type != null) && (protocolType == null || type.equals(protocolType))) {
						return input.getNode("20") != null || input.getNode("26") != null;
					}
				}
			}
			return false;
		}
		
	}
	

	private static class ProtocolTransformer implements Transformer<MGlobalNode, List<EntryIdWithSource>> {
		
		private ObjectSupply<MToken> mAdapterSupply = new MObjectSupply();
		private TokenFactory<MToken> tfLine;
		
		public ProtocolTransformer() {
			super();
			
			try {
				tfLine = MTFSupply.getInstance(MVersion.CACHE).line; //TODO: should be set from cli args
			} catch (ParseException e) {
				e.printStackTrace();
			} 
		}
		
		@Override 
		public List<EntryIdWithSource> transform(MGlobalNode inputNode) {
			List<EntryIdWithSource> results = new ArrayList<EntryIdWithSource>();
			
			MGlobalNode node = inputNode.getNode("20") != null
					&& !inputNode.getNode("20").getValue().isEmpty() ? inputNode
					.getNode("20") : inputNode.getNode("26");

			if (node == null || node.getValue().isEmpty())
				return results;
					
					
			//parse out D subroutine calls
			String line = node.getValue();
			if (line.charAt(0) != ' ')
				line = " " +line; //parser is expecting a complete, valid M line
			Text text = new Text(line);
			MLine mline = null;
			try {
				mline = (MLine) tfLine.tokenize(text, this.mAdapterSupply);
			} catch (SyntaxErrorException e) {
				e.printStackTrace();
			}
			
			EntryRefVisitor erv = new EntryRefVisitor();
			mline.getNode().accept(erv);
			
			String source = inputNode.getNode("0").getValuePiece(0);
			for (EntryId entryId : erv.getEntryTags()) {
				results.add(new EntryIdWithSource(entryId, source));
			}
			
			return results;
		}			
	}
	
	private static class RPCFilter implements Filter<MGlobalNode> {
		private DigitFilter digitFilter = new DigitFilter();
		
		@Override
		public boolean isValid(MGlobalNode input) {
			if (this.digitFilter.isValid(input.getName())) {			
				MGlobalNode node0 = input.getNode("0");
				if (node0 != null) {
					String routineName = node0.getValuePiece(2);
					return (routineName != null) && (! routineName.isEmpty());
				}
			}
			return false;
		}
	}

	private static class RPCTransformer implements Transformer<MGlobalNode, EntryIdWithSource> {
		@Override 
		public EntryIdWithSource transform(MGlobalNode node) {
			MGlobalNode node0 = node.getNode("0");
			String name = node0.getValuePiece(0);
			String tag = node0.getValuePiece(1);
			String routineName = node0.getValuePiece(2);
			if ((tag != null) && tag.isEmpty()) {
				tag = null;
			}
			EntryId entryId = new EntryId(routineName, tag);
			return new EntryIdWithSource(entryId, name);
		}			
	}
	
	private static class PackagePrefixComparator implements Comparator<VistaPackage> {
		public int compare(VistaPackage pkg0, VistaPackage pkg1) {
			String prefix0 = pkg0.getPrimaryPrefix();
			String prefix1 = pkg1.getPrimaryPrefix();
			return prefix0.compareTo(prefix1);
		}
	}
	
	private List<VistaPackage> packages = new ArrayList<VistaPackage>();
	private Map<String, VistaPackage> packagesByName = new HashMap<String, VistaPackage>();
	private TreeMap<String, VistaPackage> packagesByPrefix = new TreeMap<String, VistaPackage>();
	private Map<String, String> packagesByGlobal;
	private Map<String, String> fileByGlobal;
	
	private RepositoryInfo() {
	}
	
	private void addPackage(VistaPackage packageInfo) {
		this.packages.add(packageInfo);
		this.packagesByName.put(packageInfo.getPackageName(), packageInfo);
		for (String prefix : packageInfo.getPrefixes()) {
			this.packagesByPrefix.put(prefix, packageInfo);
		}
	}
	
	public static String getLocation() {
		return System.getenv("VistA-FOIA");
	}
	
	public static String getLocationWithLog() {
		String root = RepositoryInfo.getLocation();
		if ((root == null) || (root.isEmpty())) {
			MRALogger.logError("Root directory for VistA-FOIA needs to be specified using environment variable VistA-FOIA");
			return null;
		}
		return root;
	}
	
	public VistaPackage getPackage(String packageName) {
		return this.packagesByName.get(packageName);
	}
	
	public List<VistaPackage> getPackages(List<String> packageNames) {
		List<VistaPackage> result = new ArrayList<VistaPackage>(packageNames.size());
		for (String name : packageNames) {
			VistaPackage p = this.getPackage(name);
			if (p == null) {
				p = this.getPackageFromPrefix(name);
			}
			if (p != null) {
				result.add(p);
			}
		}
		Collections.sort(result, new PackagePrefixComparator());
		return result;
	}
	
	public List<VistaPackage> getAllPackages(List<String> packageExceptions) {
		List<VistaPackage> result = null;
		if ((packageExceptions == null) || (packageExceptions.size() == 0)) {
			result = new ArrayList<VistaPackage>(this.packages);
		} else {
			result = new ArrayList<VistaPackage>(this.packages.size());
			for (VistaPackage vp : this.packages) {
				if (packageExceptions.contains(vp.getPackageName())) continue;
				if (packageExceptions.contains(vp.getDefaultPrefix())) continue;
				result.add(vp);
			}			
		}
		Collections.sort(result, new PackagePrefixComparator());
		return result;
	}
	
	public List<VistaPackage> getAllPackages() {
		return this.getAllPackages(null);
	}
	
	public Path getPackagePath(String packageName) {
		String vistaFOIARoot = RepositoryInfo.getLocation();
		if (vistaFOIARoot == null) {
			vistaFOIARoot = "";
		}
		VistaPackage pi = this.getPackage(packageName);
		String dir = pi.getDirectoryName();
		Path path = Paths.get(vistaFOIARoot, "Packages", dir);
		return path;
	}
	
	public VistaPackage getPackageFromPrefix(String prefix) {
		return this.packagesByPrefix.get(prefix);
	}
	
	public String getPackageFromGlobal(String global) {
		if (this.packagesByGlobal == null) {
			return null;
		} else {
			return this.packagesByGlobal.get(global);
		}
	}
	
	public String getFileNameFromGlobal(String global) {
		if (this.fileByGlobal == null) {
			return null;
		} else {
			return this.fileByGlobal.get(global);
		}
	}
	
	public VistaPackage getPackageFromRoutineName(String routineName) {
		if (routineName.charAt(0) == '%') {
			return this.packagesByPrefix.get("XU");
		}		
		VistaPackage result = null;
		int n = java.lang.Math.min(routineName.length(), 4);
		for (int i=1; i<=n; ++i) {
			String prefix = routineName.substring(0, i);
			VistaPackage pi = this.packagesByPrefix.get(prefix);
			if (pi != null) {
				result = pi;
			}
		}
		if (result == null) {
			result = this.packagesByName.get("UNCATEGORIZED");
		}
		return result;
	}
	
	public List<EntryIdWithSource> getOptionEntryPoints() {
		String root = RepositoryInfo.getLocation();
		Path path = Paths.get(root, "Packages", "Kernel", "Globals", "19+OPTION.zwr");
		MGlobalNode rootNode = new MGlobalNode();
		rootNode.read(path);
		MGlobalNode node = rootNode.getNode("DIC","19");
		List<EntryIdWithSource> result = node.getValues(new OptionFilter(), new OptionTransformer());
		return result;
	}
	
	public List<List<EntryIdWithSource>> getProtocolEntryPoints(String protocolType) {
		String root = RepositoryInfo.getLocation();
		Path path = Paths.get(root, "Packages", "Kernel", "Globals", "101+PROTOCOL.zwr");
		MGlobalNode rootNode = new MGlobalNode();
		rootNode.read(path);
		MGlobalNode node = rootNode.getNode("ORD","101");
		List<List<EntryIdWithSource>> result = node.getValues(new ProtocolFilter(protocolType), new ProtocolTransformer());
		return result;
	}
	
	public List<EntryIdWithSource> getRPCs() {
		String root = RepositoryInfo.getLocation();
		Path path = Paths.get(root, "Packages", "RPC Broker", "Globals", "8994+REMOTE PROCEDURE.zwr");
		MGlobalNode rootNode = new MGlobalNode();
		rootNode.read(path);
		MGlobalNode node = rootNode.getNode("XWB","8994");
		List<EntryIdWithSource> result = node.getValues(new RPCFilter(), new RPCTransformer());
		return result;
	}

	public void addMDirectories(List<String> paths) {
		String lastPath = null;
		try {
			for (String path : paths) {
				lastPath = path;
				FileSupply fs = new FileSupply();
				fs.addPath(path);
				List<Path> files = fs.getMFiles();
				for (Path p : files) {
					String name = p.getFileName().toString().split("\\.m")[0];
					VistaPackage pkg = this.getPackageFromRoutineName(name);
					pkg.addAdditionalPath(p);
				}
			}
		} catch (IOException e) {
			MRALogger.logError("Error reading directory " + lastPath);
		}
	}
	
	public void addMFiles(List<String> filePaths) {
		for (String filePath : filePaths) {
			File file = new File(filePath);
			if (file.isFile()) {
				String name = filePath.split("\\.m")[0];
				VistaPackage pkg = this.getPackageFromRoutineName(name);
				pkg.addAdditionalFile(Paths.get(filePath));					
			} else {
				MRALogger.logError("Error reading file " + filePath);					
			}
		}
	}
	
	public void readGlobalOwnership(String filePath) {
		try {
			Path path = Paths.get(filePath);
			Scanner scanner = new Scanner(path);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				line = line.replace(",\"^",",^");
				line = line.replace("\"\"", "\"");
				line = line.replace(",\"", "");
				line = line.replace("(\",", "(,");				
				String[] pieces = line.split(",");
				if (pieces.length >= 5) {
					String global = pieces[2].trim();
					String packageName = pieces[4];
					if (this.packagesByGlobal == null) {
						this.packagesByGlobal = new HashMap<String, String>();
						this.fileByGlobal = new HashMap<String, String>();
					}
					this.packagesByGlobal.put(global, packageName);
					this.fileByGlobal.put(global, pieces[1]);
					String globalName = global.split("\\(")[0] + "(";
					if (! globalName.equals(global)) {
						this.packagesByGlobal.put(globalName, packageName);
						this.fileByGlobal.put(globalName, pieces[1]);						
					}
					
				}
			}
			scanner.close();
		} catch (IOException e) {
			MRALogger.logError("Unable to read file " + filePath, e);
		}
	}

	public static RepositoryInfo getInstance(Scanner scanner, RoutineFactory rf) throws IOException {
		RepositoryInfo r = new RepositoryInfo();
		VistaPackage packageInfo = null;
		scanner.nextLine();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] pieces = line.split(",");
			int n = pieces.length;
			if (pieces[0].length() > 0) {
				packageInfo = new VistaPackage(pieces[0], pieces[1], rf);
				r.addPackage(packageInfo);
			}
			if ((n > 2) && (pieces[2].length() > 0)) {
				packageInfo.addPrefix(pieces[2]);
				if (pieces[2].charAt(0) != '!') {
					r.packagesByPrefix.put(pieces[2], packageInfo);				
				}
			}
			if ((n > 3) && (pieces[3].length() > 0)) {
				packageInfo.addFile(pieces[3], pieces[4]);
			}
		}
		packageInfo = new VistaPackage("UNCATEGORIZED", "Uncategorized", rf);
		r.addPackage(packageInfo);
		r.packagesByPrefix.put("UNCATEGORIZED", packageInfo);				
		packageInfo = r.getPackage("KERNEL");
		packageInfo.addPrefix("%");
		r.packagesByPrefix.put("%", packageInfo);				
		return r;		
	}
	
	public static RepositoryInfo getInstance(String root, RoutineFactory rf) throws IOException {
		Path path = Paths.get(root, "Packages.csv");
		Scanner scanner = new Scanner(path);
		RepositoryInfo r = getInstance(scanner, rf);
		scanner.close();
		
		return r;
	}

	public static RepositoryInfo getInstance(RoutineFactory rf) {
		String root = RepositoryInfo.getLocation();
		if ((root == null) || (root.isEmpty())) {
			MRALogger.logError("Root directory for VistA-FOIA needs to be specified using environment variable VistA-FOIA");
			return null;
		}
		try {
			return getInstance(root, rf);
		} catch (IOException e) {
			MRALogger.logError("Packages.csv is not found in directory " + root);
			return null;
		}
	}


}
