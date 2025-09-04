# Resemblance

<img width="594" height="364" alt="image" src="https://github.com/user-attachments/assets/4bc108e8-f157-468e-87f7-5dfd14c3bde3" />

## Solve
Download the two given files 'challenge.py' and 'out.txt' to inspect the contents.

Inside the source code, we can deduce that the script uses ChaCha20, a stream cipher with a single key and single 'nonce' (number used once) for two encryptions:
- 1 Long story message
- The flag

The output file then contains iv, enc_msg, and enc_flag.

---
The vulnerability lies within the ChaCha20 encryption method itself, where Cipher Streams produce a keystream that's XOR'ed with a plaintext:
- enc_msg = Message ⊕ keystream
- enc_flag = Flag ⊕ keystream

Reusing the same key and nonce on two different plaintexts means it reuses the same keystream which is a known two-time pad issue.
If any plaintext is known, anyone could recover keystream and then decrypt the other, which is what we can find in the source code itself:
```python
message = b"The streets of New Eridu "
message += b"hummed with neon life, untouched by the chaos of the Hollows. Proxies whispered through back alleys, "
message += b"chasing commissions that bordered on myth and madness. One night, "
message += b"a Hollow surged open near Sixth Street... "
```

From this message, we can compute the following by simply reversing the encryption process:
- keystream = Message ⊕ enc_msg
- Flag = enc_flag ⊕ keystream = enc_flag ⊕ (Message ⊕ enc_msg)

With this, we'll create a script to reverse the encryption process, thus decrypting the provided enc_flag:
```python
import pathlib, re, sys

def givenMsg(chal_path="challenge.py"):
    txt = pathlib.Path(chal_path).read_text(encoding="utf-8", errors="ignore")
    parts = re.findall(r'message\s*(?:\+?=)\s*b"([^"]*)"', txt)
    if parts:
        return b"".join(s.encode() for s in parts)
    m = re.search(r'message\s*=\s*b"""(.*?)"""', txt, flags=re.S)
    if m:
        return m.group(1).encode()
    raise ValueError("Couldn't auto-extract plaintext message from challenge.py")

def main(out_file="out.txt", chal_file="challenge.py"):
    lines = pathlib.Path(out_file).read_text().strip().splitlines()
    if len(lines) != 3:
        raise SystemExit("Error")
    iv_hex, enc_msg_hex, enc_flag_hex = lines

    enc_msg  = bytes.fromhex(enc_msg_hex)
    enc_flag = bytes.fromhex(enc_flag_hex)

    try:
        message = givenMsg(chal_file)
    except Exception:
        # fallback to hard-written msg if parsing fails from reading source file
        message = (
            b"The streets of New Eridu "
            b"hummed with neon life, untouched by the chaos of the Hollows. Proxies whispered through back alleys, "
            b"chasing commissions that bordered on myth and madness. One night, "
            b"a Hollow surged open near Sixth Street... "
        )

    keystream = bytes(m ^ c for m, c in zip(message, enc_msg))

    flag = bytes(c ^ keystream[i] for i, c in enumerate(enc_flag))
    print(flag.decode("utf-8"))

if __name__ == "__main__":
    main()
```
Running this script will give us the decoded flag:
```
Flag = sunctf25{m4yb3_s0m3_k3y_d1ff3r3nc3_1snt_s0_b4d_4ft3r4LL}
```
