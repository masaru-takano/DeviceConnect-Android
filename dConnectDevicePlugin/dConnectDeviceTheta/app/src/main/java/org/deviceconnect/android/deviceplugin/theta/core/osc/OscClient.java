package org.deviceconnect.android.deviceplugin.theta.core.osc;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class OscClient {

    private static final String HOST = "192.168.1.1:80";
    private static final HttpRequest.Method GET = HttpRequest.Method.GET;
    private static final HttpRequest.Method POST = HttpRequest.Method.POST;

    private static final String PATH_INFO = "/osc/info";
    private static final String PATH_STATE = "/osc/state";
    private static final String PATH_COMMANDS_EXECUTE = "/osc/commands/execute";
    private static final String PATH_COMMANDS_STATUS = "/osc/commands/status";

    private static final String REQ_PARAM_ID = "id";

    private static final String RES_PARAM_STATE = "state";
    private static final String RES_PARAM_RESULTS = "results";
    private static final String RES_PARAM_ENTRIES = "entries";

    private final HttpClient mHttpClient;

    public OscClient() {
        mHttpClient = new HttpClient();
    }

    public OscState state() throws IOException, JSONException {
        HttpRequest request = new HttpRequest(POST, HOST, PATH_STATE);
        HttpResponse response = mHttpClient.execute(request);

        JSONObject json = response.getJSON();
        JSONObject state = json.getJSONObject(RES_PARAM_STATE);
        return OscState.parse(state);
    }

    public OscCommand.Result listAll(final int offset, final int maxLength) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("entryCount", maxLength);
        if (offset > 0) {
            params.put("continuationToken", String.valueOf(offset));
        }

        HttpResponse response = executeCommand("camera._listAll", params);
        return OscCommand.Result.parse(response);
    }

    public OscSession startSession() throws IOException, JSONException {
        HttpResponse response = executeCommand("camera.startSession", new JSONObject());
        JSONObject json = response.getJSON();
        JSONObject results = json.getJSONObject(RES_PARAM_RESULTS);
        return OscSession.parse(results);
    }

    public OscCommand.Result closeSession(final String sessionId) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("sessionId", sessionId);

        HttpResponse response = executeCommand("camera.closeSession", params);
        return OscCommand.Result.parse(response);
    }

    public OscCommand.Result takePicture(final String sessionId) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("sessionId", sessionId);

        HttpResponse response = executeCommand("camera.takePicture", params);
        return OscCommand.Result.parse(response);
    }

    public OscCommand.Result startCapture(final String sessionId) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("sessionId", sessionId);

        HttpResponse response = executeCommand("camera._startCapture", params);
        return OscCommand.Result.parse(response);
    }

    public OscCommand.Result stopCapture(final String sessionId) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("sessionId", sessionId);

        HttpResponse response = executeCommand("camera._stopCapture", params);
        return OscCommand.Result.parse(response);
    }

    public OscCommand.Result delete(final String fileUri) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("fileUri", fileUri);

        HttpResponse response = executeCommand("camera.delete", params);
        return OscCommand.Result.parse(response);
    }

    public OscCommand.Result getImage(final String fileUri, final boolean isThumbnail) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("fileUri", fileUri);
        params.put("_type", isThumbnail ? "thumb" : "full");

        HttpResponse response = executeCommand("camera.getImage", params);
        return OscCommand.Result.parse(response);
    }

    public OscCommand.Result getVideo(final String fileUri, final boolean isThumbnail) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("fileUri", fileUri);
        params.put("_type", isThumbnail ? "thumb" : "full");

        HttpResponse response = executeCommand("camera._getVideo", params);
        return OscCommand.Result.parse(response);
    }

    public InputStream getLivePreview(final String sessionId) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("sessionId", sessionId);

        HttpResponse response = executeCommand("camera._getLivePreview", params);
        return response.getStream();
    }

    public OscCommand.Result getMetaData(final String fileUri) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("fileUri", fileUri);

        HttpResponse response = executeCommand("camera.getMetadata", params);
        return OscCommand.Result.parse(response);
    }

    public OscCommand.Result getOptions(final String sessionId, final JSONArray optionNames) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("sessionId", sessionId);
        params.put("optionNames", optionNames);

        HttpResponse response = executeCommand("camera.getOptions", params);
        return OscCommand.Result.parse(response);
    }

    public OscCommand.Result setOptions(final String sessionId, final JSONObject options) throws IOException, JSONException {
        JSONObject params = new JSONObject();
        params.put("sessionId", sessionId);
        params.put("options", options);

        HttpResponse response = executeCommand("camera.setOptions", params);
        return OscCommand.Result.parse(response);
    }

    private HttpResponse executeCommand(final String name, final JSONObject params) throws IOException {
        try {
            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("parameters", params);

            HttpRequest request = new HttpRequest(POST, HOST, PATH_COMMANDS_EXECUTE);
            request.setBody(body.toString());
            return mHttpClient.execute(request);
        } catch (JSONException e) {
            throw new RuntimeException();
        }
    }

    private OscCommand.Result statusCommand(final String commandId) throws IOException {
        try {
            JSONObject body = new JSONObject();
            body.put(REQ_PARAM_ID, commandId);

            HttpRequest request = new HttpRequest(POST, HOST, PATH_COMMANDS_STATUS);
            request.setBody(body.toString());
            HttpResponse response = mHttpClient.execute(request);
            return OscCommand.Result.parse(response);
        } catch (JSONException e) {
            throw new RuntimeException();
        }
    }

    public OscCommand.Result waitForDone(final String commandId) throws IOException, JSONException, InterruptedException {
        for (;;) {
            OscCommand.Result result = statusCommand(commandId);
            JSONObject json = result.getJSON();
            String state = json.getString(RES_PARAM_STATE);
            if ("done".equals(state)) {
                return result;
            }

            Thread.sleep(200);
        }
    }

}
