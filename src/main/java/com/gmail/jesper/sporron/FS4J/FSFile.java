package com.gmail.jesper.sporron.FS4J;

public abstract class FSFile {
	private final boolean isInWriteDirectory;

	public FSFile(final boolean isInWriteDirectory) {
		this.isInWriteDirectory = isInWriteDirectory;
	}

	public abstract byte[] readBytes();

	public abstract boolean writeBytes(byte[] bytes);

	public boolean writeString(final String stringToWrite) {
		return writeBytes(stringToWrite.getBytes());
	}

	public String readString() {
		return new String(readBytes());
	}

	public boolean isWriteable() {
		return isInWriteDirectory;
	}
}
