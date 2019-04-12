package com.gmail.jesper.sporron.FS4J;

import com.gmail.jesper.sporron.FS4J.impl.NIOFileSystem;

public class SimpleTest {

	public static void main(final String[] args) {
		final NIOFileSystem fs = new NIOFileSystem();
		fs.setWriteDirectory("test");

		fs.addToSearchPath("test", FileLocation.EXTERNAL);
		fs.addToSearchPath("test2", FileLocation.EXTERNAL);

		for (final String path : new String[] { "test.txt", "test2.txt" }) {
			final FSFile file = fs.open(path, FileAccessType.READ)
					.orElseThrow(NullPointerException::new);
			System.out.println(String.format("Contents of %s: %s", path, file.readString()));
		}

		for (final String path : new String[] { "created", "dir/../home/" }) {
			final boolean success = fs.createDirectory(FilePath.from(path));
			System.out.println(String.format("Creating dir %s: %b", path, success));

			for (final String fp : new String[] { "newfile.txt" }) {
				fs.createFile(FilePath.from(fp)).ifPresent(f -> {
					System.out.println(String.format("\tCreated file %s", fp));
					f.writeString(fp);
					System.out.println(String.format("\tWrote %s", f.readString()));
				});
			}
		}
	}

}
