package com.gmail.jesper.sporron.FS4J.util;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import org.slf4j.Logger;

import com.gmail.jesper.sporron.FS4J.FileType;

public final class FSUtils {
	private static final FileEntry[] INVALID_ENTRIES = FileEntry.from("..");

	public static boolean isSafePath(final FilePath path) {
		return isSafePath(path, null);
	}

	public static boolean isSafePath(final FilePath path, final Logger logger) {
		for (final FileEntry entry : INVALID_ENTRIES) {
			if (path.containsEntry(entry)) {
				if (logger != null) logger.warn("Invalid file entry {} in path {}", entry, path);
				return false;
			}
		}
		return true;
	}

	/** Tries to predict the file type from the {@link FilePath}. This method is not always 100%
	 * accurate. If you need an accurate file type use {@link FSUtils#getFileType(Path)}.
	 *
	 * @param path
	 * @return */
	public static FileType tryPredictFileType(final FilePath path) {
		if (path.toString().endsWith(".zip")) return FileType.ARCHIVE;
		if (path.toString().matches(".*[.].+$")) return FileType.FILE;
		return FileType.DIRECTORY;
	}

	public static FileType getFileType(final Path path) {
		if (Files.isDirectory(path)) return FileType.DIRECTORY;
		final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*/*.zip");
		if (matcher.matches(path)) return FileType.ARCHIVE;
		return FileType.FILE;
	}
}
