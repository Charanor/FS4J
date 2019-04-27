package com.gmail.jesper.sporron.FS4J;

import com.gmail.jesper.sporron.FS4J.filetree.FileTree;
import com.gmail.jesper.sporron.FS4J.filetree.FileTreeSupplier;
import com.gmail.jesper.sporron.FS4J.filetree.Node;
import com.gmail.jesper.sporron.FS4J.filetree.impl.DefaultFileTreeSupplier;

public class SimpleTest {
	public static void main(final String[] args) {
		final FileTreeSupplier supplier = new DefaultFileTreeSupplier();
		final FileTree tree = supplier.get("test.zip");

		// Prints the name of all the nodes
		tree.walk(System.out::println);

		// Example for FileSystem#addAllArchivesToSearchPath
		for (final Node child : tree) {
			switch (child.getType()) {
			case ARCHIVE:
				break;
			case FILE:
			case DIRECTORY:
			default:
				break;
			}
		}
	}
}
