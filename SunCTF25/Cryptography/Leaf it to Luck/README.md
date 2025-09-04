# Leaf it to Luck

<img width="612" height="425" alt="image" src="https://github.com/user-attachments/assets/57d60569-d984-4530-9c85-03a6525c536c" />

## Solve

Download the given file which is named "clover.txt" and inspect the content:

<img width="1440" height="187" alt="image" src="https://github.com/user-attachments/assets/03d6db2e-15c1-429b-88a8-039b6d430a42" />

We get this string of characters which seem to be in base64 encoding:
```
NjYgMTQ2IDQwIDY3IDYxIDQwIDY2IDE0MSA0MCA2NyA3MSA0MCA2NyA2MCA0MCA2NiA2MiA0MCA2MyA2MiA0MCA2MyA2NSA0MCA2NyAxNDIgNDAgNjcgNjAgNDAgNjYgNjQgNDAgNjMgNjMgNDAgNjUgMTQ2IDQwIDY2IDYzIDQwIDYzIDYwIDQwIDYzIDYwIDQwIDY3IDE0MSA0MCA2NSAxNDYgNDAgNjMgNjEgNDAgNjcgNjEgNDAgNjcgNzEgNDAgNjYgNjcgNDAgNjUgMTQ2IDQwIDY2IDcwIDQwIDYzIDYzIDQwIDYzIDY0IDQwIDY2IDYyIDQwIDY3IDE0NA==
```

Plug the string into CyberChef and run it through base64, octal then hex and it decodes into this:

<img width="884" height="542" alt="image" src="https://github.com/user-attachments/assets/0fd7a54d-a8b6-437e-b908-32825645af2f" />

```
oqjypb25{pd3_c00z_1qyg_h34b}
```

It's still encrypted so after running it through Dcode's cipher identifier, we can see that it has a highest possibility of being the ROT cipher:

<img width="980" height="397" alt="image" src="https://github.com/user-attachments/assets/833bb1f0-7a30-4a67-8fbd-694a3391bb7b" />

With the ROT brute force, we're able to extract the flag from the ciphertext:
```
Flag = sunctf25{th3_g00d_1uck_l34f}
```
