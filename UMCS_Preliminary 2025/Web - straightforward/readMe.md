# Web - straightforward
## Solve
![image](https://github.com/user-attachments/assets/5dc306b7-4a6f-4480-b78d-2847da0dfb3c)

Let’s first access the website and see what we can denote: 

![image](https://github.com/user-attachments/assets/df174681-9bf3-42b2-841c-d6b5cd26b40f)

We see a landing page with a “Create Account” button. Let’s click it: 

![image](https://github.com/user-attachments/assets/421c3877-6071-4d44-bb26-77c029149c32)

Now we have an account creation page. Let’s create an account: 

![image](https://github.com/user-attachments/assets/11cf4f82-6dea-453f-ab3d-347630bb0df3)

Now have the ‘home page’ with a collect bonus button as well as redeem secret reward which costs $3000 and we have $1000 as of now. Let’s try getting the secret reward: 

![image](https://github.com/user-attachments/assets/29129b9c-ec83-4581-a969-88a7d88f8376)

![image](https://github.com/user-attachments/assets/3d84a6e7-910e-457d-936c-be6ed9e368e0)

Claiming the daily bonus, we get $2000, and if we attempt to claim again, it shows us an error message. So it seems like we’re stuck at $2000 and unable to get the “Secret Reward”. 

It also seems like we are not able to login to our account back after we logout as it should be, since it’s only a register button. So we now know that we have 1 chance in doing this. Let’s create a new account: 

![image](https://github.com/user-attachments/assets/2e88de8e-1922-4bb1-8075-abfb80219b4c)

Let’s check the downloadable zip file and see what we have: 

![image](https://github.com/user-attachments/assets/877407f6-0bea-46c5-b96b-40bb1a8e44f7)

As expected it’s a dummy source file of the original website with the folders being the ‘directory’ of the actual website. 

If we click into templates, we can see a ‘flag.html’ which leads to the flag display screen (with a dummy flag locally). This confirms that the flag is stored behind the ‘redeem secret reward’ button: 

![image](https://github.com/user-attachments/assets/35973b12-d6a7-449e-9d9b-643d3b090945)

![image](https://github.com/user-attachments/assets/1e5f2608-0021-4113-ac19-d77f0e14923d)

Now let’s look at the source code for the website in app.py: 

![image](https://github.com/user-attachments/assets/8116da0a-b849-4bc8-845c-82ac073f35a2)
![image](https://github.com/user-attachments/assets/9467055c-7300-4ec5-be59-d44ee48395a3)

Here we can see a init_db() database function as well as the register() function that works together with it. As it shows, if a user is already registered, it will not be able to re-create the account and prompting an error message as we’ve seen previously. 

What we’re more focused is this specific function of the code, the claim() function: 

![image](https://github.com/user-attachments/assets/9c04b156-2d03-41d0-9ee4-dfcff80b49dc)

From this, we can denote the following: 
- The claimed status is stored in a table named redemptions. 
- Before awarding a bonus, the app checks if claimed=1, then updates the DB. 
- But this is not atomic, this means if multiple requests hit the server quickly, they all might be able to pass the check before the DB finishes writing claimed=1. 

This leads us to an SQL Race Condition exploit which means By rapidly sending multiple POST requests to /claim, we might be able to trick the server in giving us multiple ‘daily bonus’ at once before the claimed = 1 is triggered and hopefully awarding us the x amount we want which is preferably >$2000. 

Let’s implement that theory. We came up with a simple code that does so: 
```
for (let i = 0; i < 10; i++) { 
  fetch("/claim", { 
    method: "POST", 
    credentials: "include" 
  }); 
} 
```

This code is a for loop that loops 10 times quickly, meaning we’ll be sending a POST request 10 times rapidly. The method: “POST” tells the server that it’s a form submission/action request. Then, the credentials: “include” means it includes the current session’s cookies so that the request is properly authenticated. If not, the server would reject the request. 

Let’s now go to inspect element > console and paste our code into it: 

![image](https://github.com/user-attachments/assets/082757be-f0d6-4d28-b519-dcc8a11047f0)

![image](https://github.com/user-attachments/assets/954c8ecd-a302-4841-a07c-7b31e593b591)

Success! We now have $6000 and can buy the flag: 

![image](https://github.com/user-attachments/assets/bd3207be-038b-47b7-98b6-3c1ad8383681)
```
Flag = UMCS{th3_s0lut10n_1s_pr3tty_str41ghtf0rw4rd_too!}
```
