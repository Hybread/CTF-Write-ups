# HORUS

## Solve
DiE shows packed but no specific known open-source or commercial packer:

<img width="851" height="194" alt="image" src="https://github.com/user-attachments/assets/b40dc722-b0da-4235-98d6-69737c4c2aff" />

Check the `Extractor` Tab to find that there's a GZIP section with a machO binary(?):

<img width="883" height="191" alt="image" src="https://github.com/user-attachments/assets/9544706a-3d4e-4ac2-a669-33139b9a1672" />

Section 2 of `.rdata` is the part that's super packed and it’s almost maxed the entropy scale.
Looking in IDA, the binary's stripped but we can look for meaningful strings, which we do find and it leads us to the main function:

<img width="1036" height="428" alt="image" src="https://github.com/user-attachments/assets/f4fc8a5e-ec1c-42d2-8eff-652c3c410aba" />

From the top, we can see the dropper logic with a RC4 KSA + RC4 PRGA routine below:

<img width="715" height="531" alt="image" src="https://github.com/user-attachments/assets/8c0f23da-289d-4260-8033-186c02d12f98" />

We analyze this `Buffer` variable on the stack which has the length of 8 bytes long. By checking the xref, we can see that a QWORD gets loaded from the rip register. This xref brings us to the obvious hardcoded key 
`0xBEBAFECAEFBEADDEuLL` (DE AD BE EF CA FE BA BE) that we could see in the dropper routine earlier.

<img width="868" height="72" alt="image" src="https://github.com/user-attachments/assets/1f186f64-3b0d-4c16-9073-5a8b2a5eb15c" />

Now that we have all the info necessary, we can decrypt the payload with a python script (gotta love ai) using the key we found: 

```python
def stage1(path: str) -> bytes:
    print(f"[*] Stage 1: loading {path}")
    pe = pefile.PE(path)
    imagebase = pe.OPTIONAL_HEADER.ImageBase

    rdata, rdata_va = find_rdata(pe)
    print(f"    .rdata size: {hex(len(rdata))}, entropy ≈ 8.0 (fully encrypted)")

    KEY_OFFSET   = 0x5BCD90   # offset in .rdata
    PAYLOAD_OFF  = 0xD0       # offset of where payload is stored
    PAYLOAD_SIZE = 0x5BCC00   # VirtualAlloc call

    key_bytes = rdata[KEY_OFFSET : KEY_OFFSET + 8]
    print(f"    RC4 key ({len(key_bytes)} bytes): {key_bytes.hex()}")
    assert key_bytes == b"\xde\xad\xbe\xef\xca\xfe\xba\xbe", "Unexpected key"

    encrypted = rdata[PAYLOAD_OFF : PAYLOAD_OFF + PAYLOAD_SIZE]
    print(f"    Decrypting {hex(PAYLOAD_SIZE)} bytes with RC4...")
    decrypted = rc4(key_bytes, encrypted)

    assert decrypted[:2] == b"MZ", "Decrypted payload is not a PE!"
    print(f"    [+] Got valid PE (MZ): .NET assembly")
   
    out_path = path.rsplit('.', 1)[0] + '_decrypted.exe'
    with open(out_path, 'wb') as f:
        f.write(decrypted)
    print(f"    [+] Dumped decrypted .NET PE -> {out_path}")
    return decrypted
```

After decrypting and dumping, we can again use DiE on the dumped software and we see that this time, it's a .NET binary, again showing that it's packed. Let's look at it in dnSpy:

<img width="603" height="372" alt="image" src="https://github.com/user-attachments/assets/321018f4-ef47-43df-97f7-89302ad6db26" />

Interesting name for the classes but based on the logic of each class, we can point out that Question opens a pop up window with yes and no with a question. HAHA class flashes a window with "YOU ARE AN IDIOT" and plays an mp3 audio + picturebox:

<img width="1049" height="504" alt="image" src="https://github.com/user-attachments/assets/3f6a47ac-e324-430b-b295-dd6e68fbb719" />

We can see that the program is calling for a `GetMD5()` and `HexToBytes()` function and compares it to a hardcoded `expectedMD5`, and if match, it will pass to the decryption function:

<img width="560" height="335" alt="image" src="https://github.com/user-attachments/assets/8e71cab7-260a-4d92-bf9f-b023587a8bf2" />

Trace the calls and find the hardcoded keys, then reverse the logic to decrypt the payload: 

<img width="727" height="81" alt="image" src="https://github.com/user-attachments/assets/3672986f-a93a-4149-be66-61b4fc633928" />

```python
import sys
import hashlib

CIPHER_HEX   = "037d91c01b45e9db9546f62ecf8601657a7848f04f3391d118783b227741a081e2ba87dcc2c4f0c45ab3285e248c4211c72da33be3a39e"
EXPECTED_MD5 = "c1a708f3e14e36e388ec2f75d04ceff2"

KEY = b"\xde\xad\xbe\xef\xca\xfe\xba\xbe"

def rc4(key: bytes, data: bytes) -> bytes:
    S = list(range(256))
    j = 0
    for i in range(256):
        j = (j + S[i] + key[i % len(key)]) & 0xFF
        S[i], S[j] = S[j], S[i]
    i = j = 0
    out = bytearray(len(data))
    for k in range(len(data)):
        i = (i + 1) & 0xFF
        j = (j + S[i]) & 0xFF
        S[i], S[j] = S[j], S[i]
        out[k] = data[k] ^ S[(S[i] + S[j]) & 0xFF]
    return bytes(out)

def stage2():
    print(f"[*] Key (hex)    : {KEY.hex()}")

    md5 = hashlib.md5(KEY).hexdigest()
    print(f"[*] MD5(key)     : {md5}")
    print(f"[*] Expected MD5 : {EXPECTED_MD5}")

    if md5 != EXPECTED_MD5:
        sys.exit("[-] MD5 mismatch - wrong key")

    print("[+] Key validated")

    # Decrypt
    ciphertext = bytes.fromhex(CIPHER_HEX)
    flag = rc4(KEY, ciphertext).decode()

    print(f"\n{'='*55}")
    print(f"  FLAG: {flag}")
    print(f"{'='*55}")

if __name__ == "__main__":
    stage2()
```

`Flag = UMCS{h3110_3v3ry-ny4n,_h0w_r_u_f1n3_7h4nk y0u,_0m4ig4d}`
