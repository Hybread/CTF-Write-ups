# BOH Ransomware Official Writeup

<img width="872" height="1009" alt="image" src="https://github.com/user-attachments/assets/a5944731-3156-4ee9-8338-ca0dd5983aa8" />

## Introduction
BOH Ransomware is a hard reversing challenge. The executable file provided to the user includes a malware-like trait which has anti-debugging code and anti-antivirus checks implemented into it to prevent dynamic analysis. 
To solve, the user will have to reverse engineer the provided ‘ransomware’ windows executable, written in Nim. By analyzing the code & embedded virtual machine, they can create a script to emulate the VM to execute bytecode stored inside injector.bin to retrieve the AES key.
After obtaining the key, the user has to understand how the IV and Ciphertext is stored inside the encrypted image, then the user can write a python or Nim script to decrypt MasterKey.png.BoH using AES-CTR. 
