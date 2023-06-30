package com.upwork.docker.service;

import okhttp3.*;
import org.newsclub.net.unix.AFSocketAddress;
import org.newsclub.net.unix.AFSocketFactory;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.SocketAddress;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;

@Service
public class DockerService {
    String requestUrl = "http://localhost/v1.43/containers/create";
    private static final String DOCKER_SOCKET_PATH = "/var/run/docker.sock";

    public ResponseBody createContainer(String image, String[] cmd) throws IOException {

        String requestPayload = "{\"Image\": \"" + image + "\", \"Cmd\": \"" + Arrays.toString(cmd) + "\"}";

        SocketAddress addr = this.parseAddress("--unix", DOCKER_SOCKET_PATH);

        OkHttpClient.Builder builder = new OkHttpClient.Builder() //
                .socketFactory(new AFSocketFactory.FixedAddressSocketFactory(addr)) //
                .callTimeout(Duration.ofMinutes(1));

        OkHttpClient client = builder.build();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(requestPayload, mediaType);

        Request request = new Request.Builder()
                .url(requestUrl)
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {

            ResponseBody body = response.body();
            OutputStream outputStream = null;
            try {
                if (body != null) {
                    outputStream = new ByteArrayOutputStream();
                    try (InputStream in = body.byteStream()) {
                        transferAllBytes(in, outputStream);
                    }
                }
                assert body != null;
                return ResponseBody.create(
                        outputStream != null ? outputStream.toString() : "Failed",
                        MediaType.parse("application/json")
                );
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        }
    }

    /**
     * Transfers all remaining bytes from the given {@link InputStream} to the given
     * {@link OutputStream}.
     *
     * @param in  The source.
     * @param out The target.
     * @throws IOException on error.
     */
    public static void transferAllBytes(InputStream in, OutputStream out) throws IOException {
        JavaReleaseShim.transferAllBytes(in, out);
    }

    public SocketAddress parseAddress(String opt, String val)
            throws IOException {
        switch (opt) {
            case "--unix":
                return AFUNIXSocketAddress.of(new File(val));
            case "--url":
                return AFSocketAddress.of(URI.create(val));
            default:
                throw new IllegalArgumentException("Valid parameters: --unix <path> OR --url <URL>");
        }
    }
}