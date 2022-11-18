package tech.anapad.modela.util.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

import static java.nio.file.Files.createSymbolicLink;
import static java.nio.file.Files.write;
import static java.nio.file.Paths.get;

/**
 * {@link FileUtil} contains utility functions for files.
 */
public final class FileUtil {

    /**
     * Calls {@link File#exists()} for the given <code>path</code>.
     *
     * @param path the path
     *
     * @return {@link File#exists()}
     */
    public static boolean exists(String path) {
        return new File(path).exists();
    }

    /**
     * Calls {@link File#mkdirs()} for the given <code>path</code>.
     *
     * @param path the path
     *
     * @return {@link File#mkdirs()}
     */
    public static boolean makePath(String path) {
        return new File(path).mkdirs();
    }

    /**
     * Calls {@link File#delete()} for the given <code>path</code>.
     *
     * @param path the path
     *
     * @return {@link File#delete()}
     */
    public static boolean removePath(String path) {
        return new File(path).delete();
    }

    /**
     * Calls {@link Files#writeString(Path, CharSequence, OpenOption...)}.
     *
     * @param path     the path
     * @param contents the {@link String} contents
     *
     * @throws IOException thrown for {@link IOException}s
     */
    public static void writeString(String path, String contents) throws IOException {
        Files.writeString(get(path), contents);
    }

    /**
     * Calls {@link Files#write(Path, byte[], OpenOption...)}.
     *
     * @param path     the path
     * @param contents the {@link String} contents
     *
     * @throws IOException thrown for {@link IOException}s
     */
    public static void writeBytes(String path, byte[] contents) throws IOException {
        write(get(path), contents);
    }

    /**
     * Calls {@link Files#createSymbolicLink(Path, Path, FileAttribute[])}.
     *
     * @param link   the link file path
     * @param target the target file path
     *
     * @throws IOException thrown for {@link IOException}s
     */
    public static void symlink(String link, String target) throws IOException {
        createSymbolicLink(get(link), get(target));
    }
}
