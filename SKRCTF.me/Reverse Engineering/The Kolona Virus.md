# The Kolona Virus 
![image](https://github.com/user-attachments/assets/6cfd6024-725d-4024-b7a4-715e13b25785)

## Solve
Download the zip file and the standalone file provided. 
Extract the contents of the zip and we find 3 files inside it kolona_virus, MN908947, and spread_kolona.py. 
In the hint given it said to print the virus. So within the code, we modify the exec(kolona_virus) to print() instead and after running the program we get this line of new code: 

![image](https://github.com/user-attachments/assets/a827ed81-547c-43d9-992b-01906ecfb095)

We copy and paste that code into a new python file. 
From the code itself we can see that the original jpg file was XOR-encrypted with the key= “COVID-19”. The for loop below is there to read the encrypted file then decrypt the XOR-encryption and write the binary data onto the flag.jpg using the key. But when we run this code, we encounter some issues: 

![image](https://github.com/user-attachments/assets/8d5d5fb4-be1a-44d8-8591-1ca092bf799c)

After finding for some clues we find that we’re not able to use ‘r’ or ‘w’ on a .JPG without errors. So we must tweak the code a little to make it work and after tweaking, we run the program and it will generate us the .jpg file and we get our flag 
```
Flag = SKR{V1rus_1s_3verywhere_pl3453_st4y_4t_H0me}
```
