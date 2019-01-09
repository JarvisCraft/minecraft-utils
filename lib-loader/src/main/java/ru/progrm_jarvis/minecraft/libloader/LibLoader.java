package ru.progrm_jarvis.minecraft.libloader;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * A tiny loader of JAR dependencies to runtime.
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Accessors(chain = true)
public class LibLoader {

    private static final Logger DEFAULT_LOGGER = Logger.getLogger("LibLoader");

    @NonNull private static final MethodHandle URL_CLASS_LOADER__ADD_URL_METHOD;

    @NonNull @Getter private URLClassLoader classLoader;

    /**
     * Directory to store library artifacts and hashes in
     */
    @NonNull private final File rootDirectory;

    @NonNull @Getter @Setter private Logger log = DEFAULT_LOGGER;

    // creates a MethodHandle object for URLClassLoader#addUrl(URL) method
    static {
        final Method addUrlMethod;
        try {
            addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (final NoSuchMethodException e) {
            throw new ExceptionInInitializerError(
                    "Unable to initialize LibLoader as " + URLClassLoader.class.getCanonicalName()
                            + " is missing addURL(URL) method"
            );
        }

        addUrlMethod.setAccessible(true);
        try {
            URL_CLASS_LOADER__ADD_URL_METHOD = MethodHandles.lookup().unreflect(addUrlMethod);
        } catch (final IllegalAccessException e) {
            throw new ExceptionInInitializerError(
                    "Unable to initialize LibLoader as " + URLClassLoader.class.getCanonicalName()
                            + " cannot be unreflected to MethodHandle"
            );
        } finally {
            addUrlMethod.setAccessible(false);
        }
    }

    /**
     * Tries to find an available {@link URLClassLoader} un current context.
     *
     * @return optional containing found {@link URLClassLoader} or empty if none was found in current context
     *
     * @implNote this checks current thread's class loader and system class loader
     */
    public static Optional<URLClassLoader> getAvailableUrlClassLoader() {
        var classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader instanceof URLClassLoader) return Optional.of((URLClassLoader) classLoader);

        classLoader = ClassLoader.getSystemClassLoader();
        if (classLoader instanceof URLClassLoader) return Optional.of((URLClassLoader) classLoader);

        return Optional.empty();
    }

    /**
     * Constructs new lib loader. This performs type-check of {@code classLoader}
     * in order to guarantee that it is instance of {@link URLClassLoader}.
     *
     * @param classLoader class loader to use for loading of libraries,
     * should normally be {@link URLClassLoader}
     * @param rootDirectory directory to store library artifacts and hashes in
     *
     * @throws IllegalArgumentException if the {@code classLoader} is not {@link URLClassLoader}
     */
    public LibLoader(@NonNull final ClassLoader classLoader, @NonNull final File rootDirectory) {
        if (!(classLoader instanceof URLClassLoader)) throw new IllegalArgumentException(
                classLoader + " is not instance of URLClassLoader"
        );

        this.classLoader = (URLClassLoader) classLoader;
        this.rootDirectory = rootDirectory;
    }

    /**
     * Constructs new lib loader using default root directory.
     *
     * @param urlClassLoader class loader to use for loading of libraries
     */
    public LibLoader(@NonNull final URLClassLoader urlClassLoader) {
        this(urlClassLoader, new File("libs/artifacts/"));
    }

    /**
     * Constructs new lib loader using default root directory. This performs type-check of {@code classLoader}
     *      * in order to guarantee that it is instance of {@link URLClassLoader}.
     *
     * @param classLoader class loader to use for loading of libraries,
     * should normally be {@link URLClassLoader}
     *
     * @throws IllegalArgumentException if the {@code classLoader} is not {@link URLClassLoader}
     */
    public LibLoader(@NonNull final ClassLoader classLoader) {
        this(classLoader, new File("libs/artifacts/"));
    }

    /**
     * Constructs new lib loader. This uses class loader got from {@link #getAvailableUrlClassLoader()}.
     *
     * should normally be {@link URLClassLoader}
     * @param rootDirectory directory to store library artifacts and hashes in
     */
    public LibLoader(@NonNull final File rootDirectory) {
        this(getAvailableUrlClassLoader().orElseThrow(
                () -> new IllegalStateException("Cannot find any available URLClassLoader in current context")
                ), rootDirectory
        );
    }

    /**
     * Sets this lib loader's class loader.
     *
     * @param classLoader class loader to be used by this lib loader
     * @return self for chaining
     *
     * @see #setClassLoader(ClassLoader) variant performing checks of any classloader
     */
    public LibLoader setClassLoader(@NonNull final URLClassLoader classLoader) {
        this.classLoader = classLoader;

        return this;
    }

    /**
     * Sets this lib loader's class loader throwing an exception if it is not {@link URLClassLoader}.
     *
     * @param classLoader class loader to be used by this lib loader
     * @throws IllegalArgumentException if the {@code classLoader} is not {@link URLClassLoader}
     * @return self for chaining
     *
     * @see #setClassLoader(URLClassLoader) compile-time type-safe variant
     */
    public LibLoader setClassLoader(final ClassLoader classLoader) {
        if (!(classLoader instanceof URLClassLoader)) throw new IllegalArgumentException(
                classLoader + " is not instance of URLClassLoader"
        );

        setClassLoader((URLClassLoader) classLoader);

        return this;
    }

    /**
     * Call to this method guarantees that after it {@link #rootDirectory} will be a valid directory.
     */
    @SneakyThrows
    private void assureRootDirectoryExists() {
        if (!rootDirectory.isFile()) Files.createDirectories(rootDirectory.toPath());
    }

    /**
     * Reads content of a URL as a list of lines.
     *
     * @param url url to read data from
     * @return lines read from URL
     * @throws IOException if an exception occurs while reading
     */
    public static List<String> readLinesFromUrl(final URL url) throws IOException {
        try (val reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            val lines = new ArrayList<String>();

            String line;
            while ((line = reader.readLine()) != null) lines.add(line);

            return lines;
        }
    }

    /**
     * Reads content of a URL as a single line.
     *
     * @param url url to read data from
     * @return first line read from URL
     * @throws IOException if an exception occurs while reading
     */
    public static String readLineFromUrl(final URL url) throws IOException {
        try (val reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            return reader.readLine();
        }
    }

    /**
     * Loads a file from URL to file specified
     *
     * @param url url from which to get the file
     * @throws IOException if an exception occurs in an I/O operation
     */
    public static void loadFromUrl(final URL url, final File file) throws IOException {
        try (val stream = url.openStream()) {
            Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Loads a library by its coords.
     * Loading should happen if:
     * <ul>
     *     <li>The library is missing</li>
     *     <li>The hash is not the same</li>
     *     <li>Hash is empty anywhere</li>
     * </ul>
     *
     * @param name name to store this library by
     * @param libCoords coords of a library artifact
     * @param addToClasspath whether or not the library loaded should be added to classpath
     *
     * @return file of created artifact
     */
    @SneakyThrows
    public File loadLib(@NonNull final String name, @NonNull final LibCoords libCoords, final boolean addToClasspath) {
        assureRootDirectoryExists();

        log.info("Loading library " + name);

        val artifactFile = new File(rootDirectory, name + ".jar");
        val hashFile = new File(rootDirectory, name + ".hash");
        val hash = libCoords.computeHash();

        if (!artifactFile.isFile() || !hashFile.isFile() || !Files.lines(hashFile.toPath()).findFirst().equals(hash)) {
            {
                val url = libCoords.getArtifactUrl();
                log.info("Downloading library " + name + " from source: " + url);
                loadFromUrl(url, artifactFile);
            }
            Files.write(hashFile.toPath(), hash.orElse("").getBytes(StandardCharsets.UTF_8));
        }

        if (addToClasspath) addUrlToClasspath(classLoader, artifactFile.toURI().toURL());

        return artifactFile;
    }

    /**
     * Loads a library by its coords adding it to classpath.
     * Loading should happen if:
     * <ul>
     *     <li>The library is missing</li>
     *     <li>The hash is not the same</li>
     *     <li>Hash is empty anywhere</li>
     * </ul>
     *
     * @param name name to store this library by
     * @param libCoords coords of a library artifact
     *
     * @return file of created artifact
     *
     * @see #loadLib(String, LibCoords, boolean) called with {@code addToClasspath} set to {@code true}
     */
    @SneakyThrows
    public File loadLib(@NonNull final String name, @NonNull final LibCoords libCoords) {
        return loadLib(name, libCoords, true);
    }

    /**
     * Adds the specified URL to classpath of class loader.
     *
     * @param classLoader class loader to add the url to
     * @param url url to add to classpath of class loader
     */
    @SneakyThrows
    public static void addUrlToClasspath(@NonNull final URLClassLoader classLoader, @NonNull final URL url) {
        URL_CLASS_LOADER__ADD_URL_METHOD.invokeExact(classLoader, url);
    }
}
