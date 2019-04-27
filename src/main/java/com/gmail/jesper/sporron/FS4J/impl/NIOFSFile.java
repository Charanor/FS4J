package com.gmail.jesper.sporron.FS4J.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.jesper.sporron.FS4J.FSFile;

public class NIOFSFile extends FSFile {
	private static final Logger LOGGER = LoggerFactory.getLogger(NIOFSFile.class);
	private final Path path;

	public NIOFSFile(final Path path, final boolean isInWritePath) {
		super(isInWritePath);
		this.path = path;
	}

	@Override
	public byte[] readBytes() {
		try {
			return Files.readAllBytes(path);
		} catch (final IOException e) {
			LOGGER.error("Failed to read bytes", e);
			return new byte[0];
		}
	}

	@Override
	public boolean writeBytes(final byte[] bytes, final boolean append) {
		Objects.requireNonNull(bytes, "bytes must not be null");
		try {
			if (!isWriteable()) return false;
			Files.write(path, bytes, append ? StandardOpenOption.APPEND : null);
			return true;
		} catch (final IOException e) {
			LOGGER.error("Failed to write bytes", e);
			return false;
		}
	}

	@Override
	public boolean isWriteable() {
		return super.isWriteable() && Files.isWritable(path);
	}
}
