package com.gmail.jesper.sporron.FS4J.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.jesper.sporron.FS4J.FileAccessType;
import com.gmail.jesper.sporron.FS4J.FileLocation;
import com.gmail.jesper.sporron.FS4J.FileSystem;
import com.gmail.jesper.sporron.FS4J.FileType;
import com.gmail.jesper.sporron.FS4J.util.FSUtils;
import com.gmail.jesper.sporron.FS4J.util.FilePath;

public class NIOFileSystem implements FileSystem<NIOFSFile> {
	private final Logger LOGGER = LoggerFactory.getLogger(NIOFileSystem.class);

	private final List<NIOFSRegistration> registrations;
	private FilePath writePath;

	public NIOFileSystem() {
		this.registrations = new ArrayList<>();
		this.writePath = null;
	}

	@Override
	public boolean addToSearchPath(final FilePath path, final FileLocation location) {
		final FilePath minimized = path.minimize();
		if (!verifyFilePathAndLog(minimized)) return false;

		try {
			LOGGER.debug("Attempting to add '{}' ({}) to the search path.", path, location);
			final NIOFSRegistration reg = new NIOFSRegistration(minimized, location);
			if (registrations.contains(reg)) return false;
			registrations.add(reg);
			LOGGER.info("'{}' ({}) added to search path", path, location);
			return true;
		} catch (final URISyntaxException e) {
			LOGGER.error("Could not add '{}' ({}) to the search path: {}", path.toString(),
					location, e.toString());
			return false;
		}
	}

	@Override
	public boolean isOnSearchPath(final FilePath path) {
		final FilePath minimized = path.minimize();
		if (!verifyFilePathAndLog(minimized)) return false;
		return registrations.stream().anyMatch(reg -> reg.getFilePath().equals(minimized));
	}

	@Override
	public boolean addAllArchivesToSearchPath(final FilePath path, final FileLocation location) {
		final FilePath minimized = path.minimize();
		if (!verifyFilePathAndLog(minimized)) return false;

		try {
			LOGGER.debug("Adding all archives in path '{}' ({})", path, location);
			boolean success = true;
			final Path filePath = new NIOFSRegistration(path, location).getPath();
			final Iterator<Path> it = Files.walk(filePath, 1).iterator();

			// The first element in the iterator is the input path
			// and we don't want to add that.
			it.next();
			while (it.hasNext()) {
				final Path p = it.next();
				final FilePath fp = FilePath.from(p.toString());
				final FileType fileType = FSUtils.getFileType(p);
				if (fileType != FileType.ARCHIVE) {
					LOGGER.trace("Not adding child '{}' because it is a {}", fp, fileType);
					continue;
				}

				// If we couldn't add the archive to the search path we have
				// to return false according to the spec.
				if (!addToSearchPath(fp, location)) success = false;
			}

			return success;
		} catch (final URISyntaxException e) {
			LOGGER.error("Could not create java.nio.file.Path path to '{}': {}", path, e);
			return false;
		} catch (final IOException e) {
			LOGGER.error("Could not walk file '{}': {}", path, e);
			return false;
		}
	}

	@Override
	public boolean setWriteDirectory(final FilePath path) {
		this.writePath = path;
		return true;
	}

	@Override
	public Optional<FilePath> getWriteDirectory() {
		return Optional.ofNullable(writePath);
	}

	@Override
	public Optional<NIOFSFile> open(final FilePath path, final FileAccessType accessType) {
		final FilePath minimized = path.minimize();
		if (!verifyFilePathAndLog(minimized)) return Optional.empty();

		try {
			LOGGER.debug("Attempting to open file '{}' ({})", path, accessType);
			switch (accessType) {
			case READ:
				LOGGER.trace("Number of registered input directories: {}", registrations.size());
				for (final NIOFSRegistration reg : registrations) {
					LOGGER.trace("Looking for file '{}' in '{}'", path, reg.getFilePath());
					if (reg.getType() == FileType.ARCHIVE) {
						final Path zipFile = reg.getPath();
						if (!Files.exists(zipFile)) continue;

						try (java.nio.file.FileSystem fs = FileSystems.newFileSystem(zipFile,
								null)) {
							final Path filePath = fs.getPath(path.toString());
							if (!Files.exists(filePath)) continue;
							LOGGER.trace("Found file '{}' in '{}'", path, reg.getFilePath());
							return Optional.of(new PreReadNIOFSFile(Files.readAllBytes(filePath)));
						} catch (final IOException e) {
							LOGGER.error("Could not fetch file '{}' from archive '{}': {}", path,
									zipFile, e);
							return Optional.empty();
						}
					}

					final Path nioPath = reg.getPath(minimized);
					if (!Files.exists(nioPath)) continue;
					LOGGER.trace("Found file '{}' in '{}'", path, nioPath);
					return Optional.of(new NIOFSFile(nioPath, false));
				}
				LOGGER.debug("Could not find file '{}'", path);
				return Optional.empty();
			case WRITE:
				final FilePath fqPath = writePath.append(minimized);
				final Path nioPath = new NIOFSRegistration(fqPath, FileLocation.EXTERNAL).getPath();
				LOGGER.trace("Looking for file '{}' in '{}' ({})", path, writePath,
						nioPath.toAbsolutePath());
				return Optional.of(new NIOFSFile(nioPath, false));
			default:
				throw new IllegalArgumentException("Cannot handle accessType " + accessType);
			}
		} catch (final URISyntaxException e) {
			LOGGER.error("Could not create directory '{}': {}", path, e);
			return Optional.empty();
		}
	}

	@Override
	public boolean createDirectory(final FilePath path) {
		final FilePath minimized = path.minimize();
		if (!verifyFilePathAndLog(minimized)) return false;

		try {
			final FilePath fqPath = writePath.append(minimized);
			final Path nioPath = new NIOFSRegistration(fqPath, FileLocation.EXTERNAL).getPath();
			final Path createdPath = Files.createDirectories(nioPath);
			LOGGER.debug("Created directory at '{}'", createdPath.toString());
			return true;
		} catch (final IOException | URISyntaxException e) {
			LOGGER.error("Could not create directory '{}': {}", path, e);
			return false;
		}
	}

	@Override
	public Optional<NIOFSFile> createFile(final FilePath path) {
		final FilePath minimized = path.minimize();
		if (!verifyFilePathAndLog(minimized)) return Optional.empty();

		try {
			final FilePath fqPath = writePath.append(minimized);
			final Path nioPath = new NIOFSRegistration(fqPath, FileLocation.EXTERNAL).getPath();
			LOGGER.debug("Trying to create file at '{}'", nioPath.toAbsolutePath());
			final Path createdFilePath = Files.createFile(nioPath);
			LOGGER.debug("Created file at '{}'", createdFilePath);
			return Optional.of(new NIOFSFile(createdFilePath, true));
		} catch (final FileAlreadyExistsException e) {
			LOGGER.warn("Could not create file '{}' because it already exists", path);
			return open(minimized, FileAccessType.WRITE);
		} catch (URISyntaxException | IOException e) {
			LOGGER.error("Could not create file '{}': {}", path, e);
			return Optional.empty();
		}
	}

	private boolean verifyFilePathAndLog(final FilePath path) {
		return FSUtils.isSafePath(path, LOGGER);
	}
}
