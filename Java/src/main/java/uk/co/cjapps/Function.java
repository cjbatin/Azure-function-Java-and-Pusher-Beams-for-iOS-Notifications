package uk.co.cjapps;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.pusher.pushnotifications.PushNotifications;
import com.microsoft.azure.functions.*;
public class Function {
    @FunctionName("HttpTrigger-Java")
    public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
            HttpMethod.POST }, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<Notification>> request,
            final ExecutionContext context) {
        String instanceId = "39f68ae7-be60-4aed-94e4-a459697d1e25";
        String secretKey = "A16E5B492C70B978DE33404E8985767A6E6F4AC063E69CDD34F7C44FA5BD38E7";
        final Notification body = request.getBody().get();
        if (body == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
            .body("Please pass a string or in the request body").build();
        }
        PushNotifications beamsClient = new PushNotifications(instanceId, secretKey);
        List<String> interests = Arrays.asList("hello");
        Map<String, Map> publishRequest = new HashMap();
        Map<String, String> alertMessage = new HashMap();
        alertMessage.put("title", body.getTitle());
        alertMessage.put("body", body.getMessage());
        Map<String, Map> alert = new HashMap();
        alert.put("alert", alertMessage);
        Map<String, Map> aps = new HashMap();
        aps.put("aps", alert);
        publishRequest.put("apns", aps);
        try {
            beamsClient.publishToInterests(interests, publishRequest);
            return request.createResponseBuilder(HttpStatus.OK).body("Push Sent").build();
        } catch (IOException e) {
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Push Failed").build();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Push Failed").build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Push Failed").build();
        }
    }
}