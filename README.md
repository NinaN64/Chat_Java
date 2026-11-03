## Chat application
 
### Main goal
A simple multi-client chat room in Java. One server accepts many clients over TCP (default port 2020), and every message a client sends is broadcast to everyone in the room.
 
I made this project to learn how sockets and threads work, and how to deploy a real server.
 
### Features
- Many clients can talk at the same time
- Nicknames with `/nick <name>`
- List of people in the room with `/who`
- Join and leave messages
- Runs on a Linux server all the time
### Commands
- `/nick <name>` change your nickname (2-16 letters or numbers, must be free)
- `/who` show who is online (only you see the answer)
- `/quit` leave the chat
### Build
You need Java 17 or newer. Run this in the project folder.
 
```
find src -name "*.java" > sources.txt
javac --release 17 -d out @sources.txt
jar --create --file server.jar --main-class io.ninan.chat.server.ServerMain -C out .
jar --create --file client.jar --main-class io.ninan.chat.server.ClientConnection -C out .
```
 
On Windows PowerShell the first line is different:
 
```
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } | Out-File -Encoding ascii sources.txt
```
 
### Run the server
```
java -jar server.jar
```
 
### Run a client
```
java -jar client.jar [host] [port]
```
 
(Both arguments are optional. The defaults are localhost and 2020.)
 
Example:
```
java -jar client.jar 203.0.113.10 2020
```
 
### How it works
- `ServerMain` listens on port 2020 and starts a new thread for every client
- `ServerThread` reads the messages of one client and handles the commands
- Every message goes to all clients through `broadcast`
- A nickname is claimed in one step with a `ConcurrentHashMap`, so two people can't take the same name at the same time
- `ClientConnection` is the client. One thread prints what the server sends, and the main thread sends what you type
### Deployment
The server runs on an Oracle Cloud Always Free VM (Ubuntu). It is a raw TCP server, not HTTP, so I needed a VM that lets me open my own port.


### Known limits
- Messages are not encrypted and there is no login, so don't write anything private
- One slow client can slow down the broadcast for the others
### Ideas for later
- Private messages (`/msg`)
- A queue for every client, so one slow client can't block the others
- TLS encryption
- Maven, unit tests and automatic deploy with GitHub Actions