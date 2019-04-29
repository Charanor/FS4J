package com.gmail.jesper.sporron.FS4J.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Objects;

import org.slf4j.Logger;

import com.gmail.jesper.sporron.FS4J.FileLocation;
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

	public static Path constructNIOPath(final FilePath path, final FileLocation location)
			throws NullPointerException, URISyntaxException {
		Objects.requireNonNull(path, "path must not be null");
		Objects.requireNonNull(location, "location must not be null");
		String strPath = path.toString();

		switch (location) {
		case EXTERNAL: {
			final Path nioPath = Paths.get(strPath);
			return nioPath;
		}
		case INTERNAL: {
			if (!strPath.startsWith("/")) strPath = "/" + strPath;
			final URL resource = FSUtils.class.getClassLoader().getResource(strPath);
			Objects.requireNonNull(resource, String.format("Could not find resource %s", strPath));

			try {
				final URI uri = resource.toURI();
				final Path nioPath = Paths.get(uri);
				return nioPath;
			} catch (final URISyntaxException e) {
				throw e;
			}
		}
		default:
			throw new UnsupportedOperationException(
					String.format("Can't construct path for location '%s'", location));
		}
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
