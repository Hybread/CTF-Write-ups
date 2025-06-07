# Beginner Reverse 3 
![image](https://github.com/user-attachments/assets/4176f9ef-057c-47f2-be60-cb11de2a42a4)

## Solve
Open the provided source code file and read through the code:
```C
#include <stdio.h>
#include <string.h>

int checkPassword(char* pass){
	if(strlen(pass) != 14){
		return 0;
	}
	if(strncmp(pass+2,"cur3", 4) != 0){
		return 0;
	}
	if(strncmp(pass,"S3", 2) != 0){
		return 0;
	}
	if(strncmp(pass+10,"w0rd", 4) != 0){
		return 0;
	}
	if(strncmp(pass+6,"Pa$$", 4) != 0){
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
We can see at the beginning of the code theres a checkPassword() function. It first detects whether the user’s input which is being saved as an array is equal to 14 bytes long. If it is, it will begin the checking and if not, it will reject the input and the program will end. 

When the program begins the checking, the first loop shows 'pass+2' meaning it’s checking for the array block of the input pass[2] to pass[5] and comparing it to “cur3”. If correct, it will check for pass[0] to pass[1] and comparing it to “S3”. Then pass[10] to pass[13], comparing it with “w0rd” and finally, pass[6] to pass[9] for “Pa$$". 

Following the sequence and rearranging it, we can come up with the password: S3cur3Pa$$w0rd 
Run the program and enter it and we will receive our flag. 
```
Flag = SKR{S3cur3Pa$$w0rd}
```
