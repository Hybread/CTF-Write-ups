# Tzip

<img width="589" height="391" alt="image" src="https://github.com/user-attachments/assets/52a12882-31fa-49c0-9891-d60e7e140413" />

## Solve
Download the provided files being 'tzip.exe' and 'flag.tz'.

As standard reversing procedure, we run 'strings' on the tzip.exe to see if the executable has been compressed/packed with any packers at all. After doing so, we find nothing particular,
aside from a list of imports which tells us that we're working with the Microsoft CryptoAPI library:

<img width="302" height="180" alt="image" src="https://github.com/user-attachments/assets/81b3196d-f749-41e3-a846-ef6dbdfaa565" />

Let's move to a decompiler, we'll be using IDA this time around.

Right off the bat in the entry point, we see these code:

<img width="1016" height="618" alt="image" src="https://github.com/user-attachments/assets/2bb6ec02-e3a9-4c05-90e6-2e24b31e7e33" />

From this, and running the .exe on a terminal as well, we can identify this as an encryption program where it takes a .txt file as an argument then encrypts it, and outputs a .tz file.

We can deduce that the given flag.tz is the encrypted flag dumped out by the program, so most likely, we'll have to look into the program's decompiled code, find the encrypt function,
reverse it and we'll be able to decrypt the flag file. So let's do exactly that.

From the entry point, if we scroll down, we can see the encryption process of the a .txt file. Here's a snippet of the code:

<img width="1042" height="607" alt="image" src="https://github.com/user-attachments/assets/c37540e2-c506-4608-a329-c470252cb971" />

Here we can point out something interesting, as in the printed message within the if condition, it mentions 'Failed to write seed data'. If we inspect it, we can notice that in this
particular line:
```
std::ostream::write((std::ostream *)v47, v55, 32);
```
A seed of 32 bytes is being written, then dumped into the buffer `v55` which is stored in the beginning of the .tz file. After this part it's just the rest of the encryption process and
fail handling. 

Scrolling back up a little, in the first few lines of this `encryptFile` function, we can see a `deriveKey` function being called, as well as a createAesKeyBlob which hints that
we're working with an AES encryption. We can also see that the buffer `v55` is being called as an argument. Within, the function itself isn't that big containing around ~270 lines.

<img width="1062" height="496" alt="image" src="https://github.com/user-attachments/assets/605f66bd-b47b-45c6-b766-7f0d982f872e" />

Jumping into the function, we see this interesting part of the code:
```
  if ( (unsigned int)generateRandomBytes(a1, 0x20u) )
  {
    v4 = *((_QWORD *)a1 + 1);
    *(_QWORD *)&deriveKey(unsigned char *,int)::key = *(_QWORD *)a1;
    qword_140009068 = v4;
    v5 = *((_QWORD *)a1 + 3);
    qword_140009070 = *((_QWORD *)a1 + 2);
    qword_140009078 = v5;
    for ( i = 0; i < a2; ++i )
    {
      if ( !(unsigned int)computeHash(
                            &deriveKey(unsigned char *,int)::key,
                            0x20u,
                            &deriveKey(unsigned char *,int)::key,
                            &v6) )
        return 0;
    }
```
From this, we understand that it's a SHA-256 as generateRandomBytes(a1, 0x20u) = a1 being the pointer variable to the buffer `v55`, is being filled with 32 random bytes (0x20 translated), 
then those bytes are copied into a buffer, which after, runs a XOR encryption. We can piece those lines back into readable code which becomes:
```C
for (i = 0; i < a2; ++i)
    computeHash(key, 0x20, key, &v6);
```
This here is an iterative hashing loop where a2 = 32 which means the seed is passed through `computeHash` 32 times where each iteration of the hashing is replacing the actual
AES key with the SHA-256 digest, hence the key being derived from the seed, which we are able to see in the `computeHash` function:

<img width="649" height="289" alt="image" src="https://github.com/user-attachments/assets/31973b39-dc67-4a3d-b3b8-2f3faf1996be" />

Now we know how the key is formed, we need to look for what type of AES encryption is it using. Heading back to the `encryptFile` function, right below the `createAesKeyBlob` function, 
we can see `CryptSetKeyParam` is called after the key has been imported by `CryptImportKey`:
```
if ( CryptImportKey(...
{
  *(_DWORD *)v52 = 4;
  if ( CryptSetKeyParam(*v22, 4u, v52, 0) )   <-- this 
```

<img width="845" height="262" alt="image" src="https://github.com/user-attachments/assets/26feb949-50af-497e-87ce-2182b177ed68" />


<img width="920" height="110" alt="image" src="https://github.com/user-attachments/assets/bb504a97-8626-4300-ae08-b941871fdded" />

Here the 2nd argument `4u` is the value of constant KP_Mode parameter (referencing decompiled imports shown in image above). Then, the 3rd argument of `CryptSetKeyParam` defines 
the mode that's used by the AES encryption, that being the buffer `v52` which was set to 4 just before the import where 4 = CRYPT_MODE_CFB in CryptoAPI.

And since there were no KP_MODE_BITS called, the AES-CFB mode would default to 8-bits = CFB8. Finally, there is also no IV being called either, the IV would default to 16 0's.

With all that information, we can finally craft a script with this flow to extract the seed and AES key, then decrypting the flag.tz file:
- Open .tz file > read header (for the seed) > split 32-byte seed and ciphertext `file[0:32]` > derive AES key from seed > cipher parameters (AES-256, CFB mode, default IV) > decrypt
```python
import sys, hashlib, argparse

def keyDerive(seed: bytes) -> bytes:
    k = seed
    for _ in range(32):
        k = hashlib.sha256(k).digest()
    return k  # 32 bytes

def decrypt(in_path: str, out_path: str = None, show_params: bool = False) -> bytes:
    with open(in_path, "rb") as f:
        data = f.read()
    if len(data) < 32:
        raise ValueError("File too short to contain seed + ciphertext.")
    seed, ct = data[:32], data[32:]
    key = keyDerive(seed)
    iv = b"\x00" * 16

    print("== tzip params ==")
    print(f"seed : {seed.hex()}")
    print(f"key  : {key.hex()}")
    print(f"iv   : {iv.hex()}")

    backend = None
    pt = None
    try:
        from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
        from cryptography.hazmat.backends import default_backend
        cipher = Cipher(algorithms.AES(key), modes.CFB8(iv), backend=default_backend())
        dec = cipher.decryptor()
        pt = dec.update(ct) + dec.finalize()
        backend = "cryptography"
    except Exception:
        try:
            from Crypto.Cipher import AES
            cipher = AES.new(key, AES.MODE_CFB, iv=iv, segment_size=8)
            pt = cipher.decrypt(ct)
            backend = "pycryptodome"
        except Exception as e:
            raise RuntimeError("No AES backend available (cryptography or PyCryptodome).") from e

    if out_path:
        with open(out_path, "wb") as f:
            f.write(pt)
    return pt

def main():
    ap = argparse.ArgumentParser(description="Decrypt .tz files produced by tzip.exe")
    ap.add_argument("input", help="flag.tz (or any .tz)")
    ap.add_argument("-o", "--output", help="Output plaintext path (optional)")
    args = ap.parse_args()

    pt = decrypt(args.input, args.output, show_params=args)
    if not args.output:
        try:
            print(pt.decode("utf-8"), end="")
        except UnicodeDecodeError:
            sys.stdout.buffer.write(pt)

if __name__ == "__main__":
    main()
```
Running this script, we'll be able to retrieve the flag:
```
== tzip params ==
seed : 4a6cb627db19f0a2784609ce76dcdde1745b38efe18eaf542ace043993a185fe
key  : 44712099d95c341e9a71c89319cf9f31b723fb10a52331a1a580f4b4d6e8f985
iv   : 00000000000000000000000000000000

Flag = sunctf25{435-cfb_f1l3_3ncryp710n_700l}
```
