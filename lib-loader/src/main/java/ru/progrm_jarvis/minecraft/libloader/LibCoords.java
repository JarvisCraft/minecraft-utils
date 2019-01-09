package ru.progrm_jarvis.minecraft.libloader;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Coords of a library with dependencies.
 */
@FunctionalInterface
public interface LibCoords {

    String MAVEN_CENTRAL_REPO_URL = "https://repo1.maven.org/maven2/";

    String SONATYPE_OSS_SNAPSHOTS_REPO_URL = "https://oss.sonatype.org/content/repositories/snapshots/";

    /**
     * Gets the URL of the artifact containing dependency classes.
     *
     * @return URL of the artifact containing dependency classes
     */
    URL getArtifactUrl();

    @SneakyThrows(MalformedURLException.class)
    static LibCoords fromMavenRepo(@NonNull final String repositoryUrl,
                                   @NonNull final String groupId, @NonNull final String artifactId,
                                   @NonNull final String version) {
        val jarUrl
                = (repositoryUrl.lastIndexOf('/') == repositoryUrl.length() - 1 ? repositoryUrl : repositoryUrl + '/')
                + groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/'
                + artifactId + '-' + version + ".jar";

        return new MavenLibCoords(new URL(jarUrl), new URL(jarUrl + ".sha1"), new URL(jarUrl + ".sha1"));
    }

    /**
     * Computes the hashcode of the library not to repeat loading of the same library (if possible).
     *
     * @return optional containing hash of the artifact or empty optional if not available
     *
     * @apiNote this may be a time-consuming operation
     */
    default Optional<String> computeHash() {
        return Optional.empty();
    }

    static LibCoords fromMavenCentralRepo(@NonNull final String groupId, @NonNull final String artifactId,
                                          @NonNull final String version) {
        return fromMavenRepo(MAVEN_CENTRAL_REPO_URL, groupId, artifactId, version);
    }

    static LibCoords fromSonatypeOssSnapshotsRepo(@NonNull final String groupId, @NonNull final String artifactId,
                                                  @NonNull final String version) {
        return fromMavenRepo(SONATYPE_OSS_SNAPSHOTS_REPO_URL, groupId, artifactId, version);
    }

    @Value
    @FieldDefaults(level = AccessLevel.PROTECTED)
    final class MavenLibCoords implements LibCoords {
        @NonNull URL artifactUrl;
        @Nullable URL hash1Url, hash2Url;

        public MavenLibCoords(@NonNull final URL artifactUrl,
                              @Nullable final URL hash1Url, @Nullable final URL hash2Url) {
            this.artifactUrl = artifactUrl;
            this.hash1Url = hash1Url;
            this.hash2Url = hash2Url;
        }

        public MavenLibCoords(@NonNull final URL jarUrl, final URL hash1Url) {
            this(jarUrl, hash1Url, null);
        }

        public MavenLibCoords(@NonNull final URL jarUrl) {
            this(jarUrl, null, null);
        }

        @Override
        public Optional<String> computeHash() {
            if (hash1Url == null) return Optional.empty();
            try {
                if (hash2Url == null) return Optional.ofNullable(LibLoader.readLineFromUrl(hash1Url));
                else {
                    val hash1 = LibLoader.readLinesFromUrl(hash1Url);
                    if (hash1 == null) return Optional.empty();

                    val hash2 = LibLoader.readLineFromUrl(hash2Url);
                    if (hash2 == null) return Optional.empty();

                    return Optional.of(hash1 + "_" + hash2);
                }
            } catch (final IOException e) {
                return Optional.empty();
            }
        }
    }
}
