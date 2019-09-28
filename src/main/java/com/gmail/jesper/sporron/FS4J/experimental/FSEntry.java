package com.gmail.jesper.sporron.FS4J.experimental;

import java.util.Optional;

public interface FSEntry {

	Optional<FSDirectory> asDirectory();

	boolean isDirectory();

	Optional<FSFile> asFile();

	boolean isFile();
}
