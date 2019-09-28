package com.gmail.jesper.sporron.FS4J.experimental;

import java.util.Optional;

import com.gmail.jesper.sporron.FS4J.util.FilePath;

public interface FileSystem {

	// Supports archives e.g. some/dir/archive.zip
	// Supports pathing through archives e.g. some/dir/archive.zip/testfile.txt
	Optional<FSEntry> getEntry(FilePath path);

	default Optional<FSDirectory> getDirectory(final FilePath path) {
		return getEntry(path).flatMap(FSEntry::asDirectory);
	}

	default Optional<FSFile> getFile(final FilePath path) {
		return getEntry(path).flatMap(FSEntry::asFile);
	}
}
