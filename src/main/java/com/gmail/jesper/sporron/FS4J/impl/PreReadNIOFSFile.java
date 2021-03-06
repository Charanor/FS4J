package com.gmail.jesper.sporron.FS4J.impl;

import static java.util.Objects.requireNonNull;

public class PreReadNIOFSFile extends NIOFSFile {
	private final byte[] bytes;

	public PreReadNIOFSFile(final byte[] readBytes) {
		super(null, false);
		requireNonNull(readBytes, "readBytes must not be null (but can be empty)");
		this.bytes = readBytes;
	}

	@Override
	public byte[] readBytes() {
		if (bytes == null) return new byte[0];
		final byte[] newBytes = new byte[bytes.length];
		System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
		return newBytes;
	}

	@Override
	public boolean writeBytes(final byte[] bytes, final boolean append) {
		return false;
	}

	@Override
	public boolean isWriteable() {
		return false;
	}
}
