# TicTacToe
## Solve
---
First we'll download the given file named 'game'

Then, we'll put it in our kali to analyse the details of the file:

![image](https://github.com/user-attachments/assets/3ab60b1f-7702-4cb1-a5d0-3f85bfb7cc83)
![image](https://github.com/user-attachments/assets/3d4eae9d-b954-4ada-98bb-ceefcc24ea03)
---
We find that it is a standard ELF file with stripped binaries. Meaning it'll be slightly harder to analyse as the symbol table is removed.

Let's run the file and see what get:

![image](https://github.com/user-attachments/assets/eb2fc568-7394-4115-b7ec-8f3263380fa8)
![image](https://github.com/user-attachments/assets/98923710-35ae-4fd7-814f-5be220bc229b)
---
It seems like a standard tic-tac-toe game set with a 2d array block. But when winning it just shows that we won and the amount of times won.

With this information, let's analyse it in Ghidra for reversing purposes:

![image](https://github.com/user-attachments/assets/c845de57-7dce-4a6c-8664-5cfc9d4ca4b0)
---
As the main function hidden because of the binary stripping, we're not going to manually find it. Instead, we'll use the strings window and jump to the selected string in the disassembly window to check the xreference:

![image](https://github.com/user-attachments/assets/2a03226d-de86-4c49-867e-7e6597d8042e)
![image](https://github.com/user-attachments/assets/5e0f303f-9b54-48c7-a7e9-34850b35fb6f)
---
After jumping to the xreference, in the decompiled window we can see the code for program:

![image](https://github.com/user-attachments/assets/618df4d4-9f61-44a1-a476-76f04b885dd7)

Scrolling down a bit we can see the function that checks for user win/loss:

![image](https://github.com/user-attachments/assets/24873dde-7791-4505-9d27-af15989e0d3a)
---
Here's the code we need to patch:
```
    if (local_c == 1000000) {
      FUN_00101f63();
      goto LAB_001021fa;
    }
```
It shows that if local_c == 1000000 meaning if the user wins a million times, a new function will be called. Click on the function and we see that within the function, there's a line that displays the flag:
```
  FUN_00101d36(&local_48,&local_88,0x2a,&DAT_00105080,&local_94,1);
  printf("Here\'s the flag : %s ",&local_88);
  return 0;
```
To bypass the if function, we'll just patch the JNZ instruction to JZ:

![image](https://github.com/user-attachments/assets/dcaf92a5-9d09-4e42-a81b-e4488627c437)
![image](https://github.com/user-attachments/assets/13c696df-be01-4d94-9ffa-20a4eb92e19d)
---
After patching, we can see that in the printf(); line, that the (ulong)local_c variable which is the counter for wins, is changed to 1000000 meaning we just have to go through the program loop once before receiving our flag. Save the file, import to kali and run the program again and we'll get our flag:

![image](https://github.com/user-attachments/assets/81cbc86e-41a0-4ec0-b978-a969a1874e55)
---
```
Flag = ICTF25{c0ngr47s_0n_s0lv1nG_7hiS_cH4lL3nGe}
```
