package com.gmail.jesper.sporron.FS4J.impl;

public class PreReadNIOFSFile extends NIOFSFile {
	private final byte[] bytes;

	public PreReadNIOFSFile(final byte[] readBytes) {
		super(null, false);
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
