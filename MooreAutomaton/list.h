#ifndef LIST_H
#define LIST_H

#include <stddef.h>
#include <stdint.h>

struct moore; // Forward declaration of an automaton
typedef struct moore moore_t;

/*
 * Holds the information about automata connected to current output.
 * Information about connections from a certain output bit.
 * Lists pairs of (target automaton, input bit number of that automaton).
 * Each node has a pointer to automata and connected bit inside.
 */
typedef struct output_list {
    struct output_list *next;
    struct moore *automaton;
    size_t bit;
} output_list_t;


// Output list functions
void remove_from_list(output_list_t **a, struct moore *to_remove);
void add_to_list(output_list_t **a, struct moore *to_add, size_t bit);

#endif // LIST_H
