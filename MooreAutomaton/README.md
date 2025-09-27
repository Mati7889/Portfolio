# Moore Automaton
Dynamic Moore Automata Library

This project implements a high-performance, dynamically loadable C library for simulating Moore automata, a fundamental type of deterministic finite automaton used in synchronous digital systems. The library supports large-scale simulations with advanced memory management and robust error handling.

## Overview:

- A Moore automaton is represented as a 6-tuple ⟨X, Y, Q, t, y, q⟩, where:

- X – set of input signal values

- Y – set of output signal values

- Q – set of internal states

- t : X × Q → Q – transition function

- y : Q → Y – output function

- q ∈ Q – initial state

This library focuses on binary automata, where:

- n input signals (1-bit each)

- m output signals (1-bit each)

- s-bit internal state

State, input, and output signals are stored in arrays of uint64_t for efficient bitwise operations. The library supports automata of arbitrary size, limited only by system memory.

## Features:

- Dynamic creation of automata with full or simplified configuration.

- Custom structures and lists for handling automata input and output connections.

- Flexible input/output wiring: connect outputs of one automaton to inputs of another.

- Synchronous, parallel step execution of multiple automata.

- Advanced memory management ensuring no leaks and consistent internal state.

- Optimized error handling: invalid parameters or memory allocation failures are adequately handled.

- Fully compatible with large-scale automata, unconstrained by artificial limits.

## Key functions:

- ma_create_full(...) – create a fully specified automaton.

- ma_create_simple(...) – create a simple automaton with identity output.

- ma_delete(...) – safely delete an automaton.

- ma_connect(...) / ma_disconnect(...) – connect or disconnect inputs and outputs.

- ma_set_input(...) / ma_set_state(...) – assign values to unconnected inputs or directly set the state.

- ma_get_output(...) – retrieve the current output signals.

- ma_step(...) – execute a single synchronous computation step across multiple automata.

For complete type definitions, see [ma.h](./ma.h)
