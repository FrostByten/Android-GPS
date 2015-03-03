#! ----------------------------------------- !#
#! | XML GPS location server			   | !#
#! | -- Receives XML formatted info  	   | !#
#! | -- from clients with GPS data   	   | !#
#! | -- and appends it to a master file	   | !#
#! | 							   	       | !#
#! | Design by: Lewis Scott				   | !#
#! | Programming by: Lewis Scott		   | !#
#! |									   | !#
#! | Date: March 3rd, 2015				   | !#
#! ----------------------------------------- !#

# Symbols
.equiv port_num, 8088
.equiv socket_reg, %ebp
.equiv buff_size, 1024

.bss
	file: .int 0
	client_socket: .int 0
.text

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
	try_file

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
		fork
.endm

# Check arguments and try to get file descriptor
.macro try_file
	movl 0(%esp),%eax
	cmp $2,%eax
	jl usage
	jmp after
	
	usage:
		print_message $usage_msg,$usage_len
		exit $1
		
	after:
		movl $2,%ecx		# O_RDWR
		movl 8(%esp),%ebx	# argv[1]
		movl $5,%eax		# open
		int $0x80			# systemcall
		
		cmp $0,%eax
		jl fnf
		jmp done
		
	fnf:
		print_message $fnf_msg,$fnf_len
		exit $1
		
	done:
		mov %eax,(file)
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

# Set the stack to be able to re-use the address we bind the socket to
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

# Bind the socket to the port
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

# Listen on the socket
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
		
		mov %eax,(client_socket)# Get the returned connection
		
		check_err
		
		pop_regs
.endm

# Fork the process
.macro fork
	movl $2,%eax 	 # fork
	int $0x80		 # systemcall
	
	cmp $0,%eax
	jg parent
	jz child
	check_err
	
	parent:
		#movl (client_socket),%ebx
		#movl $6,%eax				# close
		#int $0x80					# systemcall
		jmp begin_loop
	
	child:
		child_loop
		exit 0
.endm

.macro child_loop
	.bss
		bytes_read: .int 0
		buffer: .fill buff_size, 1, 0 # Create a buffer and zero it
	.text
	
	request_parent_kill:
		movl $1,%ecx		# SIGHUP
		movl $1,%ebx		# PR_SET_PDEATHSIG
		movl $72,%eax		# SYS_PRCTL
		int $0x80			# systemcall
	
		movl (client_socket), socket_reg
		
	size_read_loop:
		movl $4,%edx
		subl bytes_read,%edx			# read sizeof(int) - bytes_read
		
		cmp $0,%edx
		jle data_read_loop				# if <= 0, continue to data reading
		
		movl buffer,%ecx
		movl (client_socket),%ebx
		movl $3,%eax					# read
		int $0x80						# systemcall
		
		check_err
		addl %eax,bytes_read			# increase by the number of bytes we read
		
		jmp size_read_loop
		
	data_read_loop:
		# read the data
		jmp handle_data
		
	handle_data:
		# deal with the data
		jmp size_read_loop 				# loop
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
	movl $file,%ebx
	movl $6,%eax		# close
	int $0x80			# syscall

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

# Create a new allocated variable
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
		.ascii "Accepted a new client!\n"
		client_len = . - client_msg
	error_msg:
		.ascii "An error occured... Exiting!\n"
		error_len = . - error_msg
	ready_msg:
		.ascii "Server ready, listening...\n"
		ready_len = . - ready_msg
	usage_msg:
		.ascii "Usage: ./server {file path}\n"
		usage_len = . - usage_msg
	fnf_msg:
		.ascii "File not found...\n"
		fnf_len = . - fnf_msg
