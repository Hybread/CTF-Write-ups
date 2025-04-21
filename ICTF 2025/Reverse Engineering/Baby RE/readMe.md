# Baby RE
---
## Solve
Download the given python source code file and open it.
We this code:
```python
from secret import key,plaintext
def custom_encrypt(plaintext):
    ciphertext = []
    for i, char in enumerate(plaintext):
        shifted = ord(char) + i
        reversed_bits = int(format(shifted, '08b')[::-1], 2)
        encrypted_char = reversed_bits ^ key
        ciphertext.append(encrypted_char)
    return bytes(ciphertext)


encrypted = custom_encrypt(plaintext)
print("Encrypted:", encrypted.hex())

# hehehe this is Fun and Easy, good luck 😏
# Encrypted: 6cdc946c92a27fe88208f008823c70ecdc10302c5c2060c06c40804c7f8054d454bf9f9f9f9f2fe44f240f4f7704d8b718183798e7486767a8a83008f0b00830103be05b202babdd
```
---
This is basic Reverse Engineering, all we have to do is reverse the encryption function and include the ciphertext and we'll get our flag. 
Basically, how this encryption script works is that:
- For each character in the ciphertext, an index(i) is added to its ASCII value (first character adds 0, second adds 1, etc.)
- Then, it is converted to 8-bit binary using format('08b').
- Bits of the binary string is reversed. (::-1)
- Convert back to integer using int(..., 2).
- Finally, it is XOR'ed with the secret key using "^ key".

This is the script I came up with:
```python
cipherText = "6cdc946c92a27fe88208f008823c70ecdc10302c5c2060c06c40804c7f8054d454bf9f9f9f9f2fe44f240f4f7704d8b718183798e7486767a8a83008f0b00830103be05b202babdd"
cipher = bytes.fromhex(cipherText)

def bitsReversal(b):
    return int(f"{b:08b}"[::-1], 2)

def decrypt(ciphertext, key):
    plaintext = []
    for i, byte in enumerate(ciphertext):
        x = byte ^ key
        shifted = bitsReversal(x)
        orig = shifted - i
        if 0 <= orig <= 0x10FFFF:
            try:
                plaintext.append(chr(orig))
            except ValueError:
                return None
        else:
            return None
    return ''.join(plaintext)

for key in range(256):
    pt = decrypt(cipher, key)
    if pt and pt.startswith("ICTF25{"):
        print(f"Key: {key}")
        print(f"Flag = {pt}")
        break
```


---
Because we don't know what the exact key is, we'll have to add a brute forcing function to try every possible number from 0-256 to see which key ends up giving us a decrypted flag that includes "ICTF25{".
Run this script and we get our flag:

![image](https://github.com/user-attachments/assets/0400173c-abc2-4fe4-8d7a-2ce624d5e44d)

```
Flag = ICTF25{a6ffd26c94fa81fce1dd2ea755adcbae1e2ebe26c76a3d8cb219445147b6b7fd}
```
