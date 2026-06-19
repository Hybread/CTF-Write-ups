# Solve

<img width="537" height="455" alt="image" src="https://github.com/user-attachments/assets/f2fed99b-b2fe-42ea-a4d4-70c97f9cf22b" />

Running the given executable, we are greeted with a license checker. Inputting a dummy input gives us an error popup: 

<img width="388" height="203" alt="image" src="https://github.com/user-attachments/assets/694d8346-fb3e-42c6-ace9-b44e75da2b5a" />

Analyzing this executable in Detect-It-Easy (DiE), we can see that it’s been compiled with pyinstaller: 

<img width="612" height="421" alt="image" src="https://github.com/user-attachments/assets/fccf6795-c7b6-436b-99d3-1956ca899277" />

We can simply reverse this with the `pyinstxtractor` tool: 

<img width="653" height="223" alt="image" src="https://github.com/user-attachments/assets/4e1695ae-1314-4965-a46b-1eb7fd4da665" />

Dump the .pyc into pylingual.io and we find that the source code has been obfuscated with pyarmor: 

<img width="496" height="85" alt="image" src="https://github.com/user-attachments/assets/f69b38c3-5ae5-4da9-bca3-9bc30704c1e9" />

Annoying. Regardless, let’s just try our best to extract the source code from the pyarmor obfuscation. To do so, we can use `Process Hacker` alongside `Pyinjector` to spawn a python shell. 
We’ll need to do some manual enumeration to find the main frame of the program: 

`import dis;dis.dis(list(sys._current_frames().values())[1].f_code)` 

<img width="814" height="227" alt="image" src="https://github.com/user-attachments/assets/2e79c8d5-dde6-4a51-9297-41c83c273a90" />

Here, we find a Tkinter GUI frame loop. But heading back one frame, we’re able to see the actual main frame. 

Looking at it, most of the ‘main’ parts of the code isn’t encrypted and we can technically just analyze the instructions here: 

<img width="878" height="453" alt="image" src="https://github.com/user-attachments/assets/c8b2caf8-e909-4cc0-bb4b-48447210f454" />

Alternatively, we can decrypt the pyarmor modules and dumping the contents into a .pyc file: 

<img width="543" height="520" alt="image" src="https://github.com/user-attachments/assets/2ca52422-cb0e-41b1-a27b-50e6f7db8647" />

`exec(open(r"Solve.py").read())` 

<img width="791" height="485" alt="image" src="https://github.com/user-attachments/assets/330dbcd8-cac0-48a0-88f0-d01c4ae3aa1b" />

It's just a tad bit obfuscated but, we can still analyze the logic of the code to find that it’s pretty much useless. The main serial logic lies behind the `bread.dll`: 

<img width="376" height="78" alt="image" src="https://github.com/user-attachments/assets/d608fd25-474c-4e69-b8cd-f5509bb60896" />

<img width="228" height="64" alt="image" src="https://github.com/user-attachments/assets/82bc5441-e531-4d98-ba88-aa5146b8b488" />

Let’s drop this in DiE to see if we can find any additional info before analyzing in IDA: 

<img width="676" height="407" alt="image" src="https://github.com/user-attachments/assets/344f66d0-d0a4-47d3-be99-20f9739bb343" />

Seems like it’s packed with UPX. We can simply unpack this with the automated UPX command: 

<img width="602" height="148" alt="image" src="https://github.com/user-attachments/assets/f27537e1-f6f0-4a24-8c29-fb36a857e401" />

Now let’s utilize IDA to decompile and debug the DLL file: 

<img width="833" height="359" alt="image" src="https://github.com/user-attachments/assets/0c261192-85f4-400a-8c4a-9777ea9a747f" />

From here, we can simply set a breakpoint on the `validate_serial` function, then set the process options as such: 
- Application: <path\to\python.exe> 
- Parameters: Dump.py 

<img width="932" height="251" alt="image" src="https://github.com/user-attachments/assets/fc9b68d0-0bfe-426d-8562-f9c370fc2c1e" />

After setting the breakpoint, we load the DLL then continue until the program runs. Then after entering any input, the breakpoint will hit and we can start our analysis. 

This program essentially validates a license based on the format of `xxxx-xxxx-xxxx-xxxx`. The validation computes a hash from the username, requires Block 3 to be the XOR'd hash as hex, linking Blocks 1 and 4 to the hash, derives Block 2 from cross-block modular equations: 

## Serial Validation Algorithm

a. Username:
- Hash username using rotating XOR with golden ratio multiplier (0x9E3779B1) 
- Rotation amount: ((index % 5) * 7) % 32 
- Extract 16-bit hash: user_hash = (acc >> 16) & 0xFFFF 
- Count consonants (non-vowel letters) 

b. Block Conversions:
- Each block character converted to base-36 value (0-35) 
- Block value: val = digit[0]*46656 + digit[1]*1296 + digit[2]*36 + digit[3] 

c. Validation Constraints:
1. Block 3 Check:  
- B3 (as hex) == (user_hash ^ 0x3C3C) 

2. Main Constraint (20-bit):  
- (val_b1 + val_b4) & 0xFFFFF == (7 * user_hash) & 0xFFFFF 

3. Cross-Block Dependencies:  
- b2[0] == (b3[1] + b1[2]) % 36 
- b2[3] == (b4[0] ^ username_length) % 36 
- (b4[1] + b4[2]) % 64 == (b1[0] + b2[2]) % 64 
- (b4[3] * b2[1] + b1[3]) % 43 == (b3[0] + b3[1] + b3[2] + b3[3]) % 43 

4. Constraints:  
- If B4[0] is digit: use value directly, else: (value ^ 7) % 31 
- Must equal: (username_length + sum(b1_values)) % 31 
- (b4[1] + b4[2]) < 64 AND == ((b1[0] ^ b2[3]) & 0x3F)

5. Digit Balance:  
- (digit_count_in_serial % 5) == (consonant_count % 5) 

---

With this info, we can craft a script using a constraint solver like Z3 to simultaneously satisfy all the modular equations: 
```python
import string 
import random 

ALPHABET = string.digits + string.ascii_uppercase 
VAL_MAP = {ch: idx for idx, ch in enumerate(ALPHABET)} 

def hash_v2(username): 
    acc = 0 
    for idx, char in enumerate(username): 
        byte = ord(char) 
        rot = (idx % 5) * 7 
        acc ^= (byte << rot) | (byte >> (32 - rot)) 
        acc = (acc * 0x9E3779B1) & 0xFFFFFFFF 
    return (acc >> 16) & 0xFFFF 

def count_consonants(username): 
    vowels = set('AEIOU') 
    return sum(1 for c in username.upper() if c.isalpha() and c not in vowels) 

def val_to_base36(val): 
    result = [] 
    for _ in range(4): 
        result.append(ALPHABET[val % 36]) 
        val //= 36 
    return ''.join(reversed(result)) 

def verify_serial(username, serial): 
    try: 
        parts = serial.upper().split('-') 
        if len(parts) != 4: 
            return False 

        B1, B2, B3, B4 = parts 
        if not all(len(p) == 4 for p in parts): 
            return False 

        b1_vals = [VAL_MAP[c] for c in B1] 
        b2_vals = [VAL_MAP[c] for c in B2] 
        b3_vals = [VAL_MAP[c] for c in B3] 
        b4_vals = [VAL_MAP[c] for c in B4] 

        val_b1 = sum(b1_vals[i] * (36 ** (3-i)) for i in range(4)) 
        val_b4 = sum(b4_vals[i] * (36 ** (3-i)) for i in range(4)) 

        user_hash = hash_v2(username) 
        user_len = len(username) 
        consonant_count = count_consonants(username) 
        expected_b3 = user_hash ^ 0x3C3C 
        actual_b3 = int(B3, 16) 

        if actual_b3 != expected_b3: 
            return False 

        target = (7 * user_hash) & 0xFFFFF 
        actual = (val_b1 + val_b4) & 0xFFFFF

        if actual != target: 
            return False 

        expected = (b3_vals[1] + b1_vals[2]) % 36

        if b2_vals[0] != expected: 
            return False 

        expected = (b4_vals[0] ^ user_len) % 36 

        if b2_vals[3] != expected: 
            return False 

        lhs = (b4_vals[1] + b4_vals[2]) % 64 
        rhs = (b1_vals[0] + b2_vals[2]) % 64 
        if lhs != rhs: 
            return False 

        lhs = (b4_vals[3] * b2_vals[1] + b1_vals[3]) % 43 
        rhs = sum(b3_vals) % 43 
        if lhs != rhs: 
            return False 

        sum_b1 = sum(b1_vals) 
        w0 = b4_vals[0] 
        left = w0 if B4[0].isdigit() else ((w0 ^ 7) % 31) 
        right = (user_len + sum_b1) % 31 
        if left != right: 
            return False 

        sum_inner = b4_vals[1] + b4_vals[2]

        if sum_inner >= 64: 
            return False 

        if sum_inner != (b1_vals[0] ^ b2_vals[3]) & 0x3F: 
            return False 

        digit_count = sum(c.isdigit() for c in serial) 

        if digit_count % 5 != consonant_count % 5: 
            return False 

        return True 

    except: 
        return False 

 

def generate_valid_key(username, max_attempts=1000000): 
    user_hash = hash_v2(username) 
    user_len = len(username) 
    consonant_count = count_consonants(username) 
    expected_b3 = user_hash ^ 0x3C3C 
    B3 = f"{expected_b3:04X}" 
    b3_vals = [VAL_MAP[c] for c in B3] 
    target = (7 * user_hash) & 0xFFFFF 

    print(f"Username: {username}") 
    print(f"User hash: 0x{user_hash:04X}") 
    print(f"Block 3 (fixed): {B3}") 
    print(f"Target sum: {target} (0x{target:05X})") 

    attempts = 0 
    while attempts < max_attempts: 
        attempts += 1 
        # Random B1 
        val_b1 = random.randint(0, 36**4 - 1) 
        B1 = val_to_base36(val_b1) 
        b1_vals = [VAL_MAP[c] for c in B1] 
        val_b4_needed = (target - val_b1) & 0xFFFFF

        if val_b4_needed >= 36**4: 
            continue 

        B4 = val_to_base36(val_b4_needed) 
        b4_vals = [VAL_MAP[c] for c in B4] 
        sum_b1 = sum(b1_vals) 
        w0 = b4_vals[0] 
        left = w0 if B4[0].isdigit() else ((w0 ^ 7) % 31) 
        right = (user_len + sum_b1) % 31

        if left != right: 
            continue 

        sum_inner = b4_vals[1] + b4_vals[2]

        if sum_inner >= 64: 
            continue 

        b2_0 = (b3_vals[1] + b1_vals[2]) % 36 
        b2_3 = (b4_vals[0] ^ user_len) % 36 
        target_sum = (b4_vals[1] + b4_vals[2]) % 64 
        needed_sum = (target_sum - b1_vals[0]) % 64 

        if needed_sum >= 36: 
            continue 

        b2_2 = needed_sum 

        if sum_inner != (b1_vals[0] ^ b2_3) & 0x3F: 
            continue 

        target_mod43 = sum(b3_vals) % 43 
        rhs = (target_mod43 - b1_vals[3]) % 43 
        b2_1 = None 

        for candidate in range(36): 
            if (b4_vals[3] * candidate) % 43 == rhs: 
                b2_1 = candidate 
                break 

        if b2_1 is None: 
            continue

        B2 = ''.join([ALPHABET[b2_0], ALPHABET[b2_1], ALPHABET[b2_2], ALPHABET[b2_3]]) 
        serial = f"{B1}-{B2}-{B3}-{B4}"
        digit_count = sum(c.isdigit() for c in serial)

        if digit_count % 5 != consonant_count % 5: 
            continue

        if verify_serial(username, serial): 
            print(f"Found valid key after {attempts:,} attempts!") 
            return serial

        if attempts % 100000 == 0: 
            print(f"Attempts: {attempts:,}...") 

    print(f"Failed to find valid key after {max_attempts:,} attempts") 
    return None 

if __name__ == "__main__": 
    username = "FlagFetcher2026!" 
    valid_key = generate_valid_key(username)

    if valid_key: 
        print(f"VALID SERIAL KEY: {valid_key}") 
        print(f"\nVerification: {verify_serial(username, valid_key)}") 
```

<img width="318" height="177" alt="image" src="https://github.com/user-attachments/assets/b332abc5-9f5b-41d1-bb9a-16aef2321553" />

To double check, we can enter this serial we got into the application GUI: 

<img width="559" height="495" alt="image" src="https://github.com/user-attachments/assets/a7d5b088-4ca6-494b-b4e1-82da29c71fac" />

`Example valid flag = ESMM-T62U-9704-E97E`

`Flag = flag{valid_input}`

 

 
