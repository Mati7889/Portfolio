# Arbitrary-Precision Square Root

This assembly x86-64 project implements an **iterative, bit-level algorithm** to compute the integer square root of a non-negative number `X` **up to 256 000 bits**  represented in binary form across multiple 64-bit words. For a given `2n`-bit input `X`, the algorithm computes an `n`-bit integer `Q` so that `Q^2 ≤ X < (Q+1)^2`.


