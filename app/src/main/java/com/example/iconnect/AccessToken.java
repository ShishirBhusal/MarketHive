package com.example.iconnect;

import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {
    private static final String firebaseMessagingScope =
            "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"iconnect-1b66d\",\n" +
                    "  \"private_key_id\": \"51dbcf1c98b37c996f4bb70c24856aa99c4651ba\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDSNL+5U05i5P02\\n5hLubk6EEfuTMPG9O1rm1xoGCnBSWoIVXePC2DR+CTw+K33HlxJ7VTRr6rqmX1cx\\nEXFl6AYQU9iDvubVGgYERhVUpuFziQqI1oilBT9usRJHVxwOoW8AvSDXJ77g4kg5\\nGwxC3lBdvvVTugjnsoHLBVtoZ2qYT0dTtRddTuLtUcjU5TWW4/8H1RUlKPwRiWds\\nOHBijPQs06gxfwhrckFzRQuQIDelUpMKKc891lR+WKsE3QyTLmjeDvccnAvpd9B+\\n6o5HFxpXnY7ro7kOnNtEfovWqIUJiN994aCI2iv9An5yKlW1rCYvKcABLiXgUupK\\n9JhXZ3SbAgMBAAECggEAZXMi0uEjVDRbBvtcSKmBCFmbdqrpCKCUWxOWUjMIiZ53\\n6LMDIP7yup9fbUvVLRY6y75QCgNj6Lbb4GkgNwQXpRqqRM9GHbQtEjP2yH+wEHAj\\n3u3XTflt51DbVwXyGxeLeKpOprheda+QFst4i+86jzwBr9J5yMUMwjyIJKfAuNeu\\nU1oGldGoXK/pIv8vJxtS9GEn5xWdDGg9iveBMeYP/FvItwGnMTI+K9emkNhp6+el\\nN/3CTbTd6MLZhNXuTBrQKl1cwC/1OITKZvHQ1XYYcjlG9SmSJUI0CxTtFx0MYOfI\\nCgtMvNRnm2snxC8py67Ah2I6u1E3z+L9pDgzdtb4PQKBgQDpu/Ds7zhY9vjQjXXc\\nRQePnUcSr90hfziMCvlzw4XUFsgA//RnldgkKj6A9ZXUeBu+EkTg7jwlvFSEJTDQ\\n0xjdeiW9g8WEk/mrR3xx/UOUlh19ZEAF5ZM03njpA4QmFsMcm0Ys9N662pWAuxVk\\nkyc+4SXA8E8D+tzEmijSkSOTXQKBgQDmOwedfMe7qK+pue4WTCWolJi7QopfYY2O\\nXt7CZl6p7kRM5dJm0/arzHHO38hNWGXLbs6mjGBmlUzocggg2LdqoCDFbG4f5Hnk\\njMfTt8rB+4zzwryBbgzIq0WkzKAhwqAy5TwlUJvnf5HxAFF8sihoKutQS0ObTym0\\nuZljQevgVwKBgQCfGWEbxpIybJpdS41IjGxjI4m3fKInJPQWz3mRhSvZfFBT7eYt\\nhGAF7gNxwLlmUQOBU+oyubeTcCiPcslRu/+W8ogyAjo2zosKPbTF2sFfqPcV3WF/\\nIhAb0ru20L/pNWFGXaNdAqmBwzw0ziP3u/rg4FzUgj8m1GHmKZFUE5uFuQKBgEpv\\nyW2Eu3sT9f+VKsIwyngv2xWoVN6zxrXCPJlzyaV9cL3ADH/MhlNTXXR6a+N98iOM\\n5ul4N27TVLNOswwTpXGm5hGOlihfkX1weucb5EVFlHP3gUUDlMSIg5LdyeRAbdUm\\nwoR6BH4bg878wFvlqiTLw5sJSol7Vculuk8aMC4vAoGATMB9t8Ecg4rwY46A5Hy1\\nFUEfGJLrtcah4d4fpU9iTLxd6kb8jAV2ALv71c4x9rHu8ixIY3SIwrXCyaZXORjo\\nmxY3aWwYnRqt98dVjnsr7CccJ3ECCesnVNvR45aMIfoQkLTisInWl/pDvqJiI3fd\\nxUSHYsyBbyQrnU0BHaPUuSI=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-wkta8@iconnect-1b66d.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"104134911654763499424\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-wkta8%40iconnect-1b66d.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";

            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(firebaseMessagingScope);
            googleCredentials.refresh();


            return googleCredentials.getAccessToken().getTokenValue();
        }catch (Exception e) {
        Log.e("AccessToken", "getAccessToken failed: " + e.getMessage(), e);
        return null;
    }
    }
}