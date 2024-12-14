package openerp.openerpresourceserver.tarecruitment.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class TestCallApiImpl implements TestCallApi{

    public String getAccessToken() {
        try {
            OkHttpClient client = new OkHttpClient();

            String username = "";
            String password = "";

            RequestBody requestBody = new FormBody.Builder()
                    .add("grant_type", "client_credentials")
                    .add("client_id", "timetable")
                    .add("client_secret", "")
                    .add("username", username)
                    .add("password", password)
                    .build();

            Request request = new Request.Builder().url("https://programming.daotao.ai/iam/realms/LMS/protocol/openid-connect/token")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            Call call = client.newCall(request);

            Response response = call.execute();

            if (response.isSuccessful()) {
                JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject();
                return jsonObject.get("access_token").getAsString();
            } else {
                throw new Exception(response.message() + response.code() + " ERROR FROM LOGIN API");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public int testCallApi(String authorizationHeader) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url("https://timetable.soict.ai/api/class-call/get-all-class?index=0&limit=5")
                    .header("Authorization", authorizationHeader)
                    .build();

            Call call = client.newCall(request);

            Response response = call.execute();

            log.info(response.code() + " IS THE RESPONSE CODE");

            return response.code();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
