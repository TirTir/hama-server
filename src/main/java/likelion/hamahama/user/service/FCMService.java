package likelion.hamahama.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import likelion.hamahama.brand.entity.Brand;
import likelion.hamahama.brand.entity.BrandLike;
import likelion.hamahama.brand.repository.BrandLikeRepsitory;
import likelion.hamahama.user.dto.FCMMessageDto;
import likelion.hamahama.brand.repository.BrandRepository;
import likelion.hamahama.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final String API_URL = "https://fcm.googleapis.com//v1/projects/hamahama-sku20230818/messages:send";
    private final ObjectMapper objectMapper;

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrandLikeRepsitory brandLikeRepsitory;

    @Transactional
    public String getAccessToken() throws IOException {
        String firebaseConfigPath = "templates/firebase-admin.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        System.out.println("Access Token: " + googleCredentials.getAccessToken().getTokenValue());
        return googleCredentials.getAccessToken().getTokenValue();
    }

    @Transactional
    public void sendMessageTo(String topic, String title, String body, String link) throws IOException, FirebaseMessagingException {
        topicCreate();

        String message = makeMessage(topic, title, body, link);
        System.out.println(message);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());

        //sendWebPush(targetToken, title, body);
    }

    @Transactional
    public String makeMessage(String topic, String title, String body, String link) throws JsonProcessingException {
        FCMMessageDto fcmMessageDto = FCMMessageDto.builder()
                .message(FCMMessageDto.Message.builder()
                        .topic(topic)
                        .notification(FCMMessageDto.Notification.builder()
                                .title(title)
                                .body(body)
                                .build())
                        .webpush(FCMMessageDto.Webpush.builder()
                                .fcm_options(FCMMessageDto.FcmOptions.builder()
                                        .link(link)
                                        .build())
                                .build())
                        .build())
                .validate_only(false)
                .build();
        return objectMapper.writeValueAsString(fcmMessageDto);
    }

    @Transactional
    public void topicCreate() throws FirebaseMessagingException {

        List<String> brandNames = new ArrayList<>();
        List<BrandLike> brandLikes = brandLikeRepsitory.findAll();

        brandLikes.forEach(brandLike -> {
            if (!brandNames.contains(brandLike.getBrand().getBrandEnglishName())) {
                brandNames.add(brandLike.getBrand().getBrandEnglishName());
                System.out.println(brandNames);
            }
        });

        System.out.println(brandNames);

        for (int i = 0; i < brandNames.size(); i++) {

            List<String> registrationTokens = new ArrayList<>();
            String topic = brandNames.get(i);

            System.out.println("주제는 " + topic);

            Brand brand = brandRepository.findByBrandEnglishName(topic);
            // 브랜드 즐겨찾기 테이블에서 주제로 설정한 브랜드에 해당하는 모든 행
            Optional<List<BrandLike>> brand_likes_list = brandLikeRepsitory.findByBrandId(brand.getId());

            brand_likes_list.get().forEach(list -> {

                    registrationTokens.add(list.getUser().getFcmToken());

            });

            LinkedHashSet<String> li_hs = new LinkedHashSet<String>(registrationTokens);
            registrationTokens.clear();
            registrationTokens.addAll(li_hs);


            System.out.println("등록토큰들: " + registrationTokens + " " +i);
            if(registrationTokens != null){
                TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, topic);
                System.out.println(response.getSuccessCount() + " tokens were subscribed successfully");
            }
        }
    }
}
