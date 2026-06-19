Varsity Lake Sonata
DiE shows nothing much except UPX packed binary:

<img width="363" height="81" alt="image" src="https://github.com/user-attachments/assets/aacf3f50-8b67-4c28-98aa-5010a4d88945" />

UPX doesn’t work for some reason, so we open the binary in hexedit and with some basic reverse knowledge, we understand that these region should be UPX headers not UMCS: `0x00ec` `0x078d` `0x0f9a` `0x1908` `0x1910`

<img width="611" height="432" alt="image" src="https://github.com/user-attachments/assets/61574545-d9ee-40d9-8408-0af2b44c9929" />

Instead of the boring manual unpacking methods, as well as ELF binaries being a pain in the ass, we’ll try a more fun approach. 
Since the packed regions HAS to unpack for the CPU to understand its instructions, we’ll simply execute the binary first and let it unpack itself then rip it out directly. 

To do this you’ll need two terminals:

<img width="1000" height="392" alt="image" src="https://github.com/user-attachments/assets/ebea1129-3d37-403b-9582-700ca3464eb5" />

Now we can analyze the binary in IDA. 
We immediately find a dynamic XOR routine iterating through 5 hardcoded values. 
Reverse that with a simple script (just copy the function and paste into ai and ask it to make us a script) and we get the flag:

<img width="567" height="312" alt="image" src="https://github.com/user-attachments/assets/628e1763-a563-4dfb-b09d-31403f1c50c8" />

`Flag = UMCS{t4sik_v4rs1ty_pi4n0_x0r_m3l0dy}`
