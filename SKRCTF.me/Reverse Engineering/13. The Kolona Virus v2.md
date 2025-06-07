# The Kolona Virus V2 
![image](https://github.com/user-attachments/assets/72dc7dad-ae4f-457b-81d5-36751ed831c6)

## Solve
Download the zip file given and extract all files 

We find that one of the files is a compiled python file containing bytecode ‘spread_kolona.pyc’. Knowing this, we have to decompile it into a source code. To do that, we need to use the decompiler pycdc. 

After decompiling the code with pycdc, we obtain the source code and paste it into a .py file. 

Reading through the .py file and modifying some things like the exec evolve_virus to print(evolve_virus), we run the program. As the program runs we will run into our first error: 

![image](https://github.com/user-attachments/assets/285c9cfa-96b6-4b92-9920-6eb0f39dfad3)

Instead of using the ‘r’ read to read the contents of a file, let’s make it read the file in binary with the function ‘rb’. After fixing the code we the program and we get a new set of codes: 

![image](https://github.com/user-attachments/assets/674e7a6c-ae86-4dc0-9f84-1f6531a72cd8)
 
Copy the code given and put it into another .py file. Again, the function ‘r’ for evolved_virus needs to be changed to ‘rb’ as reading it normally will not work. After modifications, run the program again to get another set of code: 

![image](https://github.com/user-attachments/assets/da536bb9-6708-458f-ac08-0e60aeeab1f4) 

Copy and put it into another .py file. Here we see that this is the final file to retrieve the original corrupted .png file. We will need to tweak the code a little for it to run properly as some lines are missing. 

After the tweaking of the code, we run the program and it will generate the png file with the proper file signature. Open the png file and we get our flag 
```
Flag = SKR{W3_will_W1N!!}
```
