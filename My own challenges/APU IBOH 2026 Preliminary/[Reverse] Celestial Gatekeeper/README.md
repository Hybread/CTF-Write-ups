# Celestial Gatekeeper Official Writeup 

<img width="640" height="589" alt="image" src="https://github.com/user-attachments/assets/d4176bd4-b357-4ad0-9b7b-bc1488813762" />

## Introduction
Celestial Gatekeeper is a hard reversing challenge which involves a 32-bit crackme executable which implements x64-bit code utilizing the Heaven’s Gate technique (WoW64)  
The user must go through the broken looking decompiled code, identify the Heaven’s Gate calls, which they can then find each function’s real instructions dynamically.
