/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.asterix.experiment.action.derived;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.asterix.experiment.action.base.AbstractAction;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class RunAQLFileAction extends AbstractAction {
    private final Logger LOGGER = Logger.getLogger(RunAQLFileAction.class.getName());
    private static final String REST_URI_TEMPLATE = "http://{0}:{1}/aql";

    private final CloseableHttpClient httpClient;

    private String aqlContent;

    private final String restHost;

    private final int restPort;

    private final OutputStream os;

    public RunAQLFileAction(CloseableHttpClient httpClient, String restHost, int restPort, Path aqlFilePath)
            throws IOException {
        this(httpClient, restHost, restPort,
                StandardCharsets.UTF_8.decode(ByteBuffer.wrap(Files.readAllBytes(aqlFilePath))).toString());
    }

    public RunAQLFileAction(CloseableHttpClient httpClient, String restHost, int restPort, Path aqlFilePath,
            OutputStream os) throws IOException {
        this(httpClient, restHost, restPort,
                StandardCharsets.UTF_8.decode(ByteBuffer.wrap(Files.readAllBytes(aqlFilePath))).toString(), os);
    }

    public RunAQLFileAction(CloseableHttpClient httpClient, String restHost, int restPort, String aqlContent) {
        this(httpClient, restHost, restPort, aqlContent, null);
    }

    public RunAQLFileAction(CloseableHttpClient httpClient, String restHost, int restPort, String aqlContent,
            OutputStream os) {
        this.httpClient = httpClient;
        this.aqlContent = aqlContent;
        this.restHost = restHost;
        this.restPort = restPort;
        this.os = os;
    }

    @Override
    public void doPerform() throws Exception {
        String uri = MessageFormat.format(REST_URI_TEMPLATE, restHost, String.valueOf(restPort));
        HttpEntity ent = EntityBuilder.create().setText(aqlContent).build();
        //StandardCharsets.UTF_8
        HttpEntity entity = null;
        CloseableHttpResponse resp = null;
        try {
            //            int timeout = 300000;
            HttpUriRequest request = RequestBuilder.post().setUri(uri).setHeader("Accept", "application/x-adm")
                    .setHeader("Connection", "close").setEntity(ent).setConfig(RequestConfig.copy(RequestConfig.DEFAULT)
                            /*.setSocketTimeout(10000)
                            .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)*/.build())
                    .build();
            resp = httpClient.execute(request);
            entity = resp.getEntity();
            if (entity != null && entity.isStreaming()) {
                printStream(entity.getContent());
            }
            if (aqlContent.contains("compact")) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("Compaction has been completed");
                }
            }
        } finally {
            EntityUtils.consume(entity);
            if (resp != null) {
                resp.close();
            }
            //            httpClient.getConnectionManager().closeExpiredConnections();
            //            httpClient.getConnectionManager().closeIdleConnections(1, TimeUnit.SECONDS);;
        }
    }

    private void printStream(InputStream content) throws IOException {
        if (os == null) {
            IOUtils.copy(content, System.out);
            System.out.flush();
        } else {
            OutputStream choppedOutput = new NoNewLineFileInputStream(os);
            IOUtils.copy(content, choppedOutput);
            choppedOutput.flush();
        }
    }

    class NoNewLineFileInputStream extends OutputStream {

        private OutputStream outerStream;

        public NoNewLineFileInputStream(OutputStream outerStream) throws FileNotFoundException {
            this.outerStream = outerStream;
        }

        @Override
        public void write(int c) throws IOException {
            if (c != -1 && c != '\n' && c != '\r') {
                outerStream.write(c);
            }
        }
    }
}
