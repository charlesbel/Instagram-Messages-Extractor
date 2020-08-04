# Instagram Messages Extractor - Android No Root
 A demo of what we can do with an Accessibility Service on Android, like here, we can extract Instagram Messages withtout rooting the phone. Here it is not very serious, but think that someone can inject this code in an app and spy your chats.

# Use it
You'll have to activate the accesibility service of the app in your Android settings. After that, each time you will open an instagram chat, the service will Toast and Log the all the messages shown on the screen with some metadatas like the interlocutor, the time when the message were sent and also if the message is incoming or outgoing.

# How it works
- The Message : each time the content change on com.instagram.android, the service will get all the content of the screen, so with some filters, the program extract the message
- The Date and Time : this is pretty simple, the app filter each elements and identify with a patern if it is a date. If it is, it is the date and time of the previous message
- The Interlocutor : very simple, his name is in a static place, so just get the name by navigating with .getParent() and .getChild()
- Incoming or Outgoing : not the simpliest way, but working, we just get the width position of the left side of the message, we devide the screen width by 5,5 if the width position of the left side of the message is < than the screen width devided 5,5, the message is incoming, and reverse.
