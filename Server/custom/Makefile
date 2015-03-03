ASM=as
LINK=ld

all: server.o
	$(LINK) -m elf_i386 -o server server.o
server.o:
	$(ASM) --32 server.asm -o server.o
clean:
	rm -f *.o *.out core.* server
