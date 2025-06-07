# nogard 2 
![image](https://github.com/user-attachments/assets/8e7836a7-fe1b-4cef-a161-2e661eed36e5)

## Solve
Download the provided file and analyse it with Ghidra or/and IDA (I used both). 
Looking at the code in the Ghidra’s decompiler window, we can identify that it’s not binary patching due to it not being an authenticator. As well as the given hint of the question is XOR. So let’s decompile the code and export it to a readable language. 
I used IDA to extract the source code in this manner: 
```
- Search for the String value then press “Shift + F12” to view the strings of the binary.  
- Double click on the found string, and it will bring you to the code part of the string.  
- Next, while hovering over the variable name, click ‘x’ to jump to where the string was used.  
- Then click ‘F5’ while hovering over the assembly code to view it in a readable language/source code then extract it: 
```

![image](https://github.com/user-attachments/assets/f397e554-7abe-4471-adf6-470b27ac4787)

Analysing the code we see that there’s are two variable ‘part1’ and ‘enc’ we need to extract the flag. 
We use Ghidra and analyze the memory tree. After snooping around we find: 

![image](https://github.com/user-attachments/assets/0991a925-a96a-4152-9bba-cd6ca3853f91)
 
![image](https://github.com/user-attachments/assets/9bcbd8f5-368e-4e7e-afe1-fe4cabb238dd)

Double click into those values and it will bring us to the variable’s string values: 

![image](https://github.com/user-attachments/assets/e6b3d11b-0174-45e5-9cc8-4763ce1bed5c)

Now we have enc: 3B735C3E4A1E3963h and part1: 46526F587B524B53h, we analyze the code and it’s XOR ciphering again. We see that the “first key”, is v5 and part1 = v5. Next, we have v6 ^ v5 = enc, which means we need to find v6 = enc ^ v5 as XOR is symmetrical. 

To solve for the XOR operation, we create a script to solve it for us in hexadecimal format and we get: }!3f1Lr0 which looks like a part of the flag. 

Going back to part1 we take that hex value and plug it into a decipher tool (dcode/cyberchef) and we get: FRoX{RKS 

With these two values retrieved we can assume just by looking at it that it’s in reverse and if we flip them around and combine both we get our flag: 
```
Flag = SKR{XoRF0rL1f3!}
```
