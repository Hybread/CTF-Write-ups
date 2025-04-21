# Lucky Coin
## Introduction
I will flag this as 'Partial Revision' as during the competition itself, I was literally 1 step away from getting the flag but was stuck.

## Solve
Download the .zip file and extract the contents which contain an executable file named 'kaching'

When running the program it prompts us to input a 'coin':

![image](https://github.com/user-attachments/assets/0f779ad6-63ce-42eb-9c99-89b813359f42)

But when entered a dummy input, it will give an error text before closing the program

Let's plug this into a decompiler to analyse it. I used IDA for this one:

![image](https://github.com/user-attachments/assets/f1cc5d76-62ea-4dd6-91b0-b0abd496f604)

Looking around, we can't actually find anything to for a hint, but we can see a constant repetition of PYI, PYINSTALLER hinting that this program was compiled with pyinstaller.

With this, we have to decompile the program. But to do so we'll need a tool and for that, we'll be utilizing pyinstxtractor:

![image](https://github.com/user-attachments/assets/260afc29-c9c1-43e7-96ee-d10cbbbe1eeb)

![image](https://github.com/user-attachments/assets/a3ff8840-84d3-4484-ac21-82592f7e47f9)

---

Export the folder out onto our host machine and analyse the files within:

![image](https://github.com/user-attachments/assets/d5842c44-8b99-41ff-a385-134d84d52e32)

Right off the bat, we can see a coinchallenge.pyc which is most likely the main source file. We'll use PyLingual (online tool) to decompile the python source code within:
```Python
import os

def challenge():
    user_input = input('Please insert a coin to receive the flag: ')
    if random_translation(conceal(user_input)) == 'QIGizIoipGiJguoJijtIu':
        win()
    else:
        print('Your coin is rejected. Please try again')

def conceal(secretCoin):
    concealed = ''
    for char in secretCoin:
        concealed = concealed | chr(ord(char) + 2)
    return concealed

def random_translation(message):
    rot_random = str.maketrans('oAJWhKgSmpkVGuBHTLEcQlqCiwPyFaxNnvMRetOfsbDZUjrzXYId', 'ABCDEFGHIJKLMabcdefghijklmNOPQRSTUVWXYZnopqrstuvwxyz')
    message = message.translate(rot_random)
    return message

def win():
    flag = 'KQBS25{f9r98z8ngj9632224qwh432v095582u07t428553nx22nsv292g31304q2k0n67g}'
    os.system('cls || clear')
    print(f'Ka-ching! Here is the flag: {flag}\n\n')
    print("Wait, don't forget your secret key!(the key is your previous input)")
if __name__ == '__main__':
    challenge()
```
Now we have the source code to the program, within, we can find an encrypted flag as well as an encrypted "user_input", let's analyse the code and reverse it.
Breaking down the user_input():
- user_input() is passed through conceal() which adds 2 to each char.
- Then, it is passed through random_translation() which custom translation using str.maketrans(...).
- The final output must match: QIGizIoipGiJguoJijtIu

There seems to be encryption for the 'flag' itself. But if we look at the win() function, we can find print("Wait, don't forget your secret key!(the key is your previous input)"). This hints that the 'previous input' is the user_input which is the (coin). So it's hinting that it might be a Vigenere cipher.

Simply reverse the encryption of each encrypting function and we'll get this script:
```Python
def reverse_user_input(encrypted):
    src = 'oAJWhKgSmpkVGuBHTLEcQlqCiwPyFaxNnvMRetOfsbDZUjrzXYId'
    tgt = 'ABCDEFGHIJKLMabcdefghijklmNOPQRSTUVWXYZnopqrstuvwxyz'
    inverse_trans = str.maketrans(tgt, src)

    concealed = encrypted.translate(inverse_trans)

    original_input = ''.join(chr(ord(c) + 2) for c in concealed)

    return original_input

if __name__ == '__main__':
    ciphertext = 'QIGizIoipGiJguoJijtIu'
    secret_input = reverse_user_input(ciphertext)
    print(f"Coin: {secret_input}")
```

Run it and we get our "coin":

![image](https://github.com/user-attachments/assets/d361b2ec-2f4e-4c10-8c83-e58aeebb9c56)

---

Now, let's throw in our encrypted flag and the key(coin) into a Vigenere decoder and we get our flag:

![image](https://github.com/user-attachments/assets/34d69b6d-aacf-478e-a17f-0c3f5eff23c1)

---
```
Flag = ICTF25{a9d98f8adb9632224dfd432c095582a07c428553af22cec292e31304c2c0a67b}
```
