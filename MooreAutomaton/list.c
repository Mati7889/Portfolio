#include <stdlib.h>
#include <errno.h>
#include "list.h"

/*
 * Removes or adds a given automaton to the linked list of output connections.
 * Used when disconnecting or connecting an input to an output, ensuring proper cleanup
 * and preventing dangling connections.
 */

void add_to_list(output_list_t **a, moore_t *to_add, size_t bit) {
    output_list_t *new_node = malloc(sizeof(output_list_t));
    if (!new_node) {
        errno = ENOMEM;
        return;
    }

    new_node->automaton = to_add;
    new_node->bit = bit;

    new_node->next = *a;

    *a = new_node;
}

void remove_from_list(output_list_t **a, moore_t *to_remove) {
    if (a == NULL || *a == NULL) return;

    output_list_t *current = *a;

    if (current->automaton == to_remove) {
        *a = current->next;
        free(current);
        return;
    }

    while (current->next != NULL) {
        if (current->next->automaton == to_remove) {
            output_list_t *to_delete = current->next;
            current->next = to_delete->next;
            free(to_delete);
            return;
        }
        current = current->next;
    }
}

