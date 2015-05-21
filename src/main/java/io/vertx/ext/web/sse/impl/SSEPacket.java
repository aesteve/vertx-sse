package io.vertx.ext.web.sse.impl;

import io.vertx.core.buffer.Buffer;

class SSEPacket {

    private StringBuilder payload;
    String headerName;
    String headerValue;

    SSEPacket() {
        payload = new StringBuilder();
    }

    boolean append(Buffer buffer) {
        String response = buffer.toString();
        boolean willTerminate = response.endsWith("\n\n");
        String[] lines = response.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int idx = line.indexOf(":");
            if (idx == -1) {
                continue; // ignore line
            }
            String type = line.substring(0, idx);
            String data = line.substring(idx + 2, line.length());
            if (i == 0 && headerName == null && !type.equals("data")) {
                headerName = type;
                headerValue = data;
            } else {
                payload.append(data + "\n");
            }
        }
        return willTerminate;
    }

    @Override
    public String toString() {
        return payload == null ? "" : payload.toString();
    }
}
