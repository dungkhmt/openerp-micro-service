package openerp.openerpresourceserver.tarecruitment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class TestCallApiImpl implements TestCallApi{
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
