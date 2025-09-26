#include <stdio.h>
#include <stddef.h>
#include <stdint.h>
#include <stdlib.h>
#include "list.h"
#include "ma.h"
#include <errno.h>
#include <string.h>

/*
 * Author: Mateusz Gnat
 * This program is a dynamic library that simulates Moore automata.
 * It provides functionality to create, connect, and manipulate Moore machines.
 * The library supports managing input and output connections, transitioning between states
 * and computing outputs based on the current state and input.
 * The implementation is designed to handle multiple connected Moore machines
 */

//Resize define will be used for malloc/calloc/realloc size calculation
#define bits_to_len(x)  ((x + 63) / 64)

typedef void (*transition_function_t)(uint64_t *next_state, uint64_t const *input,
                                      uint64_t const *state, size_t n, size_t s);

typedef void (*output_function_t)(uint64_t *output, uint64_t const *state, size_t m, size_t s);

/*
 * Contains information about input bits.
 * Which input bit is connected to which automaton
 * and to which bit of this automaton it is connected.
 */
typedef struct input {
    struct moore *automaton;
    ssize_t bit;
} input_t;

/*
 * Automaton struct with two structs inside defined above.
 * Dynamic memory allocation.
 * Setting first values to starting values (-1 / NULL).
 * n – number of input signals.
 * m – number of output signals.
 * s – number of internal state bits.
 */
typedef struct moore {
    size_t n;
    size_t m;
    size_t s;

    transition_function_t transition;
    output_function_t output_func;

    uint64_t *state;
    uint64_t *input;
    uint64_t *output;

    input_t *input_connections;
    output_list_t **output_connections;
} moore_t;

/*
 * Allocates all memory resources required by the Moore automaton.
 * This includes buffers for state, input, output, and the connection structures.
 * If any allocation fails, all allocated memory is freed and the function returns -1.
 */
int moore_allocs(moore_t *moore, size_t n, size_t m, size_t s) {
    moore->input = calloc(bits_to_len(n), sizeof(uint64_t));
    moore->output = calloc(bits_to_len(m), sizeof(uint64_t));
    moore->state = calloc(bits_to_len(s), sizeof(uint64_t));

    moore->output_connections = malloc(sizeof(output_list_t *) * m);

    moore->input_connections = malloc(sizeof(input_t) * n);

    if (((!moore->input || !moore->input_connections) && n != 0)
        || !moore->output || !moore->state || !moore->output_connections) {
        errno = ENOMEM;
        free(moore->input);
        free(moore->output);
        free(moore->state);
        free(moore->input_connections);
        free(moore->output_connections);
        return -1;
    }

    for (size_t i = 0; i < n; i++) {
        moore->input_connections[i].automaton = NULL;
        moore->input_connections[i].bit = -1;
    }

    for (size_t i = 0; i < m; i++) {
        moore->output_connections[i] = NULL;
    }

    return 0;
}

/*
 * Updates the internal state of the automaton and recomputes its output.
 * The state is copied from a provided buffer and then the output function is called
 * to compute the new output value based on the current state.
 */
void output_step(moore_t *moore, uint64_t const *to_copy, size_t size) {
    memcpy(moore->state, to_copy, sizeof(uint64_t) * bits_to_len(size));

    moore->output_func(moore->output, moore->state, moore->m, moore->s);
}

/*
 * Creates a fully configurable Moore automaton.
 * Allows specifying the number of inputs, outputs, and state bits,
 * as well as providing the transition and output functions, and the initial state.
 * Returns a pointer to the created automaton or NULL on failure.
 */
moore_t *ma_create_full(size_t n, size_t m, size_t s, transition_function_t t,
                        output_function_t y, uint64_t const *q) {
    if (m == 0 || s == 0 || t == NULL || q == NULL || y == NULL) {
        errno = EINVAL;
        return NULL;
    }

    moore_t *moore = malloc(sizeof(moore_t));

    if (!moore) {
        errno = ENOMEM;
        return NULL;
    }

    moore->n = n;
    moore->m = m;
    moore->s = s;

    moore->transition = t;
    moore->output_func = y;

    if (moore_allocs(moore, n, m, s) != 0) {
        free(moore);
        errno = ENOMEM;
        return NULL;
    }

    output_step(moore, q, s);

    return moore;
};

//Identity function for create simple function.
void copy_state_output(uint64_t *output, const uint64_t *state, size_t m, size_t) {
    memcpy(output, state, sizeof(uint64_t) * bits_to_len(m));
}

/*
 * Creates a simple Moore automaton.
 * The output function returns the current state.
 * The user must provide a transition function and the number of input/state bits.
 * This function initializes the state to zero.
 */
moore_t *ma_create_simple(size_t n, size_t s, transition_function_t t) {
    if (s == 0 || t == NULL) {
        errno = EINVAL;
        return NULL;
    }

    uint64_t *first_state = calloc(bits_to_len(s), sizeof(uint64_t));

    if (!first_state) {
        errno = ENOMEM;
        return NULL;
    }

    moore_t *moore = ma_create_full(n, s, s, t, copy_state_output, first_state);

    free(first_state);

    return moore;
}

/*
 * Disconnects a range of input bits from their connected output sources.
 * Ensures that both ends of the connection (input and output) are properly updated,
 * removing any dangling links in the output automata.
 */
int ma_disconnect(moore_t *a_in, size_t in, size_t num) {
    if (a_in == NULL || num == 0 || in + num > a_in->n) {
        errno = EINVAL;
        return -1;
    }

    for (size_t i = 0; i < num; i++) {
        input_t *target = &a_in->input_connections[in + i];
        if (target->bit != -1) {
            remove_from_list(&(target->automaton->output_connections[target->bit]), a_in);

            target->bit = -1;
            target->automaton = NULL;
        }
    }
    return 0;
}

/*
 * Frees all memory associated with a Moore automaton.
 * This includes its state, input and output buffers, and all connection structures.
 * Also ensures that any automata connected to this one have their input connections cleared.
 */
void ma_delete(moore_t *a) {
    if (a == NULL) {
        return;
    }

    // Disconnects this automaton from any others that are receiving its outputs
    for (size_t i = 0; i < a->m; i++) {
        if (a->output_connections[i] != NULL) {
            output_list_t *current = a->output_connections[i];

            while (current != NULL) {
                current->automaton->input_connections[current->bit].bit = -1;
                current->automaton->input_connections[current->bit].automaton = NULL;

                output_list_t *del = current;
                current = current->next;
                free(del);
            }
        }
    }

    // Disconnects inputs
    ma_disconnect(a, 0, a->n);

    free(a->input);
    free(a->input_connections);
    free(a->output_connections);
    free(a->output);
    free(a->state);
    free(a);
}

/*
 * Connects outputs from one automaton to inputs of another.
 * If any connection already exists in the target we disconnect.
 * input range, it is disconnected before making the new link.
 */
int ma_connect(moore_t *a_in, size_t in, moore_t *a_out, size_t out, size_t num) {
    if (a_in == NULL || a_out == NULL || num == 0 || in + num > a_in->n || out + num > a_out->m) {
        errno = EINVAL;
        return -1;
    }

    ma_disconnect(a_in, in, num);

    for (size_t i = 0; i < num; i++) {
        a_in->input_connections[in + i].bit = out + i;
        a_in->input_connections[in + i].automaton = a_out;

        add_to_list(&(a_out->output_connections[out + i]), a_in, in + i);
    }

    return 0;
}

// Overrides input bits of an automaton manually.
int ma_set_input(moore_t *a, uint64_t const *input) {
    if (a == NULL || a->n == 0 || input == NULL) {
        errno = EINVAL;
        return -1;
    }

    for (size_t i = 0; i < a->n; i++) {
        if (a->input_connections[i].bit == -1) {
            if (input[i / 64] & (1ULL << (i % 64))) {
                a->input[i / 64] |= (1ULL << (i % 64));
            } else {
                a->input[i / 64] &= ~(1ULL << (i % 64));
            }
        }
    }
    return 0;
}


int ma_set_state(moore_t *a, uint64_t const *state) {
    if (a == NULL || state == NULL) {
        errno = EINVAL;
        return -1;
    }

    output_step(a, state, a->s);

    return 0;
}

uint64_t const *ma_get_output(moore_t const *a) {
    if (a == NULL) {
        errno = EINVAL;
        return NULL;
    }

    return a->output;
}

// Checks and copies connected bits from source to given automaton input using bit masks
void configure_input(moore_t *a) {
    for (size_t j = 0; j < a->n; j++) {
        ssize_t what_bit = a->input_connections[j].bit;


        if (what_bit != -1) {
            if ((a->input_connections[j].automaton->output[what_bit / 64]) & (
                    1ULL << (what_bit % 64))) {
                a->input[j / 64] |= (1ULL << (j % 64));
            } else {
                a->input[j / 64] &= ~(1ULL << (j % 64));
            }
        }
    }
}

/*
 * Performs a single simulation step for the Moore automaton.
 * This includes:
 *   - Reading input values from connected output automata (if any),
 *   - Computing the next state using the transition function,
 *   - Updating the state,
 *   - Computing the new output using the output function.
 */
int ma_step(moore_t *at[], size_t num) {
    if (at == NULL || num == 0) {
        errno = EINVAL;
        return -1;
    }

    for (size_t i = 0; i < num; i++) {
        if (at[i] == NULL) {
            errno = EINVAL;
            return -1;
        }
    }

    for (size_t i = 0; i < num; i++) {
        configure_input(at[i]);
    }

    for (size_t i = 0; i < num; i++) {
        uint64_t *next_state = calloc(bits_to_len(at[i]->s), sizeof(uint64_t));

        if (next_state == NULL) {
            errno = ENOMEM;
            return -1;
        }

        at[i]->transition(next_state, at[i]->input, at[i]->state, at[i]->n, at[i]->s);

        memcpy(at[i]->state, next_state, sizeof(uint64_t) * bits_to_len(at[i]->s));

        free(next_state);
    }

    for (size_t i = 0; i < num; i++) {
        at[i]->output_func(at[i]->output, at[i]->state, at[i]->m, at[i]->s);
    }

    return 0;
}
