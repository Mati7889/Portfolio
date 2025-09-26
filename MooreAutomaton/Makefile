#compiler and linker flags
CFLAGS = -Wall -Wextra -Wno-implicit-fallthrough -std=gnu17 -fPIC -O2 -g
LDFLAGS = -shared -Wl,--wrap=malloc -Wl,--wrap=calloc -Wl,--wrap=realloc \
          -Wl,--wrap=reallocarray -Wl,--wrap=free -Wl,--wrap=strdup -Wl,--wrap=strndup

#source and object files
SRC = ma.c memory_tests.c list.c
OBJ = $(SRC:.c=.o)
LIB = libma.so

#default target
all: $(LIB)

#creating lib linking object files
$(LIB): $(OBJ)
	$(CC) -o $(LIB) $(OBJ) $(LDFLAGS)

#object file pattern rule
%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

#clean rule as a phony
.PHONY: clean

clean:
	rm -f $(OBJ) $(LIB)

#building and running the program with compiler flags and gcc compiler
#-L. tells the linker to look for libraries in the current directory.
#-lma tells the linker to link against the libma library
run:
	$(CC) $(CFLAGS) -o example ma_example.c -L. -lma
