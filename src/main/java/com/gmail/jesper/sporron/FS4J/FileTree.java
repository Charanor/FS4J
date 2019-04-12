package com.gmail.jesper.sporron.FS4J;

import java.util.Optional;

public interface FileTree {
	Optional<FileTree> set(String name, FileType type);

	Optional<FileTree> get(String name);

	String getName();

	FileType getType();
}
