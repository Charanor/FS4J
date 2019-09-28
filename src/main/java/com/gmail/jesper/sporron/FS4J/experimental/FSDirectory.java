package com.gmail.jesper.sporron.FS4J.experimental;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.gmail.jesper.sporron.FS4J.util.FilePath;

public interface FSDirectory {
	Optional<FSEntry> getEntry(FilePath relativePath);

	List<FSEntry> allEntries();

	// Default implementations for convenience sake

	default Optional<FSFile> getFile(final FilePath relativePath) {
		return getEntry(relativePath).flatMap(FSEntry::asFile);
	}

	default Optional<FSDirectory> getDirectory(final FilePath relativePath) {
		return getEntry(relativePath).flatMap(FSEntry::asDirectory);
	}

	default List<FSDirectory> allDirectories() {
		return allEntries().stream().map(FSEntry::asDirectory).filter(Optional::isPresent)
				.map(Optional::get).collect(Collectors.toList());
	}

	default List<FSFile> allFiles() {
		return allEntries().stream().map(FSEntry::asFile).filter(Optional::isPresent)
				.map(Optional::get).collect(Collectors.toList());
	}
}
