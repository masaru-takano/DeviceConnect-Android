package org.deviceconnect.android.profile.spec;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DConnectApiSpecList {

    private final List<DConnectApiSpec> mApiSpecList = new ArrayList<DConnectApiSpec>();

    public DConnectApiSpecList() {}

    public DConnectApiSpec findApiSpec(final String method, final String path) {
        for (DConnectApiSpec spec : mApiSpecList) {
            if (spec.getMethod().getName().equals(method) && spec.getPath().equals(path)) {
                return spec;
            }
        }
        return null;
    }

    public void addApiSpecList(final InputStream json) throws IOException, JSONException {
        String file = loadFile(json);
        JSONArray array = new JSONArray(file);
        for (int i = 0; i < array.length(); i++) {
            JSONObject apiObj = array.getJSONObject(i);
            DConnectApiSpec apiSpec = DConnectApiSpec.fromJson(apiObj);
            if (apiSpec != null) {
                addApiSpec(apiSpec);
            }
        }
    }

    private String loadFile(final InputStream in) throws IOException {
        try {
            byte[] buf = new byte[1024];
            int len;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = in.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            return new String(baos.toByteArray());
        } finally {
            in.close();
        }
    }

    private void addApiSpec(final DConnectApiSpec apiSpec) {
        mApiSpecList.add(apiSpec);
    }

}