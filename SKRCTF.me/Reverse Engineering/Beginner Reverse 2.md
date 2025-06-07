# Beginner Reverse 2 
![image](https://github.com/user-attachments/assets/566d3506-0aba-4232-bb9e-010126cf628a)

## Solve
Open the provided source code file and read through the code:
```C
#include <stdio.h>

int main () {
	char password[16];
	printf("Enter password: ");
	scanf("%s",password);
	int pass = atoi(password);
	if ((pass*2)-666 == 2008)
	{
		printf("Welcome admin!\nFlag: SKR{%s}",password);
	}else{
		printf("Login failed!");
	}
}
```
We can see that the program takes a user input with the function scanf() and transforms it into the ‘pass’/password array.  

In this line: 
```C
if ((pass*2)-666 == 2008)
```
It shows that the password given by the user is taken, multiplied by 2 and deducted with 666 and then compared with 2008. If the final output matches 2008 it will print “Welcome admin!” and the flag. 

Knowing this let’s reverse the formula given for us to find the value of ‘pass’. 

Formula: pass = 2008 – 666/2 = 1337. Now we take the value and input it into the program, and we will receive our flag. 

```
Flag = SKR{1337} 
```
