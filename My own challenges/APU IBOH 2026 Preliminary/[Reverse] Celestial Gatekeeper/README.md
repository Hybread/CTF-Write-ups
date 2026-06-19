# Celestial Gatekeeper Official Writeup 

## Introduction
Celestial Gatekeeper is a hard reversing challenge which involves a 32-bit crackme executable which implements x64-bit code utilizing the Heaven’s Gate technique (WoW64)  
The user must go through the broken looking decompiled code, identify the Heaven’s Gate calls, which they can then find each function’s real instructions dynamically.
