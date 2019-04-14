package com.gmail.jesper.sporron.FS4J;

/** Class representing a file in the abstract file system. Writing to the file is prohibited unless
 * it is in the write directory.
 *
 * @author Jesper Sporron */
public abstract class FSFile {
	private final boolean isInWriteDirectory;

	public FSFile(final boolean isInWriteDirectory) {
		this.isInWriteDirectory = isInWriteDirectory;
	}

	/** Reads the content of the file as bytes. It is guaranteed that two calls to this method will
	 * return different array instances.
	 *
	 * @return the read bytes */
	public abstract byte[] readBytes();

	/** Writes the byte array to this file. It is guaranteed that calls to this method will not
	 * modify the input <code>bytes</code> array.
	 *
	 * @param bytes
	 *            the bytes to write
	 * @param append
	 *            if we should append the bytes
	 * @return true if this file is in the write directory and all bytes were written, false
	 *         otherwise */
	public abstract boolean writeBytes(byte[] bytes, boolean append);

	/** Writes a string to this file.
	 *
	 * @param stringToWrite
	 *            the string to write
	 * @param append
	 *            if we should append the string
	 * @return true if this file is in the write directory and the entire string was written, false
	 *         otherwise */
	public boolean writeString(final String stringToWrite, final boolean append) {
		return writeBytes(stringToWrite.getBytes(), append);
	}

	/** Reads this files byte contents as a string.
	 *
	 * @return the file contents */
	public String readString() {
		return new String(readBytes());
	}

	/** Checks if we can write to this file. It is recommended that child classes overwrites this
	 * method.
	 *
	 * @return true if we can write to this file, false otherwise. */
	public boolean isWriteable() {
		return isInWriteDirectory;
	}
}
