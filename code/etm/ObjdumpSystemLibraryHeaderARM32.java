import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class ObjdumpSystemLibraryHeaderARM32 {

	private static String ObjdumpBin = "/home/****/Android/android-ndk-r17b/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-objdump";	
	private static String SystemLibraryDirectory = "/media/****/D313-DBB3/android/system/lib/"; // <- adjust this constant
	private static String VendorLibraryDirectory = "/media/****/D313-DBB3/android/vendor/lib/"; // <- adjust this constant
	
	private static String OutputDirectory = "PreprocessScripts/";
	
	private static ArrayList<File> libraries = new ArrayList<File>();
	
	private void retrieveLibrary(String path) throws IOException {
		File rootDirectory = new File(path);
		if (!rootDirectory.isDirectory() || !rootDirectory.exists()) {
			System.out.println("Root directory " + path + " is invalid, exit ...");
			System.exit(0);
		}
		
		SimpleFileVisitor<Path> finder = new SimpleFileVisitor<Path>() {
		    @Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		    	libraries.add(file.toFile());
		        return super.visitFile(file, attrs);
		    }
		};
		
		Files.walkFileTree(rootDirectory.toPath(), finder);
	}
	
	private void writeObjdumpHeader() throws IOException {
		File script = new File(OutputDirectory + "objdump_header_arm.sh");
		if (script.exists())
			script.delete();
		script.createNewFile();
		
		FileWriter fw = new FileWriter(script);
		
		fw.write("#!/bin/sh" + "\n");
		fw.write("\n");
		
		fw.write("if [ ! -d \"" + Configuration.Workspace + Configuration.ArmSystemLibHeaderDirectory + "\" ]; then" + "\n");
		fw.write("\t" + "mkdir " + Configuration.Workspace + Configuration.ArmSystemLibHeaderDirectory + "\n");
		fw.write("fi" + "\n");
		fw.write("\n");
		
		fw.write("OBJDUMP=" + ObjdumpBin + "\n");
		fw.write("\n");
		
		for (File library : libraries) {
			String libraryPath = library.getAbsolutePath();
			if (!libraryPath.endsWith(".so"))
				continue;
			
			String librarySubPath = libraryPath;
			if (libraryPath.contains(SystemLibraryDirectory))
				librarySubPath = "system_lib_" + libraryPath.replace(SystemLibraryDirectory, "");
			if (libraryPath.contains(VendorLibraryDirectory))
				librarySubPath = "vendor_lib_" + libraryPath.replace(VendorLibraryDirectory, "");
			assert librarySubPath != libraryPath;
			
			String objdumpHeaderPath = Configuration.Workspace + Configuration.ArmSystemLibHeaderDirectory + librarySubPath.replace("/", "_").replace(".so", ".txt");
			
			fw.write("echo " + libraryPath + "\n");
			fw.write("$OBJDUMP" + " -h " + libraryPath + " > " + objdumpHeaderPath + "\n");
		}
		
		fw.flush();
		fw.close();
	}
	
	public static void main(String[] args) {
		ObjdumpSystemLibraryHeaderARM32 generator = new ObjdumpSystemLibraryHeaderARM32();
		
		try {
			generator.retrieveLibrary(SystemLibraryDirectory);
			generator.retrieveLibrary(VendorLibraryDirectory);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		try {
			generator.writeObjdumpHeader();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
}
