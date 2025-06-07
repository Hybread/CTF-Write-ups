# Beginner Reverse 4 
![image](https://github.com/user-attachments/assets/10612fea-4246-44f3-8733-7c3357901cc4)

## Solve
Open the provided source code file and read through the code:
```C
#include <stdio.h>
#include <string.h>

int checkPassword(char* pass){
	size_t length = strlen(pass);
	if(length != 15){
		return 0;
	}
	char* part1 = "Spr3ue45";
	for (int i = 0, j = 0; i < length; i+=2,j++){
		if(pass[i] != part1[j]){
			return 0;
		}
	}

	char* part2 = "5PrcS3u";
	for (int i = length-2, j = 0; i > 0; i-=2,j++){
		if(pass[i] != part2[j]){
			return 0;
		}
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
In the checkPassword() function, there are 3 checks being conducted. The first being that the password the user enters HAS to be 15 bytes long. Then part1 and part2, with part1 being the checking of the characters “Spr3ue45” in an EVEN index manner as mentioned in the code for i < length, i+=2. Visually: 

![image](https://github.com/user-attachments/assets/f04ccc99-8f82-4944-86d7-ac139a9cb781)

As for part2 it checks for the characters “5PrcS3u” in an ODD index manner and in reverse order as the loop starts with for i = length – 2, i-=2. Visually: 

![image](https://github.com/user-attachments/assets/024e4b47-ff33-40bd-a886-b6f5155ea470)

Now, if we put them into an array table, as well as following their index numbers, we will get this sequence of text: 

![image](https://github.com/user-attachments/assets/b72af21f-14f6-49a7-b0d4-d722559b8edc)

Type it out, run the program and enter the password and we will get our flag 
```
Flag = SKR{Sup3rS3cureP455}
```
