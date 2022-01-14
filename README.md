# taiga_telegram_bot

# All changes in private.

After first launch application will generate config file (config.json) and exit. Just configure this file and launch again.

```
{
  "chat_ids": "", // Can be empty.
  "admins": "userid1,userid2,userid3", // Who have admin rights. You can add multiple admins.
  
  "tgbot_id": "", // Your telegram bot ID.
  "tgbot_token": "", // Your telegram bot token.
  
  "username": "", // Your taiga username.
  "password": "", // Your taiga password.
  
  "ready_category": "", // Taiga category for ready tasks.
  "update_timer": 3600000, // Taiga will send alerts every this milliseconds. Default is 1 hour.
  
  "site_address": "", // Your taiga site address. Can be empty.
  "api_address": "" // Your taiga api address. By default it's in "https:/yoursite.com/api/v1" format.
}
```

Use /subscribe command in needed chat for subscribe to taiga alerts. Use "/subscribe cancel" for unsubscribe. You need to have admin right for this.

Use /alert command for instant alert for tests. You need to have admin right for this.
