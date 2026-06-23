package xyz.ItzArad.bafLibs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
public class JsonStorage {

    private final Path baseDir;
    private final Gson gson;
    private final Executor ioExecutor;

    public JsonStorage(Path baseDir) throws IOException{
        this(baseDir, new GsonBuilder().serializeNulls().setPrettyPrinting().create(), Executors.newFixedThreadPool(2));
    }
    public JsonStorage(Path baseDir, Gson gson, Executor ioExecutor) throws IOException {
        this.baseDir = baseDir;
        this.gson = gson;
        this.ioExecutor = ioExecutor;
        ensureBaseDir();
    }
    private void ensureBaseDir() throws IOException {
        if (Files.notExists(baseDir)) {
            Files.createDirectories(baseDir);
        } else if (!Files.isDirectory(baseDir)) {
            throw new IOException("Base path exists but is not a directory: " + baseDir);
        }
    }

    private Path pathFor(UUID uuid) {
        return baseDir.resolve(uuid.toString() + ".json");
    }

    public <T> void save(UUID uuid, T obj) throws IOException {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(obj, "obj");
        Path dest = pathFor(uuid);
        Path temp = Files.createTempFile(baseDir, uuid.toString(), ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(obj, writer);
        }
        Files.move(temp, dest, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    public <T> T load(UUID uuid, Class<T> clazz) throws IOException {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(clazz, "clazz");
        Path f = pathFor(uuid);
        if (Files.notExists(f)) return null;
        try (BufferedReader reader = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, clazz);
        }
    }

    public <T> T load(UUID uuid, Type typeOfT) throws IOException {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(typeOfT, "typeOfT");
        Path f = pathFor(uuid);
        if (Files.notExists(f)) return null;
        try (BufferedReader reader = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, typeOfT);
        }
    }
    public boolean delete(UUID uuid) throws IOException {
        Objects.requireNonNull(uuid, "uuid");
        Path f = pathFor(uuid);
        return Files.deleteIfExists(f);
    }
    public boolean exists(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return Files.exists(pathFor(uuid));
    }
    public List<UUID> listAll() throws IOException {
        try {
            return Files.list(baseDir)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .map(p -> p.getFileName().toString().replaceFirst("\\.json$", ""))
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        } catch (NoSuchFileException e) {
            return Collections.emptyList();
        }
    }
    public <T> CompletableFuture<Void> saveAsync(UUID uuid, T obj) {
        return CompletableFuture.runAsync(() -> {
            try {
                save(uuid, obj);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ioExecutor);
    }

    public <T> CompletableFuture<T> loadAsync(UUID uuid, Class<T> clazz) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return load(uuid, clazz);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ioExecutor);
    }

    public <T> CompletableFuture<T> loadAsync(UUID uuid, Type typeOfT) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return load(uuid, typeOfT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ioExecutor);
    }

    public CompletableFuture<Boolean> deleteAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return delete(uuid);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ioExecutor);
    }

    public CompletableFuture<List<UUID>> listAllAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return listAll();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ioExecutor);
    }
    public String readRawJson(UUID uuid) throws IOException {
        Path f = pathFor(uuid);
        if (Files.notExists(f)) return null;
        return Files.readString(f, StandardCharsets.UTF_8);
    }

    public void writeRawJson(UUID uuid, String json) throws IOException {
        Path dest = pathFor(uuid);
        Path temp = Files.createTempFile(baseDir, uuid.toString(), ".tmp");
        Files.writeString(temp, json, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        Files.move(temp, dest, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }
}
