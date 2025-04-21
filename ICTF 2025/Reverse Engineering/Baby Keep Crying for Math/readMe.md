# Baby Crying for Math
## Solve
First, download the .zip file given, and extract the content which is a windows executable file:

![image](https://github.com/user-attachments/assets/8b8bab36-7bd3-4f9e-84dc-ce7ac08f8100)

If we run the program, it prompts to enter a flag:

![image](https://github.com/user-attachments/assets/70ba7793-cd91-4efa-bfa6-60061493fc2c)

After entering a dummy input, the program just closes.

---
Since the challenge name implies that there is math involved, let's put it into a decompiler to see if we can find any 'math' in the program. I'm using IDA here because the decompiled code is easier to understand compared to Ghidra/BinaryNinja.

First we start by decomipiling the main function (binaries are stripped but IDA is able to find the entry point which is the first page we get):

![image](https://github.com/user-attachments/assets/0578d39c-b657-4225-a2c8-69cfcd8b5110)

We don't particularly see any math in here, but there is a check_flag() function. Click into it and we see a very long math equation:

![image](https://github.com/user-attachments/assets/f90f245f-915c-4577-8c5b-8af97ae48472)

I plugged it into VSC so that it's more pleasing to my eyes:
```C
_BOOL8 __fastcall check_flag(__int64 a1)
{
  int i; // [rsp+28h] [rbp-8h]
  int v3; // [rsp+2Ch] [rbp-4h]

  v3 = 0;
  for ( i = 7; i <= 21; ++i )
    v3 += *(char *)(i + a1);
  printf("secret: %d\n", v3);
  if ( strlen((const char *)a1) != 23 )
    return 0;
  if ( strncmp((const char *)a1, "ICTF25{", 7u) || *(_BYTE *)(a1 + 22) != 125 )
    return 0;
  if ( v3 != 1573 )
    return 0;
  if ( *(char *)(a1 + 7) * *(char *)(a1 + 8) != 10504 )
    return 0;
  if ( *(char *)(a1 + 7) * *(char *)(a1 + 10) != 10504 )
    return 0;
  if ( *(char *)(a1 + 9) * *(char *)(a1 + 10) != 10504 )
    return 0;
  if ( *(char *)(a1 + 9) * *(char *)(a1 + 12) != 12168 )
    return 0;
  if ( *(char *)(a1 + 11) * *(char *)(a1 + 12) != 11115 )
    return 0;
  if ( *(char *)(a1 + 13) * *(char *)(a1 + 14) != 9690 )
    return 0;
  if ( *(char *)(a1 + 13) * *(char *)(a1 + 16) != 11115 )
    return 0;
  if ( *(char *)(a1 + 15) * *(char *)(a1 + 16) != 12987 )
    return 0;
  if ( *(char *)(a1 + 17) * *(char *)(a1 + 18) != 11000 )
    return 0;
  if ( *(char *)(a1 + 17) * *(char *)(a1 + 20) == 11550 )
    return *(char *)(a1 + 19) * *(char *)(a1 + 20) == 9975;
  return 0;
}
```
---
Simplifying how this function and equations work:
- strlen(flag) == 23 = flagArraySize[22]
- strncmp(flag, "ICTF25{", 7) == 0 | flag[22] == '}' meaning let s = flag[7:22] (15 chars inside the flag braces)
- Equation simplification: *(char *)(a1 + 7) * *(char *)(a1 + 8) != 10504 == **s[0] * s[1] = 10504**

Since none of the variable values are given, there are two ways to solve the equations.
1. Trial and error factorization: 10504 = 104 * 101 => **s[0] = 104 = 'h' | s[1] = 101 = 'e'**
2. Easier way, just use a python script lol:
   ```Python
   from itertools import product

    ascii_range = range(33, 127)
    solutions = []
    for a, b in product(ascii_range, repeat=2):
    if a * b != 10504:
        continue
    c = a  
    d = b  
    if c * d != 10504:
        continue

    for f in ascii_range:
        if c * f != 12168:
            continue
        e = 11115 // f
        if e * f != 11115 or e not in ascii_range:
            continue

        for g, h in product(ascii_range, repeat=2):
            if g * h != 9690:
                continue

            for j in ascii_range:
                if g * j != 11115:
                    continue

                for i in ascii_range:
                    if i * j != 12987:
                        continue

                    for k, l in product(ascii_range, repeat=2):
                        if k * l != 11000:
                            continue

                        for m, n in product(ascii_range, repeat=2):
                            if k * n != 11550 or m * n != 9975:
                                continue

                            chars = [a, b, c, d, e, f, g, h, i, j, k, l, m, n]
                            current_sum = sum(chars)

                            o = 1573 - current_sum
                            if o not in ascii_range:
                                continue

                            inner = ''.join(chr(x) for x in chars + [o])
                            flag = f"ICTF25{{{inner}}}"
                            solutions.append(flag)
                            print(f"Flag: {flag}")
                            break
   ```
---
Run the script or solve it manually, we get the flag:

![image](https://github.com/user-attachments/assets/d398e164-edfa-44e5-98b7-25d7261baca2)
---
```
Flag = ICTF25{hehe_u_found_it}
```
