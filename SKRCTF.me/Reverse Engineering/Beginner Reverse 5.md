Beginner Reverse 5 
![image](https://github.com/user-attachments/assets/9ee99eb5-2148-471a-809f-14e5d05b46ab)

Open the web shell and access the command given
Open and read through the source code given:
```C
#include <stdio.h>
#include <string.h>

int checkPassword(char* pass){
	size_t length = strlen(pass);
	if(length != 14){
		return 0;
	}
	char* correct_pass = "BTDJJ`Qmvt`1o4";
	for (int i = 0; i < length; i++){
		if(pass[i] != correct_pass[i]-1){
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
We can see that in the checkPassword() function, it checks for the user’s input which is converted to the variable ‘correct_pass’. The correct password is given in the ciphertext of “BTDJJ`Qmvt`1o4”. Copy it and run it through a cipher identifier and it will reveal the encryption of ASCII. 
Paste the ciphertext into an ASCII decipher and we will get our password: 
![image](https://github.com/user-attachments/assets/456e981a-ef11-4425-adae-64de081b2547)

Paste it into the program on the web shell and we can successfully login and retrieve the flag. 
```
Flag = SKR{ASCII_Plus_0n3} 
```
