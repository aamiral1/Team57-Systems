package main.misc;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.security.MessageDigest;
import java.util.Arrays;
import java.security.SecureRandom;

public class Encryption {

    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    // encrypt method
    public static String encrypt(String data, String password) throws Exception {
        SecretKeySpec key = createKey(password);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
        byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // decrypt method
    public static String decrypt(String encryptedData, String password) throws Exception {
        SecretKeySpec key = createKey(password);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(original, "UTF-8");
    }

    // generate user salt
    public static String generateSalt(){
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        // convert salt to base64 string for easier storage
        String saltString = Base64.getEncoder().encodeToString(salt);

        return saltString;
    }

    private static SecretKeySpec createKey(String password) throws Exception {
        byte[] key = password.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }

    public static void main(String[] args){
        // String originalData = "pass";
        // String password = User.salt;

        // try {
        //     String encryptedData = encrypt(originalData, password);
        //     System.out.println("Encrypted Data: " + encryptedData);

        //     String decryptedData = decrypt(encryptedData, password);
        //     System.out.println("Decrypted Data: " + decryptedData);

        //     // Check if the decrypted data matches the original
        //     System.out.println("Does the decrypted data match the original? " + originalData.equals(decryptedData));
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }
}

