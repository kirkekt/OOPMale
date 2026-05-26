# OOPMale

A multiplayer chess project built using a client-server architecture.  
The project includes a custom chess server, client code to comunicate with the server, and an AI chess bot with adjustable difficulty built into the server.

## Features

+ Multiplayer over network
+ Separate server and client applications
+ Move validation handled on the server
+ Chess bot with configurable search depth
+ Support for multiple simultaneous clients
- Choosing what piece you want to promote you pawn into

---

## Setup

1. Download the repo.
2. Find the OOPMale/built folder.
3. There you can find folders where there are built versions of the server and the client.
4. Most parameters can be set in the according setup.txt files. The descriptor and value need to be seperated by an = and any amount of whitespace.
5. Start the server or choose a public server to connect to (you can connect to a server set up by us using the default values).
5. Start the client and join.
6. Enjoy!


## Architecture

The project is based on a client-server model:

- The server handles:
  - game logic
  - move validation
  - game state management
  - client communication
  - bot player

- The clients are responsible for:
  - user interface
  - sending player input to the server
  - displaying the game state

This design makes the system:
  - more secure
  - easier to maintain
  - easier to extend

---

## What we learned

During this project we gained experience in:

- network and socket programming
- client-server architecture design
- designing communication protocols
- structuring larger software projects
- front-end and back-end integration

One of the key lessons was that game logic should be placed on the server side rather than the client.

---

## Technologies

- Java
- Javafx
- Socket programming
- Object-Oriented Programming
- Client-server architecture

---

## Team members

- Artus Leo Marco Klemm (@Artus-lm)
- Kirke Karolin Tark (@kirkekt)
- Madis Roosma (@ma10-r)

Where

Artus
 Responsible for the whole of the client-server communication and leading the team (making sure that there was good communication between its members). He also set up the server everyone can play on now and later overhauled the board look.

Kirke 
 Mainly focused on the front-end and make everyting look pretty and functional. She also worked on some of the chess logic

Madis
 The mastermind behind the bot and most of the chess logic. He's the guy who made the pieces move the way they should

This was made as a part of the OOP course at the University of Tartu

---

## Chess piece graphics

Big thanks to

- https://x.com/dr_smey
- https://www.instagram.com/dr.smey
- https://www.reddit.com/r/PixelArt/comments/pmfegd/sets_of_chess_pieces/

for allowing us to use their chess piece artwork!