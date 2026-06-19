# Donut Calculator

## Solve
Standard rev procedure, analyze the binary in Detect-It-Easy. DiE shows a packed section in .data. We can see the offset of the packed section as well.

<img width="553" height="109" alt="image" src="https://github.com/user-attachments/assets/45de6af1-c7dd-485f-8157-19df5e45ba46" />

<img width="672" height="379" alt="image" src="https://github.com/user-attachments/assets/4615bf94-963f-4283-a246-b0fe1a267322" />

In IDA, the program is wrapped by `winmain` so tracing into the actual main function `WndProc`, we're able to see the program logic:

<img width="418" height="530" alt="image" src="https://github.com/user-attachments/assets/368381a3-647a-4593-be5f-a106e9bcb52b" />

Seems like a standard calculator program, but if we go back in the WinMain wrapper, we can see a Hidden function being called:

<img width="749" height="354" alt="image" src="https://github.com/user-attachments/assets/9d407a8e-75d8-49ab-a7dd-8cddb6fe3680" />

We can immediately see a hashing function Hash_smtg runs it through some values. Looking into the hashing routine, we understand that it's a MARU hash resolver to decrypt the values we saw into windows API calls:

```bash
  380894834  = CreateProcessA
  1847235996 = VirtualAllocEx
  3627903649 = WriteProcessMemory
  1755826130 = GetThreadContext
  3903309779 = SetThreadContext
  2655666056 = ResumeThread
```

Going down, we can see that it's creating a child process of Notepad.exe in a suspended state, indicating that this malware is utilizing a technique called process hollowing.

We then see the process writing with `v9` having 4 arguments `v9(hObject[0], v5, &number, 152521, 0);` being passed, this is most likely the encrypted payload that we will dump:

<img width="909" height="443" alt="image" src="https://github.com/user-attachments/assets/0c9fdaf6-b160-449b-b77a-720f5f694a8c" />

Examining the blob at &number, we identify the Donut instance header at offset `0x05`. The first 4 bytes are the instance length `(0x21EBD)`, followed by the 16-byte Chaskey key (mk) at `0x09` and the 16-byte counter/IV (ctr) at `0x19`. Let's dump this with a script. 

***<I'm too lazy to post the script, but you can ask AI to make one that carves this section out with the proper offset and key + IV in place>***

<img width="489" height="101" alt="image" src="https://github.com/user-attachments/assets/ac5b0b63-c2b5-4132-b30d-6ce34272803a" />

Open the new binary in IDA, we find a main function `nice()` immediately and inside, we find some XOR routines to decrypt encrypted array in both `good[]` and `bad[]` using the hardcoded repeating xor keys as shown:

<img width="630" height="384" alt="image" src="https://github.com/user-attachments/assets/ddbad47a-5ec1-43be-8f35-024e1dd6eb8c" />

Reverse the logic and decrypt flag with a simple script.
<Just copy this section, the array values in good[] and bad[], and send it to AI and ask it to make a decrypt script> 

`Flag = UMCS{Ap1_HaSH1n5_Pr0c3ss_Ha11owInG_D0nuT_sHellcode}`
