package com.gmail.jesper.sporron.FS4J;

import org.slf4j.Logger;

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
}
