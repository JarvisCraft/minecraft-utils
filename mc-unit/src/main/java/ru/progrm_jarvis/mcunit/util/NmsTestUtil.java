package ru.progrm_jarvis.mcunit.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.Bukkit;

import java.util.regex.Pattern;

/**
 * Utility for NMS-related functionality.
 */
@UtilityClass
public class NmsTestUtil {

    /**
     * Pattern matching a server class name which has {@code v<Major>_<Minor>_R<Patch>} in its package.
     */
    private static final Pattern NMS_SERVER_VERSION_PART_PATTERN = Pattern
            .compile("(?:\\w+\\.)*v\\d+_\\d+_R\\d+\\.\\w+");

    /**
     * Checks whether the specified class name is a valid NMS server name.
     *
     * @param serverName server name to check
     * @return {@link true} if the class name is a valid NM-server name and {@link false} otherwise
     *
     * @apiNote valid NMS-server name contains {@code v<Major>_<Minor>_R<Patch>} in its package
     */
    public boolean isNmsServerClassName(@NonNull final String serverName) {
        return NMS_SERVER_VERSION_PART_PATTERN.matcher(serverName).matches();
    }

    /**
     * Checks whether current environment is an NMS-environment.
     *
     * @return {@code true} if current environment is (<i>possibly</i>) a valid NMS-environment
     * and {@link false} otherwise
     *
     * @apiNote this method checks whether {@link Bukkit#getServer()} is not {@link null}
     * and that its canonical class name is a valid NMS-server class name
     *
     * @see #isNmsServerClassName(String) method used for checking {@link Bukkit#getServer()}'s class's canonical name
     */
    public boolean isEnvironmentNms() {
        val server = Bukkit.getServer();
        //noinspection ConstantConditions
        if (server == null) return false;

        return isNmsServerClassName(server.getClass().getCanonicalName());
    }
}
