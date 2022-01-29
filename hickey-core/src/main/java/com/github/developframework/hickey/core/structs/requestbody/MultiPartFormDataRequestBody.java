package com.github.developframework.hickey.core.structs.requestbody;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * @author qiushui on 2022-01-29.
 */
public final class MultiPartFormDataRequestBody implements RequestBody {

    private final List<PartsSpecification> partsSpecifications = new ArrayList<>();

    @Getter
    private final String boundary;

    public MultiPartFormDataRequestBody() {
        final StringBuilder sb = new StringBuilder();
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }
        this.boundary = sb.toString();
    }

    @Override
    public byte[] toByteArray(Charset charset) {
        if (partsSpecifications.isEmpty()) {
            return new byte[0];
        }
        addFinalBoundaryPart();
        return assemble(charset);
    }

    @Override
    public String pretty() {
        return "(form data)";
    }

    public MultiPartFormDataRequestBody addPart(String name, String value) {
        PartsSpecification newPart = new PartsSpecification();
        newPart.type = PartsSpecification.Type.STRING;
        newPart.name = name;
        newPart.value = value;
        partsSpecifications.add(newPart);
        return this;
    }

    public MultiPartFormDataRequestBody addPart(String name, Path path) {
        PartsSpecification newPart = new PartsSpecification();
        newPart.type = PartsSpecification.Type.FILE;
        newPart.name = name;
        newPart.path = path;
        partsSpecifications.add(newPart);
        return this;
    }

    public MultiPartFormDataRequestBody addPart(String name, String filename, String contentType, byte[] bytes) {
        PartsSpecification newPart = new PartsSpecification();
        newPart.type = PartsSpecification.Type.BYTES;
        newPart.name = name;
        newPart.bytes = bytes;
        newPart.filename = filename;
        newPart.contentType = contentType;
        partsSpecifications.add(newPart);
        return this;
    }

    public MultiPartFormDataRequestBody addPart(String name, String contentType, File file) {
        return addPart(name, file.getName(), contentType, () -> {
            try (InputStream is = new FileInputStream(file)) {
                return is;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public MultiPartFormDataRequestBody addPart(String name, String filename, String contentType, Supplier<InputStream> stream) {
        PartsSpecification newPart = new PartsSpecification();
        newPart.type = PartsSpecification.Type.STREAM;
        newPart.name = name;
        newPart.stream = stream;
        newPart.filename = filename;
        newPart.contentType = contentType;
        partsSpecifications.add(newPart);
        return this;
    }

    private void addFinalBoundaryPart() {
        PartsSpecification newPart = new PartsSpecification();
        newPart.type = PartsSpecification.Type.FINAL_BOUNDARY;
        newPart.value = "--" + boundary + "--";
        partsSpecifications.add(newPart);
    }

    private static class PartsSpecification {

        public enum Type {
            STRING, FILE, BYTES, STREAM, FINAL_BOUNDARY
        }

        public Type type;
        public String name;
        public String value;
        public Path path;
        public byte[] bytes;
        public Supplier<InputStream> stream;
        public String filename;
        public String contentType;

    }

    @RequiredArgsConstructor
    private class PartsIterator implements Iterator<byte[]> {

        private final Charset charset;

        private final Iterator<PartsSpecification> iterator = partsSpecifications.iterator();

        private InputStream currentInputStream;

        private byte[] nextBytes;

        private static final String NEW_LINE = "\r\n";

        @Override
        public boolean hasNext() {
            try {
                nextBytes = currentInputStream == null ? determineNextPart(charset) : readCurrentInputStream();
                return nextBytes != null;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public byte[] next() {
            byte[] result = nextBytes;
            nextBytes = null;
            return result;
        }

        /**
         * 决定下一个Part
         */
        private byte[] determineNextPart(Charset charset) throws IOException {
            if (!iterator.hasNext()) return null;
            final PartsSpecification nextPart = iterator.next();
            switch (nextPart.type) {
                case FINAL_BOUNDARY: {
                    return nextPart.value.getBytes(StandardCharsets.UTF_8);
                }
                case STRING: {
                    currentInputStream = new ByteArrayInputStream((nextPart.value).getBytes(StandardCharsets.UTF_8));
                    return headerBytes(nextPart.name, null, "text/plain; charset=" + charset.displayName());
                }
                case BYTES: {
                    currentInputStream = new ByteArrayInputStream(nextPart.bytes);
                    return headerBytes(
                            nextPart.name,
                            nextPart.filename,
                            nextPart.contentType
                    );
                }
                case FILE: {
                    currentInputStream = Files.newInputStream(nextPart.path);
                    return headerBytes(
                            nextPart.name,
                            nextPart.path.getFileName().toString(),
                            Files.probeContentType(nextPart.path)
                    );
                }
                case STREAM: {
                    currentInputStream = nextPart.stream.get();
                    return headerBytes(
                            nextPart.name,
                            nextPart.filename,
                            nextPart.contentType
                    );
                }
                default:
                    throw new AssertionError();
            }
        }

        private byte[] readCurrentInputStream() throws IOException {
            byte[] buffer = new byte[8192];
            int r = currentInputStream.read(buffer);
            if (r > 0) {
                byte[] actualBytes = new byte[r];
                System.arraycopy(buffer, 0, actualBytes, 0, r);
                return actualBytes;
            } else {
                currentInputStream.close();
                currentInputStream = null;
                return NEW_LINE.getBytes();
            }
        }

        private byte[] headerBytes(String name, String filename, String contentType) {
            StringBuilder sb = new StringBuilder("--")
                    .append(boundary).append(NEW_LINE)
                    .append("Content-Disposition: form-data; name=").append(name);
            if (filename != null) {
                sb.append("; filename=").append(filename);
            }
            sb.append(NEW_LINE).append("Content-Type: ").append(contentType).append(NEW_LINE).append(NEW_LINE);
            return sb.toString().getBytes(charset);
        }
    }

    private byte[] assemble(Charset charset) {
        // 使用以下方法 自己拼装byte[]
        int length = 0, pos = 0;
        PartsIterator iteratorForCount = new PartsIterator(charset);
        while (iteratorForCount.hasNext()) {
            length += iteratorForCount.next().length;
        }
        byte[] data = new byte[length];
        PartsIterator iteratorForBytes = new PartsIterator(charset);
        while (iteratorForBytes.hasNext()) {
            final byte[] nextBytes = iteratorForBytes.next();
            System.arraycopy(nextBytes, 0, data, pos, nextBytes.length);
            pos += nextBytes.length;
        }
        return data;
    }
}
