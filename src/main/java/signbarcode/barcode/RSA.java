package signbarcode.barcode;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSA {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private static final String PUBLIC_KEY_STRING = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpDX5iyQu2s+HltfSTciif2IVp3PzKNo4T1v3RAMilWyul1twebTyhKhCGm33bP1gd/0mYP3Vp61hzRyFLaLIBhPGkAb68R7AL4T9QuB9J8dQBHCB1fwm4Ea5J8CnQTr9iTXfIEZ5XLhGJA8fbpM6LelqHP5ECzUNT5nGeq6yseQIDAQAB";
    private static final String PRIVATE_KEY_STRING= "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKkNfmLJC7az4eW19JNyKJ/YhWnc/Mo2jhPW/dEAyKVbK6XW3B5tPKEqEIabfds/WB3/SZg/dWnrWHNHIUtosgGE8aQBvrxHsAvhP1C4H0nx1AEcIHV/CbgRrknwKdBOv2JNd8gRnlcuEYkDx9ukzot6Woc/kQLNQ1PmcZ6rrKx5AgMBAAECgYANsDaaiUS+IDjLc20wmYbcZzk+820c8t0UZ60zz3BaYp8OkFl7SuU6IoW7NIYfat9iccGehuLltu/5nkSTTntSk4+Gjqub+qTJzCdH8MdMDKt7GmltbkWZEHaQWHTEKwN1S89M4iGI0Z50fPdslVWwK+T1KhfV7VOxgNQz40xv5QJBAMGTgs+Nkbfeonjwkl/zAVARNH8t1z5m7mewXEIPoPmp5PtRLMf9566g1TV2IVxD5kk6aFMMOapDc7TvSNUhXI0CQQDfkXgx7DaXCIsH6IfFmCpB0oFDqZVasn2JB0bJOp4bRZyua8SplHU3EnbtqSCtL3KT/LRPjCEqm57+SUx/yRKdAkB1tGZ0IhcbAMs2UsIOicqgjvWm547cKOaKMhjHKo2tgfr0PhXGvcMk0jMSwPPkEH10xYkuBl8CAuXWfTGCNzj5AkBZj58E8MnVq1h27JIneBPFlpyuDGuGj+Z2VHHo/xCDgFmKDiYUgSKL01vNTWmHt3BBFITvMQXAWeNRnu+EaXqJAkEAtm52mSLQSvUXwNcYxwbaU3cVodz3xCh5YEI9v+R0UyWUG1gldz35x7kypnWgXCOkov8xi0zKL36s3zacYvMiIA==";

    public RSA(){

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        }catch (Exception e){

        }
    }

    public void initFromStrings() {

        try {
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(PUBLIC_KEY_STRING));
            PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(PRIVATE_KEY_STRING));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            publicKey = keyFactory.generatePublic(keySpecPublic);
            privateKey = keyFactory.generatePrivate(keySpecPrivate);
        }catch (Exception ignored){

        }

    }

    public String encrypt(String message) throws Exception{
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

    private String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    public String decrypt(String encryptedMessage) throws Exception{
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage, "UTF8");
    }

    private byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }

}
