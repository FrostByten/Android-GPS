#! ------------------------------------- !#
#! | XML GPS location server		   | !#
#! | -- Receives XML formatted info    | !#
#! | -- from clients with GPS data     | !#
#! | -- and appends it to a master file| !#
#! | 							       | !#
#! | Design by: Lewis Scott			   | !#
#! | Programming by: Lewis Scott	   | !#
#! |								   | !#
#! | Date: March 3rd, 2015			   | !#
#! ------------------------------------- !#

# Symbols
.equiv port_num, 8088
.equiv socket_reg, %ebp

# Main macro
.macro server
		.globl _start
		.weak _start
	_start:
		.globl main
	main:
		setup
		loop
.endm

# Setup the socket and listen on it
.macro setup
	print_message $loading_msg,$loading_len

	open_socket
	set_reuse
	bind
	listen
	
	print_message $ready_msg,$ready_len
.endm

# The main loop
.macro loop
	begin_loop:
	
	accept
	print_message $client_msg,$client_len
	jmp begin_loop
	#fork
.endm

# Open a socket
.macro open_socket
	push_regs
	
	newvar socket_args, .long 2, 1, 0 # PF_INET, SOCK_STREAM
	
	movl $socket_args,%ecx
	movl $1,%ebx 			# SYS_SOCKET
	movl $102,%eax 			# socketcall
	int $0x80				# syscall
	
	check_err
	movl %eax, socket_reg
	
	pop_regs
.endm

.macro set_reuse
	push_regs
	
	newvar true, .int 1
	#SOL_SOCKET, SO_REUSEADDR, true, length of socket
	newvar sockopt_args, .long 0, 1, 2, true, 4 # reuse option
	
	movl socket_reg,(sockopt_args) # Append the socket address to the argument list
	
	movl $sockopt_args,%ecx
	movl $14,%ebx				# SYS_SETSOCKOPT
	movl $102,%eax				# socketcall
	int $0x80					# syscall
	
	check_err
	
	pop_regs
.endm

.macro bind
	.data
		address: .short 2 							  # AF_INET
				 .byte port_num >> 8, port_num & 0xff # convert byte order
				 .int 0								  # any address
	.text
		push_regs
	
		newvar sockbind_args, .long 0, address, 16 # 16 = sizeof(sockaddr_in)
		
		movl socket_reg,(sockbind_args) # Append the socket address to the argument list
		
		movl $sockbind_args,%ecx
		movl $2,%ebx					 # SYS_BIND
		movl $102,%eax					 # socketcall
		int $0x80						 # systemcall
		
		check_err
	
		pop_regs
.endm

.macro listen
	push_regs
	
	newvar socklisten_args, .long 0, 5 # Max backlog
	
	movl socket_reg,(socklisten_args) # Append the socket address to the argument list
	
	movl $socklisten_args,%ecx
	movl $4,%ebx				# SYS_LISTEN
	movl $102,%eax				# socketcall
	int $0x80					# systemcall
	
	check_err
	
	pop_regs
.endm

# Accept a connection
.macro accept
	.bss
		sockaddr: .fill 16, 1, 0 # 16 = sizeof(sockaddr_in)
		sockaddr_len: .int 0
	.text
		push_regs
		
		newvar accept_args, .long 0, sockaddr, sockaddr_len
		
		movl socket_reg,(accept_args) # Append the socket address to the argument list
		
		movl $accept_args,%ecx
		movl $5,%ebx			# SYS_ACCEPT
		movl $102,%eax			# socketcall
		int $0x80				# systemcall
		
		check_err
		
		pop_regs
.endm

# Fork the process
.macro fork
	movl $FORK, %eax # fork
	int $0x80		 # systemcall
	
	cmp $0,%eax
	jg parent
	jz child
	checkerr
	
	parent:
		
	
	child:
		
	
.endm

# Check the return of a system call for error
.macro check_err
	push_regs
	
	cmp $0,%eax
	jl error_occured
	
	pop_regs
.endm

# Print a message to stdout
.macro print_message message, length
	push_regs # Save the register values
	
	movl \length,%edx
	movl \message,%ecx
	movl $1,%ebx		# stdout
	movl $4,%eax		# write
	int $0x80			# syscall
	
	pop_regs # Restore the register values
.endm

# Exit the program
.macro exit return
	movl \return,%ebx
	movl $1,%eax		# exit
	int $0x80			# syscall
.endm

# Save the registers on the stack
.macro push_regs
	push %edx
	push %ecx
	push %ebx
	push %eax
.endm

# Recover the registers from the stack
.macro pop_regs
	pop %eax
	pop %ebx
	pop %ecx
	pop %edx
.endm

.macro newvar var, contents:vararg
	.data
	\var: \contents
	.previous
.endm

# Run the main macro
.text
	server

error_occured:
		print_message $error_msg,$error_len
		exit $1
		
.data
	loading_msg:
		.ascii "Loading server...\n"
		loading_len = . - loading_msg
	client_msg:
		.ascii "Got a client!\n"
		client_len = . - client_msg
	error_msg:
		.ascii "An error occured... Exiting!\n"
		error_len = . - error_msg
	ready_msg:
		.ascii "Server ready, listening...\n"
		ready_len = . - ready_msg
