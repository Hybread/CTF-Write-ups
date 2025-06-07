# Beginner Reverse 6 
![image](https://github.com/user-attachments/assets/566a8d32-36a6-4791-b26b-c835edcc9eb5)

## Solve
Open the source code file and read through it:
```C
#include <stdio.h>
#include <string.h>
#include <ctype.h>

int checkPassword(char* pass){
	size_t length = strlen(pass);
	if(length != 17){
		return 0;
	}
	if(pass[0] != 'R'){
		return 0;
	}
	if(pass[1] - pass[0] != -31 || pass[1] != pass[3]){
		return 0;
	}
	if(pass[4] != tolower(pass[0]) || pass[2] - pass[4] != 4){
		return 0;
	}
	if(pass[5] != '5' || pass[5] - pass[6] != 4){
		return 0;
	}
	if(pass[7] != pass[0] + 28 || pass[2] - pass[8] != 47){
		return 0;
	}
	if(pass[9] != '_' || pass[12] != pass[9] || strncmp(pass+13,"Fun!",4) != 0 || strncmp(pass+10,"1s",2) != 0){
		return 0;
	}
	return 1;
}


int main () {
	char password[20];
	printf("Enter password: ");
	scanf("%19s",password);
	if (checkPassword(password))
	{
		printf("Welcome admin!\nFlag: SKR{%s}",password);
	}else{
		printf("Login failed!");
	}
}
```
We can see that the checkPassword() function has a bunch of ‘if’ loops and a whole lot of sequences/conditions within them. Let’s run them through one by one. 

First, we should write down what we know just from a brief scanning through the code. Given that the password’s length must be 17 bytes and the ‘pass’ variable is in an array format let’s note the given characters down matching their indexes: 

![image](https://github.com/user-attachments/assets/6406d797-1fd8-4906-96ce-053ed968361d)

The 3rd condition states that pass[1] - pass[0] = -31 and pass[1] must match pass[3]. With –31 being such a big number, we’re going to assume that its either a ASCII Decimal or Hex translation. Let’s begin the conversions into hex and decimal to figure out which one is it:
```
- pass[1] = -31 + 52 = 21 but in hex, 21 doesn’t translate into anything. Let’s try Decimal conversion instead: pass[1] = -31 + 82 = 51. 51 converted from decimal to characters is ‘3’ 
- pass[4] != tolower(pass[0]) || pass[2] - pass[4] != 4. 
```
Tolower() function converts all characters into lowercase characters. With ‘r’ for pass[4] let’s satisfy the next condition: 
```
- pass[2] = 4 + r = 4 + 114 = 118.118 converted is a lowercase ‘v’. 
- pass[5] != '5' || pass[5] - pass[6] != 4. 
- Sub ‘5’ for pass[5] it into the next equation. 
- -pass[6] =  4 – pass[5] = 4 – 5 = -1. (Cancel out both negatives) pass[6] = 1. 
- pass[7] != pass[0] + 28 || pass[2] - pass[8] != 47. 
- pass[7] = pass[0] + 28 = 82 + 28 = 110 ≈ ‘n’ (converted) 
- -pass[8] = 47 – pass[2] = 47 – 118 = -71. (Cancel out both negatives) pass[8] = G 
- On the final loop all the characters are given with pass[12] = pass[9] of ‘_’ 
```
Now let’s put it all together: 

![image](https://github.com/user-attachments/assets/9c22417a-dc6e-49a2-ad54-d966c52353c3)

And we get "R3v3r51nG_1s_Fun!"

Access the web shell and put in the command given in the question and type in out password and we’ll retrieve our flag. 
```
Flag = SKR{R3v3r51nG_1s_Fun!} 
```
Written solve:

![image](https://github.com/user-attachments/assets/7df1be23-a3ee-41e4-81af-f3505c50228f)
