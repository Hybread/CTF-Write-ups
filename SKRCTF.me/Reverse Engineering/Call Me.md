# Call Me 
![image](https://github.com/user-attachments/assets/fdd26897-3714-494a-8d7e-9a9700a0cdf5)

## Solve
Download the provided file ‘call_me’ and when we run the file the output of the program is just “Call Me!” 

![image](https://github.com/user-attachments/assets/711fa8c9-5100-4ab9-99f0-dc4e60825ece)

In the hints it mentioned the utilization of GDB. So we’ll run the file through GDB on our Kali VM. 

In the GDB CLI, we run the command ‘info functions’ to list out all the functions that exist in the program to find a possible functions to call: 

![image](https://github.com/user-attachments/assets/ea92f5bc-2110-4d6f-a752-1b61e4b0c6b8)

Here we have the main function and the call_me function let’s try to call it. 

We first set a breakpoint in the main function and run it. During the runtime, we will use the command ‘jump call_me’ and we get our flag. 
```
Flag = SKR{C4LL_1nst3@d_0f_p4tCH}
```
