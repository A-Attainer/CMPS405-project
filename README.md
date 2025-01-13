
# Project

## Summary

This project integrates shell scripting, network management, and multi-threaded programming to automate the setup of virtual machines and implement a robust chat server. The project is divided into two phases:

1. **Phase 01: Multi-VM Configuration and Automation**
   - Automates the creation and configuration of a Fedora Server and two Fedora Workstations.
   - Includes setting up SSH, Mosh, and NGINX for remote access and web hosting.
   - Supports user account creation with secure password generation, network mapping, and personal website management.
   - Automation scripts handle server and client configurations dynamically using CSV files.

2. **Phase 02: Multi-User Chat Server**
   - Implements a Java-based multi-user chat server with support for multiple rooms.
   - Features secure, ticket-based user identification and synchronized collections to prevent race conditions.
   - Allows clients to interact with the server and other users using defined commands.
   - Focuses on modular design, thread safety, and extensibility for future improvements.

---
## Team
 
 - Hamza Aljaji [ha2205541@student.qu.edu.qa]
 - Abubaker Elfagih [ae2205649@student.qu.edu.qa]
 - Obada Alrefai [oa2110207@student.qu.edu.qa]
 - Asem shouman [as2105001@student.qu.edu.qa]

---

## Challenges

### **Phase 01**
1. **Pushing and Pulling from GitHub**  
   Synchronizing scripts and configurations across multiple machines required frequent pushing and pulling from GitHub. This introduced challenges with version control, ensuring all changes were reflected across all devices, and managing conflicts when multiple users contributed to the same repository.

2. **Running Multiple Virtual Machines**  
   Running multiple VMs simultaneously on the same host strained hardware resources (CPU and RAM). This resulted in performance challenges such as slow responses and occasional VM crashes due to high memory utilization.

3. **Configuration Between Test Client and Server**  
   Setting up remote access (SSH, Mosh) and the web server (NGINX) required precise configuration of firewalls, permissions, and consistent environments. Ensuring seamless communication between server and client was time-consuming and required troubleshooting.

### **Phase 02**
1. **Understanding the Structure and Implementation**  
   Deciding the correct class structure for the chat server and identifying where to use threads was challenging. Ensuring thread-safe operations in multi-user environments and assigning responsibilities to the appropriate classes required careful planning.

2. **Command Implementation**  
   Understanding and implementing the commands (`join`, `leave`, `send`, `direct`, etc.) required collaboration to define their functionality and ensure they adhered to the server's rules and logic.

3. **Testing with Multiple Clients**  
   Running multiple clients simultaneously was difficult to achieve initially. The team resolved this by copying the client class and creating a test class for multiple instances, ensuring proper communication with the server.

4. **Collaborative Problem-Solving**  
   The team overcame these challenges by hosting live sessions on Discord, where they shared ideas, discussed logic, and collaboratively resolved issues.

---

## Issues

1. **Package Installation Failures**  
   Dependency issues or network restrictions occasionally caused package installation failures for critical tools such as `pwgen`, `nginx`, `mosh`, and `openssh`.

2. **VM Resource Constraints**  
   Hardware limitations caused performance issues when running multiple VMs. High memory or CPU utilization sometimes led to slow responses or VM crashes.

3. **Firewall and Network Configuration Issues**  
   Despite correctly setting up firewall rules, remote connections occasionally failed due to misconfigured IP addresses or routing, requiring additional troubleshooting.

---

## Contributions

| Member   |       Date | Description                       |
| :------- | ---------: | :-------------------------------- |
| Hamza    | 28-11-2024 | Worked on the server part with Abobaker |
| Obada    | 21-11-2024 | Worked on the client part with Assem    |
| Abobaker | 28-11-2024 | Worked on the server part with Hamza    |
| Assem    | 30-11-2024 | Worked on the client part with Obada    |

---

## References

- [What is SSH (Secure Shell)?](https://www.ssh.com/academy/ssh)
- [Map Interface in Java](https://www.geeksforgeeks.org/map-interface-java-examples)
- [LocalDate in java](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)
- 
