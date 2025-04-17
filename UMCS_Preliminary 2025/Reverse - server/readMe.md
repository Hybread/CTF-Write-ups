# Reverse - server
## Solve

![image](https://github.com/user-attachments/assets/8135d8c1-0b6a-47f3-b86e-02b69ed02dd6)

First, we download the provided file named ‘server.unkown’ as well as accessing the remote ip address given. Then, We put the server.unknown file into kali to identify what type of file it is before our actual analysis: 

![image](https://github.com/user-attachments/assets/6e8565d0-924c-4f7a-8a2b-f484f7db56dc)

With the ‘file’ command we can see that it is a regular ELF file and the .unkown was just a fake ‘format’ to throw us off. 
If we try to access the remote port, nothing comes up (or so I thought, you're supposed to send a HTTP request manually at that time so I just closed the connection without thinking twice about it): 

![image](https://github.com/user-attachments/assets/aea8c05c-db21-4d87-9260-94974301f412)

Then, when we try to run the program locally we get this: 

![image](https://github.com/user-attachments/assets/f7a5fcdd-30d5-41d4-8ace-db615eb038d7)

Now we can assume that in the actual remote port, to we need the IP address and Socket to bind(?) 
Let’s decompile this ELF file with a decompiler. We will be using IDA: 

![image](https://github.com/user-attachments/assets/1c43de4f-7cee-42b6-bc42-49bfe8a8a491)

Let’s click into the main function to see if we get a decompiled version of the main function: 

![image](https://github.com/user-attachments/assets/1f79403b-0f69-4d5c-854d-e1b4b47d9afc)

Here we can see a hard coded IP of “10.128.0.27” which tells us that our guess of the solve is wrong as this is just to throw us off, and it’s why we got the error message of “socket and ip did not bind” 

Anyways, let’s set this aside and take a look at something we forgot to do initally. The strings section: 

![image](https://github.com/user-attachments/assets/429b3255-7e82-44d7-b5f3-379a6dde7d5c)

![image](https://github.com/user-attachments/assets/86e1686c-0051-4e5c-950b-7b258ec13c03)

Clicking into the string with GET, we see that if the proper request is sent and accepted, we will be able to acces /flag and get something like: 

```
HTTP/1.1 200 OK  
Content-Type: text/plain
(supposedly the flag)
```

And if it's wrong, it will show:
```
HTTP/1.1 404 Not Found
Content-Type: text/plain
Not here buddy
```

Something more interesting we see if the GET request for “/goodshit/umcs_server HTTP/13.37”. This looks like a potential hint for where we must send our GET request to. So let’s try it with this echo command towards the server:
```
echo -e "GET /goodshit/umcs_server HTTP/13.37" http://34.133.69.112:8080
```

![image](https://github.com/user-attachments/assets/3f1c5bb0-57ed-4a6d-8aad-f092bdde5661)

Success! We got our flag from the server 
```
Flag = umcs{http_server_a058712ff1da79c9bbf211907c65a5cd}
```
