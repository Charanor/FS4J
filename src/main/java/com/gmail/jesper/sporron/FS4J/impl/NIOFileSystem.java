package com.gmail.jesper.sporron.FS4J.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.jesper.sporron.FS4J.FSUtils;
import com.gmail.jesper.sporron.FS4J.FileAccessType;
import com.gmail.jesper.sporron.FS4J.FileLocation;
import com.gmail.jesper.sporron.FS4J.FilePath;
import com.gmail.jesper.sporron.FS4J.FileSystem;

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
			final NIOFSRegistration reg = new NIOFSRegistration(minimized, location);
			if (registrations.contains(reg)) return false;
			registrations.add(reg);
			return true;
		} catch (final URISyntaxException e) {
			LOGGER.error("Could not add {} ({}) to the search path: {}", path.toString(), location,
					e.toString());
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
		return false;
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
			switch (accessType) {
			case READ:
				for (final NIOFSRegistration reg : registrations) {
					final Path nioPath = reg.getPath(minimized);
					if (!Files.exists(nioPath)) continue;
					return Optional.of(new NIOFSFile(nioPath, false));
				}
			case WRITE:
				final FilePath fqPath = writePath.append(minimized);
				final Path nioPath = new NIOFSRegistration(fqPath, FileLocation.EXTERNAL).getPath();
				return Optional.of(new NIOFSFile(nioPath, false));
			default:
				throw new IllegalArgumentException("Cannot handle accessType " + accessType);
			}
		} catch (final URISyntaxException e) {
			LOGGER.error("Could not create directory {}: {}", path, e);
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
			LOGGER.debug("Created directory at {}", createdPath.toString());
			return true;
		} catch (final IOException | URISyntaxException e) {
			LOGGER.error("Could not create directory {}: {}", path, e);
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
			final Path createdFilePath = Files.createFile(nioPath);
			return Optional.of(new NIOFSFile(createdFilePath, true));
		} catch (final FileAlreadyExistsException e) {
			LOGGER.warn("Could not create file {} because it already exists", path);
			return open(minimized, FileAccessType.WRITE);
		} catch (URISyntaxException | IOException e) {
			LOGGER.error("Could not create file {}: {}", path, e);
			return Optional.empty();
		}
	}

	private boolean verifyFilePathAndLog(final FilePath path) {
		return FSUtils.isSafePath(path, LOGGER);
	}
}
