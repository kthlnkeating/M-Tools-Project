package org.mumps.meditor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class MEditorUtils {

	private static final String SEP = FileSystems.getDefault().getSeparator();
	
	public static String readFile(IFile routineFile) throws CoreException, IOException {
		//sync file
		//compare it, if equal do nothing and just show a warning
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(routineFile.getContents()));
		} catch (ResourceException e) {
			if (e.getMessage().startsWith("Resource is out of sync with the file system:")) {
				routineFile.refreshLocal(IResource.DEPTH_ZERO, null);
				reader = new BufferedReader(new InputStreamReader(routineFile.getContents()));
			} else
				throw e;
		}
		char[] buffer = new char[512];
		StringBuilder sb = new StringBuilder();
		int read;

		while ((read = reader.read(buffer)) != -1) {
			sb.append(buffer, 0, read);
			buffer = new char[512];
		}

		reader.close();

		return sb.toString();
	}
	
	/*
	 *   cleanSource - remove spaces and/or tabs at end of line, 
	 *                 and all other control characters
	 *                 
	 *   @param  contents   the input string
	 *   
	 */
	public static String cleanSource(String contents) {
		String result = "";
		String str = "";
		while (!contents.equals("")) {
			int fromIndex1 = contents.indexOf('\n');
			if ( (fromIndex1 == -1))
				fromIndex1 = contents.length()-1; //JLI 101102 fix string index out of range
			if (fromIndex1 > -1) {
				str = contents.substring(0,fromIndex1+1);
				if (contents.length() > fromIndex1)
					contents = contents.substring(fromIndex1+1);
				else
					contents = "";

				// trim white space from end of lines
				while (str.contains(" \r\n")) {
					str = str.replaceAll(" \r\n","\r\n");
				}
				while (str.contains("\t\r\n")) {
					str = str.replaceAll("\t\r\n","\r\n");
				}
				// check and remove control characters other than tab
				if (str.length() > 0) {
					int counter = 0;
					while (counter < str.length()) {
						if (!(str.charAt(counter) == '\t')) {
							if (str.charAt(counter) < ' ') {
								str = str.substring(0,counter) + str.substring(counter+1);
								counter--;

							}
						}
						counter++;
					}
				}
				// skip blank lines
				if (! str.equals("")) {
					result = result + str + "\r\n";
				}
			}
		}
		return result;
	}
	
	
	/**
	 * Save the backup file. Create it if one doesn't exist. Also change the
	 * filename to end on todays date in the case a file already exists.
	 * 
	 * @param projectName
	 * @param routineName
	 * @param serverCode
	 * @throws CoreException
	 */
	public static void syncBackup(String projectName, String routineName,
			String serverCode) throws CoreException {
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IFolder backupFolder = getBackupFolder(projectName);
		if (!backupFolder.exists())
			backupFolder.create(true, true, null);
		
		//Search for the backup file if it exists:
		IFile backupFile = getBackupFile(backupFolder, routineName);
		String today = new SimpleDateFormat("yyMMdd").format(new Date());
		
		if (backupFile == null)		
			backupFile = iProject.getFile(
						"backups" +SEP+routineName+" "+today+ ".m");
		else
			backupFile = updateBackupFileName(routineName, backupFolder, backupFile, today);

		//note: backup routines should be in a static folder, not configured else that will move them around dynamically and cause bugs
		try {
			createOrReplace(backupFile, serverCode);
		} catch (UnsupportedEncodingException | CoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the latest backup file contents. Does not change the filename
	 * @param projectName
	 * @param routineName
	 * @throws IOException 
	 * @throws CoreException 
	 */
	public static String getBackupFileContents(String projectName, String routineName) throws CoreException, IOException {
		IFolder backupFolder = getBackupFolder(projectName);
		
		IFile backupFile = getBackupFile(backupFolder, routineName);
		if (backupFile == null) {
			throw new FileNotFoundException("Cannot find backup file for: " +routineName);
		}
		return readFile(backupFile);
	}

	private static IFolder getBackupFolder(String projectName)
			throws CoreException {
		IFolder backupFolder = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName).getFolder("backups");
		
		if (!backupFolder.exists())
			backupFolder.create(true, true, null);
		return backupFolder;
	}

	private static IFile updateBackupFileName(String routineName,
			IFolder backupFolder, IFile backupFile, String today)
			throws CoreException {

			Pattern p = Pattern.compile("[%A-Z][A-Z0-9]* (\\d{6})\\.m");
			Matcher m = p.matcher(backupFile.getName());
			m.find();
			if (!m.group(1).equals(today)) {
				//rename the backup file
				//IFile deleteMe = getBackupFile(backupFolder, routineName);
				IFile newFileLoc = backupFolder.getFile(routineName+" "+today+".m");
				backupFile.move(newFileLoc.getFullPath(), true, null); //this does not actually mutate the IFile object
				
				//deleteMe.delete(true, null);
				return newFileLoc;
			} else
				return backupFile;
	}
	
	private static IFile getBackupFile(IFolder backupFolder, String routineName) {
		//loop through the backup directory searching for the file Name, return null if not found
		for (File file : new File(backupFolder.getLocationURI()).listFiles())
			if (file.getName().matches(routineName+" \\d{6}\\.m"))
				return backupFolder.getFile(file.getName());
		
		return null;
	}
	
	public static void createOrReplace(IFile routineFile, String serverCode)
			throws UnsupportedEncodingException, CoreException {
		
		InputStream stream = new ByteArrayInputStream(serverCode.getBytes("UTF-8"));
		if (!routineFile.exists())
			routineFile.create(stream, true, null);
		else
			routineFile.setContents(stream, true, true, null);
	}
	
	/**
	 * Compares 2 routines to see if they are equal. It normalizes line feeds so
	 * tha CRLF and LF won't produce differences. It also strips out special
	 * characters.
	 * 
	 * 
	 * @param routine1
	 * @param routine2
	 * @return
	 */
	public static boolean compareRoutines(String routine1, String routine2) {
		if (routine1.contains("\r\n"))
			routine1 = routine1.replaceAll("\r\n", "\n");
		if (routine2.contains("\r\n"))
			routine2 = routine2.replaceAll("\r\n", "\n");
		return cleanSource(routine1).equals(cleanSource(routine2));
	}
}
