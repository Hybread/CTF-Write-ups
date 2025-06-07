# Ular challenge 1 
![image](https://github.com/user-attachments/assets/931393ba-b804-48b9-b06e-47fb178a2404)

## Solve
Open the provided source code file:
```Python
#!/usr/bin/env python3
p = input(" :edocssap eht retnE"[::-1]); print("}s%{RKS si galF !edocssap tcerroC"[::-1] % p if len(p) == 9 and p[1337^1337] == 'D' and p[1**1337] == '3' and p[len('2'*2)] == '4' and p[3] == p[len('')] and p[len('skrr')] == '-' and p[5] == 'B' and p[(len('6'*6)-5)*6] == p[1<<1337>>1337] and p[int(p[6])+4] == p[len(p)-3] and p[len(p[1:])] == 'F' else " ! e d o c s s a p   g n o r W"[::-1][::2])
```
Just by a glance, we can see that most of the code is literally written in reverse. Reformat/restructure the code with "[::1]" slicing to make it readable or just put it into an AI tool to restructure it for you. 

Based on the code the password’s length is 9 and going through the sequence we get D34D-B3?F as the line after password[6] include some calculations. 

Breaking down the first part, we have password(int(password[6]+4] this means password[6] which is ‘3’ + 4 so we get password[7]. Then it’s == password[-3] and this translates to password[len(p)-3] as stated in the comment of the code and that equates to password[6]. Now we have the simplified version which is: 
```Python
password[7] == password[6] 
```
We end up with D34D-B33F and if we plug that into the program, we get our flag. 
```
Flag = SKR{D34D-B33F}
```
