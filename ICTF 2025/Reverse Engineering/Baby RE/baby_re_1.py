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
