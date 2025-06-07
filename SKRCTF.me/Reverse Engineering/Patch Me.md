# Patch Me 
![image](https://github.com/user-attachments/assets/5bb8f41a-7a36-4ba6-b993-c572525b6645)

## Solve
Download the provided file 
Import it into Ghidra as well as a VM to analyze 
In the VM when we run the file it will give us an error saying “You’re not admin, no flag for you...” 
Now we head back to Ghidra and click the ‘s’ key to search. Filter by string and type in the keywords we saw when attempting to run the file: 

![image](https://github.com/user-attachments/assets/137eb718-bb38-4c91-9179-05f8da26454d)

Double click the location and it will jump to that specific location. Next, we need to find the cross-reference where that specific line is being called: 

![image](https://github.com/user-attachments/assets/b8c3a938-6685-43af-906a-01bd8d99705e)

We click on the right green text ‘000011df(*)’ which is the cross-reference for the memory address and it will bring us to the source. 
Then again we see another reference point so we’ll click it again and now we get to the main source of where that line is being called: 

![image](https://github.com/user-attachments/assets/a16a048b-7d33-4a09-9c6e-9d9c179bc5ba)

We can see on the right side the Decompiler it shows the code and how it works. Now, lets patch the binary value. 
We know tha JZ in assembly means “Jump If Zero” and this indicates relative to the code where it Jumps to the error message if isAdmin == 0. What we have to do is modify JZ to JNZ (Jump if NOT Zero). 

![image](https://github.com/user-attachments/assets/5230f990-aebb-4721-b94f-cf7d7867b0f9)

As you can see after we modify the binary data, the code in the decompiler also changes. Now it will print the flag when it DOESN’T detect an admin. 
Export this by clicking ‘o’ and selecting original file type. Import the file into our VM and run the file and we should get the flag. 
```
Flag = SKR{p4tch1nG_ezpz}
```
