package com.mylearn.tracker.admin;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Lists sub-folders on the machine running the app, for the "choose backup folder" dialog
 * (a browser page can't open a native folder picker for the server's disk). Folders only;
 * the app listens on 127.0.0.1 in the desktop and systemd setups.
 */
@Component
public class FolderBrowser {

    private static final int MAX_ENTRIES = 500;

    /** @param path null/blank = the starting points (drives and home folder) */
    public FolderListing list(String path) {
        if (path == null || path.isBlank()) {
            List<Folder> roots = new ArrayList<>();
            Path home = Path.of(System.getProperty("user.home"));
            roots.add(new Folder("Home (" + home.getFileName() + ")", home.toString()));
            for (File root : File.listRoots()) {
                roots.add(new Folder(root.getPath(), root.getPath()));
            }
            return new FolderListing(null, null, roots);
        }
        Path dir;
        try {
            dir = Path.of(path).toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            throw new BusinessRuleException("Not a valid folder path: " + path);
        }
        if (!Files.isDirectory(dir)) {
            throw new BusinessRuleException("Folder not found: " + dir);
        }
        List<Folder> folders = new ArrayList<>();
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(dir, Files::isDirectory)) {
            for (Path p : entries) {
                if (folders.size() >= MAX_ENTRIES) {
                    break;
                }
                if (!isHidden(p)) {
                    folders.add(new Folder(p.getFileName().toString(), p.toString()));
                }
            }
        } catch (IOException | SecurityException e) {
            throw new BusinessRuleException("Can't open " + dir + " (access denied?)");
        }
        folders.sort(Comparator.comparing(f -> f.name().toLowerCase()));
        Path parent = dir.getParent();
        return new FolderListing(dir.toString(), parent == null ? "" : parent.toString(), folders);
    }

    private static boolean isHidden(Path p) {
        try {
            String name = p.getFileName().toString();
            return name.startsWith(".") || name.startsWith("$") || Files.isHidden(p);
        } catch (IOException e) {
            return true;
        }
    }

    public record Folder(String name, String path) {
    }

    /**
     * @param path   the listed folder (null for the starting points)
     * @param parent folder above it ("" = go back to the starting points)
     */
    public record FolderListing(String path, String parent, List<Folder> folders) {
    }
}
