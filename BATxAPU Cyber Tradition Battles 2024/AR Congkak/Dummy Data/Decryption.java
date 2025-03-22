import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Decryption {

    public static void main(String[] args) throws InterruptedException {
        String encryptedSecret = "fc34fa4110356538528a3e29e60750d95135fd78919550401e38afa407bb5a8349a869454f85676afb54c18a106ae003b1c6cb2e8207de4e081b80a0b7b0bee21da1100a026112b37788874a058cceaa6d6f1d4e1407c21e9d3c7e878c0631b4";

        try {
            String decryptedPassword = decrypt(encryptedSecret, Key, ivHex);
            System.out.println("..................................................................................................................................................................................." + decryptedPassword);
        } catch (Exception e) {
            System.out.println("Decryption failed: " + e.getMessage());
        }

        Thread.sleep(5000);
    }

    public static String decrypt(String encryptedHex, String hexKey, String ivHex) throws Exception {
        byte[] keyBytes = hexStringToByteArray(hexKey);
        byte[] ivBytes = hexStringToByteArray(ivHex);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] encryptedBytes = hexStringToByteArray(encryptedHex);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}

