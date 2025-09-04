# EasyRev

<img width="601" height="499" alt="image" src="https://github.com/user-attachments/assets/d701e273-b4c0-46b0-8acb-713d27fb9c19" />

## Solve
Download the provided file named 'solve.py'

Opening the file, we can see that it's a script which runs through a few layers of encryption and the output being a jumbled-up, jibberish string of characters.

Analyzing the code, we see that its just a very elaborate way of coding these ciphers:
- ROT13
- ROT47
- Tiny bytewise shifts
- XOR

Based on this, we can compute the encoding steps:
```
ROT13 → ROT47 → (+217 mod 256) → XOR 232 → (−23 mod 256)
```

And since all these encryption methods are reversible, we'll do just that with this simple script:
```python
def rot13(b):  return (b-65+13)%26+65 if 65<=b<=90 else (b-97+13)%26+97 if 97<=b<=122 else b
def rot47(b):  return 33 + ((b-33+47)%94) if 33<=b<=126 else b

def decode(ct):
    data = [ord(c) for c in ct]
    for f in [
        lambda x: (x + 23) % 256,
        lambda x: x ^ 232,
        lambda x: (x - 217) % 256,
        rot47,
        rot13,
    ]:
        data = [f(x) for x in data]
    return ''.join(map(chr, data))

print(decode("áãÌÛâÞ»¾¶ºâ¾Êâà¼Ê½ß¼Ê¹ÞÊ½ºÊÌ¹å¸"))
```
Running this script returns us the flag:
```
Flag = sunctf25{1t5_th3_4g3_0f_41_n0w}
```
