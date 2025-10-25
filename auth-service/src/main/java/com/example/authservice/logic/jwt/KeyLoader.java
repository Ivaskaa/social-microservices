package com.example.authservice.logic.jwt;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyLoader {

    public static PrivateKey loadPrivateKey(InputStream inputStream) throws Exception {
        String key = new String(inputStream.readAllBytes())
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] bytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    public static PublicKey loadPublicKey(InputStream inputStream) throws Exception {
        String key = new String(inputStream.readAllBytes())
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] bytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}
