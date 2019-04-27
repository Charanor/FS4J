[![Build Status](https://semaphoreci.com/api/v1/charanor/fs4j/branches/master/shields_badge.svg)](https://semaphoreci.com/charanor/fs4j)

# FS4J
An abstract file system for Java. The purpose of this project is to increase file I/O safety by isolating what directories users can read from and write to. 

## Why FS4J
Let's begin with a question: Why use FS4J? Well, there are several benefits:

* Provides an abstract file system, meaning a common interface in your code for file I/O.
* Simple and intuitive to use interface.
* Very flexible and expandable, easy to add new implementations of a file system.
* Comes with a ready-to-use implementation of a file system using `java.nio.*`. Minimal setup needed!

Even though these are nice bonuses the main reason to use FS4J is **safety**. By using FS4J you ensure that users of your program cannot access, read, or write to files that are supposed to be out of their reach, for example `C:\system32` or someones personal documents.

### Q: Can FS4J guarantee 100% file safety?
No, it can not. At least not without the developer doing their part and ensuring that **all user file I/O must go through FS4J.** We cannot guarantee safety for any I/O that does not pass through the FS4J interface, such as viruses.

### Q: What can FS4J **not** protect against?
* FS4J **cannot** protect against external programs (viruses, etc...). 
* FS4J **cannot** protect against file I/O that does not pass through the FS4J interface.
* FS4J **cannot** protect against programmer misuse (so read the wiki carefully!).

### Q: So what is an example of the main use case?
Let's say you have created a program that can load user-plugins or scripts. Perhaps a drawing tool that can load special brush scripts, or a video game that can load user mods. By using FS4J for all file I/O going in/out of those scripts/mods you can ensure that they cannot read, overwrite, or delete personal files on the end user's computer or (even worse) execute a virus script downloaded from the internet.

### Todo list:
- [x] Project outline
- [ ] Add several utility methods to the `FSFile` class
- [ ] Expand the file system to be able to handle more archive types (currently only `.zip`).
