#Dummy Data

---

##Introduction
In the final round of the BAT’s Cyber-Traditional Battle 2024, there were 3 questions and each team received one of the three following questions to solve overnight: Dummy Data, Colorful Design, and B-A-T cipher. My team, nas1g3puk, received Dummy Data, which I managed to solve. These questions were to be solved in 12 hours overnight and each question was worth 80 points as they were all under the ‘hard’ difficulty but, the first team to solve their question would receive 100 points.
My leader received a message which contained a hint and a link that was directed to a google form which contained a .zip file of the challenge:
![Screenshot 2025-03-23 032024](https://github.com/user-attachments/assets/15e412d0-d4a1-4b34-afd1-c97d10ed9ebb)

First, after downloading the zip file provided we find 2 files inside to be extracted:
![Screenshot 2025-03-23 032159](https://github.com/user-attachments/assets/3a72fad1-6608-4da3-a2f0-ab12329708f7)

---

##Solve
Now we have these two files ‘Decryption.java’ and ‘dummy data.hprof’. Since it was my first time participating in a CTF competition, I had no clue for a lot of the earlier questions, let alone what a .hprof file was. So, I had to do some searching on google and stumbled onto a Now I knew that it was a java memory dump file, I had to find a proper tool to open the file and analyze it. After some searching I decided to go with Memory Analyzer (MAT) created by the Eclipse Foundation.
Before analyzing the .hprof file knowing I had 0 knowledge in Java memory dump analysis, I investigated the Decryption.java code first to see if I can pick out hints:

###Code
```java
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
```
---

Right off the bat, we can see that it is a script to decrypt the variable ‘encryptedSecret’. I notice that it is also missing 2 other variables ‘Key’ and ‘ivHex’.
Then, we can see on line 20, a decrypt() function is being defined where it’s retrieving some variables and processing ‘keyBytes’ and ‘ivBytes’ into byte arrays which are to be used for a key of some sort based on the variable name and IV (initialization vector).
Below that, we see that in line 24, it is setting up an AES key using the ‘keyBytes’ variable in CBC mode with PKCS5 padding, then it uses the key and IV to initialize the cipher for decryption.
After that, we see the hexStringtoByteArray() that is a helper method which converts the key, IV, and encrypted text from hex to bytes.
Now let’s sum up the information that we have gotten:
* Missing Variables ‘Key’ and ‘ivHex’
* AES format key (16 bytes [32 hex characters] or 32 bytes [64 hex characters])
* IV (16 bytes [32 hex characters] for AES)
* Both Key and ivHex are in Hexadecimal format

Then, I launched the Memory Analyzer to analyze the .hprof file:
![image](https://github.com/user-attachments/assets/46efc37d-ccbb-4d72-baf9-a557eb596fe8)

Seeing this UI for the very first time was immensely overwhelming. But the first thing I did was find steps and guides on how to use the analyzer.
I searched the internet on how to use the Memory Analyzer and found the Realizing that to filter the data from the memory heap was to use Query Language like SQL, but for Java memory dump files it’s Object Query Language.
After this, I knew I had to input a specific command to filter for my data.
On the top of the UI, below the ‘dummy data.hprof’ tab, click on the OQL icon to access the OQL studio to type in our commands: 
![image](https://github.com/user-attachments/assets/4c592c2c-aa52-48c9-afcc-829c408b06c5)

![image](https://github.com/user-attachments/assets/b2d83ae9-2a63-44e4-bd0d-72e39d574274)

Now that I’m here my issue was that I had to come up/search for a query command to list out the data I was looking for and I did not know how to do so, so I head back to google to ask.
At first, silly me thought that the data could’ve been within the packages that were being imported in the beginning of the java script:
![image](https://github.com/user-attachments/assets/a025f775-f22e-47df-86e4-1b8e9f7c216c)

And because I was very lost, I tried seeking help from some AI tools like ChatGPT to come up with a query command for me to use and it gave me the following:
```sql
SELECT * FROM java.lang.String s WHERE s.toString().contains("Spec")
```
This query essentially retrieves all object instances that match: 
* Java.lang.String class > refer each one as ‘s’
* Each object is to be filtered by the String data type containing “Spec” as the substring.

![image](https://github.com/user-attachments/assets/d4263e37-7805-4285-b3eb-c29e77b50976)

After retrieving the filtered objects, I saw javax.crypto.spec.SecretKeySpec and javax.crypto.spec.IvParameterSpec thinking that the missing variable data was in it. Just to remember that this is filtering by individual objects and not like standard code.
So now, I had to get a new query line for filtering. As I was thinking, I remembered that the Key and IvHex variables are in hexadecimal format from the code. Also, the key was an AES encryption format so it had to be 16 or 32 bytes (32 or 64 hex characters). Similarly, since the IV is used for AES it had to be 16 bytes long).

Then, I searched around on the commands to filter hex in object query language and eventually came up with this:
```sql
SELECT * FROM java.lang.String s WHERE s.toString().matches("[0-9a-fA-F]{32}") OR s.toString().matches("[0-9a-fA-F]{64}")
```

Let’s break down this line of query command. The first half is similar to the command used earlier that ChatGPT gave me and after s.toString() I used .matches instead of .contains because we’re trying to match the number of characters of the string. ‘[0-9a-fA-F]’ is the format to filter by hex and the following {x} is the amount of characters in the hexadecimal string. Since we don’t know what type of AES encryption it is (either AES-128 or AES-256) we had to filter by 32 and 64-hexadecimal characters. Since we know that the IV is 32 characters long it is also included in this line of filtering.
![image](https://github.com/user-attachments/assets/5dd013e7-30dc-4404-b2b4-2ac6be993fc0)

After running the command, we get a bunch of results, 31 to be exact. But if we look closely it seems that only the first 2 results matches what we’re looking for and the rest looking like error values, padding or uninitialized data.
We copy the first two results by its value and we have this:
```
b7315f2f7f25a8a33c991e1bb32bfd97bfc5d5abf0f8aebf14fa575cb65a222f
68747470733a2f2f64726976652e676f
```

As we know by now the IV is always 16 bytes (32 hex characters) long so we can tell which string belongs to which variable. So let’s head back to the Decryption script and add our newly found strings:
![image](https://github.com/user-attachments/assets/8feeaea3-586a-443a-8d2a-07a4b533499d)

Now that we have restored our missing variables, we can finally run the script and we get the following:
![image](https://github.com/user-attachments/assets/87896043-3aac-4768-9fd1-04d5544a8ac5)
“?1\_/%??<?▲+??ogle.com/drive/folders/10duyyMEJ5GvWiYWPR1abnDko2YbgkI2T?usp=sharing”

The output seems to be a Google Drive folder but the first few texts are messed up so we’ll have to manually alter it and we end up with:
[drive.google.com/drive/folders/10duyyMEJ5GvWiYWPR1abnDko2YbgkI2T?usp=sharing](drive.google.com/drive/folders/10duyyMEJ5GvWiYWPR1abnDko2YbgkI2T?usp=sharing)

Plug that into our search engine and we find a zip file named ‘the\_flag’:
![image](https://github.com/user-attachments/assets/c4db98bd-030c-4a83-8cac-34fc11768e14)

After downloading the zip, open it and we see this:
![image](https://github.com/user-attachments/assets/a3271e88-f136-4e6e-9aaa-34bf1487d0b7)

Following by order shown on the folder name, I opened the first one and inside were a bunch of folders that lead to more folders and eventually leading to a .txt file named ‘1st flag.txt’
![image](https://github.com/user-attachments/assets/ca93e9b9-c4d4-4ebe-a7b4-c3a8785e005d)

Extracting this required a password which we do not have so ignoring it we open the other two folders and ended up with the same thing:
![image](https://github.com/user-attachments/assets/55080e01-8872-40b0-aa78-0091d9687d2b)
![image](https://github.com/user-attachments/assets/b524616b-1298-400c-98fb-4b2d4f9815c6)

Except, this time there were different folder names like ‘1’, ‘8’ and Cap; x, = and ‘?’.
Similar to the 1st folder, we needed a password to extract the .txt files which made me think that the .txt file is not actually the flag but a hint to parts of the flag (?).
At the same time, I decided to jot down the address bar and ended up with these:
```
the\_flag.zip\the\_flag\1- 8\20\20\16\19\4\18\9\22\5\7\15\15\7\12\5\3\15\13\4\18\9\22
the\_flag.zip\the\_flag\2- 5\6\15\12\4\5\18\19\`1`\Cap; 17\Cap; 2\Cap; 19\Cap; 24\Cap; 13\Cap; 6\Cap; 2\Cap; 13\14\Cap; 12\20\\_\16\`8`\11\26
the\_flag.zip\the\_flag\3- 15\13\Cap; 26\Cap; 21\5\`9`\Cap; 13\Cap 5\Cap; 5\Cap; 19\5\Cap; 25\24\Cap; 25\`3`\`3`\8
```

Observing the pattern, especially on no.1, I tried using a1z26 to decipher it and it worked! For the 1st part, I got this: 
‘httpsdrivegooglecomdriv’

Then looking at no.2 and no.3, I assumed that the folders with “Cap” might be short for Capital or uppercase format and the folder names that are `x` are that specific character/plaintext itself with no ciphers. So I attempted it on both and got these:
‘efolders1QBSXMFBMnLt\_p8kz’ | ‘omZUe9MESeYxY33h’

After putting it together we get:
‘httpsdrivegooglecomdrivefolders1QBSXMFBMnLt\_p8kzomZUe9MESeYxY33h’
Obviously, it’s missing some url formatters so I had to fix it myself and ended up with the [link](https://drive.google.com/drive/folders/1QBSXMFBMnLt_p8kzomZUe9MESeYxY33h) and there were multiple contents within:
![image](https://github.com/user-attachments/assets/29085222-6858-45d3-bd1e-7b1b72f4f38b)

There were 7 different zip files. But if we refer to the message we got in the beginning, we can find that we received a ‘first’ hint along with a password. Download number\_6.zip we find a .png file but it’s password locked. Entering the password that was given in the hints we are able to extract the .png file and we get a QR code:
![image](https://github.com/user-attachments/assets/689731e7-0cda-46b9-bb7e-fadf466b8f05)

Scan the QR code and we get this link: 
Enter the link and we get our “Flag”
Flag = 
![image](https://github.com/user-attachments/assets/92469698-9917-4393-ac81-5c94f2d78d07)

(CTF event was based around Malaysia’s Traditional Culture hence the name of the event)
