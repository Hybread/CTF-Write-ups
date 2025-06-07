# Ular Challenge 2 
![image](https://github.com/user-attachments/assets/289c63a6-7e6e-40e6-9750-94fc6faa2b7a)

## Solve
Open the source code provided and analyze the code:
```Python
#!/usr/bin/env python3
import sys
a=lambda a,b:a+b
b=lambda b,c:a(b,-c)
c=lambda c:b(c,-c)
p=list(input("Enter passcode: ").encode())
if(len(p)==8):
	if(a(p[0],p[1])==c(52) and b(p[1],p[0])==-2):
		if(a(p[2],p[3])-b(p[3],p[2])==a(int(chr(p[0])+chr(p[1])),45) and c(p[2])+c(p[3])==b(-1141,-1337)):
			if(a(c(p[4]),c(p[5]))==c(108) and b(c(p[5]),c(p[4]))==-12):
				if(b(a(p[6],p[7]),b(p[6],p[7]))==108 and b(c(b(p[7],p[6])),a(p[7],p[6]))==-111):
					print("Correct passcode! Flag is SKR{%s}"%bytes(p).decode())
					sys.exit()
print("Wrong passcode!")
```
After analyzing we find that it’s a bunch of if loops with some conditions. There is also the use of ‘lambda’ function. We also know that the password’s length is 8 bytes. 

Now, break down each loop and solve accordingly (It's just mathematics, so I did a paper solve): 

![image](https://github.com/user-attachments/assets/6c5b540e-8221-4887-96e7-a8efdd55474c)

![image](https://github.com/user-attachments/assets/62925fbd-3e4d-4e1a-ad3e-88a504d09f21)

```
Flag = SKR{53119376}
```
