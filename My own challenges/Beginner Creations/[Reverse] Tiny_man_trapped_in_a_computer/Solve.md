# Small Man writeup

## Side Note
The given zip and its content is slightly different from the writeup provided here. This writeup has the version of my published version in my university's annual CTF, whilst the zip content features the original version.

## Solve
Given two text file, `I'm_4_.txt` and `sm4l1_m4n!.txt`, the player can figure out the order of the challenge. Opening both files they find a bunch of sequential decimal numbers with `;` being comments and additional integers provided. Searching around google, they can figure out that it's a bunch of low-level LMC OPCODE instructions *(AI will not say that it's LMC)*. All they have to do now is just follow and trace the instructions through some basic ADD and SUB instructions.

As the player trace the instruction blocks, they will find that for each equation, the first value is always missing but the 2nd value and expected output is given. With some basic math reversal, they will be able to find the value of each input. 

Players may find instruction blocks which have an additional alphabet at the end of them. After finding the missing input, they can match the alphabet together with the input value they got to form a HEX value. By now the user is able to figure out the 2nd layer of encryption.

Step-by-step solve:
```bash

Im_4_.txt
ADD 60: a61 + 174 = 216		⇒ 42    → 'B'
SUB 61: 200 - a62 = 151		⇒ 49    → 'I'
ADD 62: (a63 + 50 = 54)E	⇒ 4E    → 'N'
SUB 63: 100 - a64 = 53		⇒ 47    → 'G'
ADD 64: (a65 + 24 = 28)F	⇒ 4F    → 'O'
SUB 65: 150 - a66 = 107		⇒ 43    → 'C'
ADD 66: a67 + 33 = 87	    ⇒ 54    → 'T'

sm4l1_m4n!.txt
SUB 90: 1337 - a90 = 1291	⇒ 46    → 'F'
ADD 91: (a91 + 95 = 102)B	⇒ 7B    → '{'
SUB 92: 200 - a92 = 126		⇒ 74    → 't'
ADD 93: a93 + 82 = 113		⇒ 31    → '1'
SUB 94: (6 - a94 = 0)E		⇒ 6E    → 'n'
ADD 95: a95 + 10 = 69		⇒ 59    → 'Y'
SUB 96: 12 - a96 = -9		⇒ 21    → '!'
ADD 97: (a95 + 5 = 12)D		⇒ 7D    → '}'

Expected 1st input of each instruction block Hex → ASCII:

42 49 4E 47 4F 43 54 46 7B 74 31 6E 59 21 7D
↓  ↓  ↓  ↓  ↓  ↓  ↓  ↓  ↓  ↓  ↓  ↓  ↓  ↓  ↓
B  I  N  G  O  C  T  F  {  t  1  n  Y  !  }
```

```
Flag = BINGOCTF{t1ny!}
```
