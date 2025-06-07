# nogard 3 
![image](https://github.com/user-attachments/assets/62f4ca9e-5acc-4666-b3e2-4dbe116c1d66)

## Solve
Download the provided file and we’ll run it through a decompiler (I used IDA). 

After running in IDA we head to the main function and click ‘F5’ to get the decompiled code: 

![image](https://github.com/user-attachments/assets/a54b9107-07db-46fa-9219-db669ce9ba81)

Here in the if() function we can see that the program is taking a user input of 4 different values separated by a dash ‘-’ and checking the values of the variables ‘v4, v5, v6, and v7’ with some equations related to it. 

We’ll have to reverse the equation and after doing so we get v4 = 307, v5 = 1984, v6 = 3377, v7 = 823. 

Knowing that these 4 values are separated by dashes it will look like:  

‘133-1984-3377-823' 

But, in the scanf() function, we see that it is taking the user input as a hexadecimal as it is ‘%x’. So we have to convert our decimal values into hex. 

After doing so we get the following: v4 = 307 = 0x133, v5 = 1984 = 0x7c0, v6 = 3377 = 0xd31, v7 = 0x337. 

Final output: 133-7c0-d31-337 

Plug that into the program, and we get our flag: 

![image](https://github.com/user-attachments/assets/531ad67d-2aeb-4b08-9dcb-e8b44b87297e)

```
Flag = SKR{1337c0d31337}
```
